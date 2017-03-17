package com.rkalyankumar;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by kramases on 11/4/16.
 */
@RestController
@Slf4j
public class AsyncResponseController {

    public static final long BASE_SLEEP_TIME = 1000;

    private int numRequests = 0;

    @AllArgsConstructor
    @EqualsAndHashCode
    class SseEmitterTracker {
        int numRequests;
        SseEmitter sseEmitter;
    }

    private final Map<String,SseEmitterTracker> emitterMap = new ConcurrentHashMap<>();

    @RequestMapping("/events")
    @CrossOrigin(origins = "http://localhost:8081")
    public SseEmitter sseEmitter(@RequestParam("id") String id) {

        SseEmitter sseEmitter = null;

        if (!emitterMap.containsKey(id))
            emitterMap.put(id,new SseEmitterTracker(0,new SseEmitter(10000L)));

        sseEmitter = emitterMap.get(id).sseEmitter;

        sseEmitter.onCompletion(() -> {
            if (emitterMap.get(id) != null) {
                emitterMap.get(id).sseEmitter.complete();
                emitterMap.remove(id);
            }
        } );
        sseEmitter.onTimeout(()-> {
            if (emitterMap.get(id) != null) {
                emitterMap.get(id).sseEmitter.complete();
                emitterMap.remove(id);
            }
        });

        StupidThread stupidThreads[] = new StupidThread[3];
        ExecutorService executorService = Executors.newFixedThreadPool(stupidThreads.length);
        stupidThreads[0] = new StupidThread(sseEmitter,this, BASE_SLEEP_TIME);
        stupidThreads[1] = new StupidThread(sseEmitter,this, BASE_SLEEP_TIME * 3);
        stupidThreads[2] = new StupidThread(sseEmitter,this, BASE_SLEEP_TIME * 3 * 3);
        numRequests = 3;
        for (StupidThread stupidThread : stupidThreads) {
            log.info("Running a thread ..");
            executorService.execute(stupidThread);
        }
        executorService.shutdown();
        return sseEmitter;
    }
}