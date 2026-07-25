package com.quickboot.application.service;

import com.quickboot.domain.model.HealthStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HealthService {
    public HealthStatus check() {
        return new HealthStatus("UP", "quickboot", LocalDateTime.now());
    }
}
