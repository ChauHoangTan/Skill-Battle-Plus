package com.example.questionservice.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MessageQueueConfig {
    public static final String QUESTION_EXCHANGE = "question-exchange";
    public static final String QUESTION_DLX_EXCHANGE = "question-exchange-dlx";
    public static final String QUESTION_QUEUE = "question-queue";
    public static final String RETRY_QUEUE = "retry-queue";
    public static final String FAIL_QUEUE = "fail-queue";
    public static final String ROUTING_KEY_QUESTION_CREATE = "question.create";
    public static final String ROUTING_KEY_QUESTION_UPDATE = "question.update";
    public static final String ROUTING_KEY_QUESTION_DELETE = "question.delete";
    public static final String ROUTING_KEY_QUESTION_FAILED = "question.failed";
    public static final String ROUTING_KEY_QUESTION_DLX = "question.dlx";

    // Exchange
    @Bean
    public TopicExchange questionExchange() {
        return new TopicExchange(QUESTION_EXCHANGE);
    }

    @Bean
    public TopicExchange questionDlxExchange() {
        return new TopicExchange(QUESTION_DLX_EXCHANGE);
    }

    // Main Queue
    @Bean
    public Queue questionQueue() {
        Map<String, Object> args = new HashMap<>();
        // Set exchange and routing key for dead-lettering
        args.put("x-dead-letter-exchange", QUESTION_DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", RETRY_QUEUE);
        return new Queue(QUESTION_QUEUE, true, false, false, args);
    }

    // Retry Queue
    @Bean
    public Queue retryQueue() {
        Map<String, Object> args = new HashMap<>();
        // TTL for retry after 5 seconds
        args.put("x-message-ttl", 15000);
        // Back to the main queue after delay times
        args.put("x-dead-letter-exchange", QUESTION_EXCHANGE);
        args.put("x-dead-letter-routing-key", QUESTION_QUEUE);
        return new Queue(RETRY_QUEUE, true, false, false, args);
    }

    // Fail queue
    @Bean
    public Queue failQueue() {
        return new Queue(FAIL_QUEUE, true);
    }

    @Bean
    public Binding bindingCreateQuestion(Queue questionQueue, TopicExchange questionExchange) {
        return BindingBuilder
                .bind(questionQueue)
                .to(questionExchange)
                .with(ROUTING_KEY_QUESTION_CREATE);
    }

    @Bean
    public Binding bindingUpdateQuestion(Queue questionQueue, TopicExchange questionExchange) {
        return BindingBuilder
                .bind(questionQueue)
                .to(questionExchange)
                .with(ROUTING_KEY_QUESTION_UPDATE);
    }

    @Bean
    public Binding bindingDeleteQuestion(Queue questionQueue, TopicExchange questionExchange) {
        return BindingBuilder
                .bind(questionQueue)
                .to(questionExchange)
                .with(ROUTING_KEY_QUESTION_DELETE);
    }

    @Bean
    public Binding retryBinding(Queue retryQueue, TopicExchange questionDlxExchange) {
        return BindingBuilder
                .bind(retryQueue)
                .to(questionDlxExchange)
                .with(RETRY_QUEUE);
    }

    @Bean
    public Binding failBinding(Queue failQueue, TopicExchange questionDlxExchange) {
        return BindingBuilder
                .bind(failQueue)
                .to(questionDlxExchange)
                .with(ROUTING_KEY_QUESTION_FAILED);
    }
}
