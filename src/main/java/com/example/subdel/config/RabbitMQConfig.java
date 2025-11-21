package com.example.subdel.config;

import com.example.subdel.service.DelicacyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private static final Logger log = LoggerFactory.getLogger(RabbitMQConfig.class);

    public static final String EXCHANGE_NAME = "delicacy-exchange";
    public static final String ROUTING_KEY_BOOK_CREATED = "delicacy.created";

    @Bean
    public TopicExchange delicacyExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }
}
