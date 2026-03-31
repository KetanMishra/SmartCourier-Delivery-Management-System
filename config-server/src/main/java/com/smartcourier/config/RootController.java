package com.smartcourier.config;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping("/")
    public Map<String, Object> root() {
        return Map.of(
                "service", "config-server",
                "status", "UP",
                "message", "Spring Cloud Config Server is running.",
                "endpoints", Map.of(
                        "health", "/actuator/health",
                        "exampleConfig", "/auth-service/default"
                )
        );
    }
}