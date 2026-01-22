package com.example.subdel.controller;

import com.example.subdel.service.DelicacyAnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analytics")
public class DelicacyAnalyticsController {

    private final DelicacyAnalyticsService delicacyAnalyticsService;

    public DelicacyAnalyticsController(DelicacyAnalyticsService delicacyAnalyticsService) {
        this.delicacyAnalyticsService = delicacyAnalyticsService;
    }

    /**
     * Запуск аналитики деликатеса
     * 1) gRPC -> analytics-service
     * 2) Fanout -> RabbitMQ
     */
    @PostMapping("/delicacy/{id}")
    public ResponseEntity<String> analyzeDelicacy(@PathVariable Long id) {
        delicacyAnalyticsService.analyze(id);

        return ResponseEntity.ok(
                "Analytics requested for delicacy " + id
        );
    }
}
