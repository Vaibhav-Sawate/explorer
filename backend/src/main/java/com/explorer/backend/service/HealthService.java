package com.explorer.backend.service;

import com.explorer.backend.dto.HealthResponse;
import org.springframework.stereotype.Service;

//TOPIC: Dependency Injection and the Spring IoC container
@Service     //tells this class is at service layer - creates bean and injects the service
public class HealthService {

    public HealthResponse getHealth() {
        return new HealthResponse(
                "Explorer",
                "UP"
        );
    }

}
