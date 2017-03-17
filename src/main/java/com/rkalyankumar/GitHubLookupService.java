package com.rkalyankumar;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class GitHubLookupService {

    RestTemplate restTemplate = new RestTemplate();

    @Async
    public CompletableFuture sample() throws InterruptedException {
        return CompletableFuture.completedFuture("Result");
    }
    
    @Async
    public CompletableFuture<User> findUser(String user, SseEmitter sseEmitter) throws InterruptedException, IOException {
        System.out.println("Looking up " + user);
        User results = restTemplate.getForObject("https://api.github.com/users/" + user, User.class);
        sseEmitter.send(results);
        // Artificial delay of 1s for demonstration purposes
        
        return CompletableFuture.completedFuture(results);
    }

}
