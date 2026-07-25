package com.quickboot.web.controller;

import com.quickboot.application.service.HealthService;
import com.quickboot.common.api.ApiResponse;
import com.quickboot.domain.model.HealthStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
public class HealthController {
    private final HealthService healthService;

    public HealthController(HealthService healthService) {
        this.healthService = healthService;
    }

    @GetMapping("/health")
    public ApiResponse<HealthStatus> health() {
        return ApiResponse.success(healthService.check());
    }
}
