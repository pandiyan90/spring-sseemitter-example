package com.rkalyankumar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Created by kramases on 11/4/16.
 */
@Slf4j
public class StupidThread implements Runnable {

    private final SseEmitter responseBodyEmitter;
    private long sleepTime;
    private final AsyncResponseController controller;

    public StupidThread(SseEmitter responseBodyEmitter,AsyncResponseController controller, long sleepTime) {
        this.responseBodyEmitter = responseBodyEmitter;
        this.controller = controller;
        this.sleepTime = sleepTime;
    }

    public void run() {
        try {
            log.info("Sleeping for {} ms.", sleepTime);
            Thread.sleep(sleepTime);
            responseBodyEmitter.send("I am a message from the StupidThread, my timestamp is: "
                    + System.currentTimeMillis() + "\r\n");
        } catch (InterruptedException ex) {
            log.error("Thread sleep interrupted {}",ex.getLocalizedMessage(),ex);
        } catch (Exception e) {
            log.error("Exception occured {}",e.getLocalizedMessage(),e);
        }
    }
}
