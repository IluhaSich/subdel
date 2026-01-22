package com.example.subdel.service;
import com.example.events_contract.events.DelicacyAnalyzedEvent;
import com.example.grpc.analytics.*;
import com.example.subdel.config.RabbitMQConfig;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
public class DelicacyAnalyticsService {

    @GrpcClient("analytics-service")
    private DelicacyAnalyticsServiceGrpc.DelicacyAnalyticsServiceBlockingStub analyticsStub;

    private final RabbitTemplate rabbitTemplate;

    public DelicacyAnalyticsService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void analyze(Long delicacyId) {
        var request = DelicacyAnalyticsRequest.newBuilder()
                .setDelicacyId(delicacyId)
                .build();

        var response = analyticsStub.analyzeDelicacy(request);

        var event = new DelicacyAnalyzedEvent(
                response.getDelicacyId(),
                response.getScore(),
                response.getVerdict()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ANALYTICS_FANOUT,
                "",
                event
        );
    }
}