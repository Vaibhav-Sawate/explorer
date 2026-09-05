package com.explorer.backend.controller;

import com.explorer.backend.dto.HealthResponse;
import com.explorer.backend.service.HealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/api/health")
    public HealthResponse health(){
        return healthService.getHealth();
    }


//    @GetMapping("/api/health")
//    public HealthResponse health() {
//        return new HealthResponse(
//                "Explorer",
//                "UP");
//    }


//    public String health() {
//        return "Explorer is up and running!@";
//    }
//    Note: But APIs return structured data hence We can use Map

//    public Map<String, String> health() {        //Spring converts it to JSON
//        return Map.of("status", "UP",
//                "application", "Explorer");
//    }
    //Spring Boot uses an HTTP message converter (typically backed by Jackson) to serialize Java objects into JSON.

    //Note: but when the keys are in large number we use DTOs: Data Transfer Objects




}
