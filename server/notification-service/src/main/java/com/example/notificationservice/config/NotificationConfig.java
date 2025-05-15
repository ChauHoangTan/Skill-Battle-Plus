package com.example.notificationservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationConfig {
    public static final String NOTIFICATION_EXCHANGE = "notification-exchange";
    public static final String EMAIL_QUEUE = "email-queue";
    public static final String PUSH_QUEUE = "push-queue";
    public static final String ROUTING_KEY_EMAIL = "notify.email";
    public static final String ROUTING_KEY_PUSH = "notify.push";

    // Exchange
    @Bean
    public TopicExchange notificationExchange() {
        return new TopicExchange(NOTIFICATION_EXCHANGE);
    }

    // Email queue
    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    // Push queue
    @Bean
    public Queue pushQueue() {
        return new Queue(PUSH_QUEUE, true);
    }

    // Bindings
    @Bean
    public Binding bindEmailQueue(Queue emailQueue, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(emailQueue)
                .to(notificationExchange)
                .with(ROUTING_KEY_EMAIL);
    }

    @Bean
    public Binding bindPushQueue(Queue pushQueue, TopicExchange notificationExchange) {
        return BindingBuilder
                .bind(pushQueue)
                .to(notificationExchange)
                .with(ROUTING_KEY_PUSH);
    }
}
