package com.rkalyankumar;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@Slf4j
public class CompletableFutureExample {

    @Autowired
    GitHubLookupService gitHubLookupService;

    @RequestMapping("/mine")
	SseEmitter mine() throws InterruptedException, IOException {
	    final SseEmitter sseEmitter = new SseEmitter();
	    notifyProgress(sseEmitter);
	    notifyProgress(sseEmitter);
	    final List<CompletableFuture> futures = new ArrayList();

        CompletableFuture page1 = gitHubLookupService.findUser("PivotalSoftware", sseEmitter);
        futures.add(page1);
        CompletableFuture page2 = gitHubLookupService.findUser("CloudFoundry", sseEmitter);
        futures.add(page2);
        CompletableFuture page3 = gitHubLookupService.findUser("Spring-Projects", sseEmitter);
        futures.add(page3);

        futures.forEach(future ->
	            future.thenRun(() -> {
					try {
						notifyProgress(sseEmitter);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}));
	    final CompletableFuture[] futuresArr = futures.toArray(new CompletableFuture[futures.size()]);
	    CompletableFuture
	            .allOf(futuresArr)
	            .thenRun(sseEmitter::complete);
	    return sseEmitter;
	}

	private void notifyProgress(SseEmitter sseEmitter) throws InterruptedException {
	    try {
	        sseEmitter.send(1);
	        Thread.sleep(2000);
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    }
	}

}