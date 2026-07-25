package com.quickboot.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatus {
    private String status;
    private String application;
    private LocalDateTime checkedAt;
}
