package com.example.quizservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MessageQueueConfig {
    public final static String QUIZ_EXCHANGE = "quiz-exchange";
    public final static String QUIZ_DLX_EXCHANGE = "quiz-exchange-dlx";
    public final static String QUIZ_QUEUE = "quiz-queue";
    public final static String RETRY_QUEUE = "quiz-service.retry-queue";
    public final static String FAIL_QUEUE = "quiz-service.fail-queue";
    public final static String ROUTING_KEY_QUIZ_CREATE = "quiz.create";
    public final static String ROUTING_KEY_QUIZ_UPDATE = "quiz.update";
    public final static String ROUTING_KEY_QUIZ_DELETE = "quiz.delete";
    public final static String ROUTING_KEY_QUIZ_FAILED = "quiz.failed";

    @Bean
    public TopicExchange quizExchange() {
        return new TopicExchange(QUIZ_EXCHANGE);
    }

    @Bean
    public TopicExchange quizDlxExchange() {
        return new TopicExchange(QUIZ_DLX_EXCHANGE);
    }

    @Bean
    public Queue quizQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", QUIZ_DLX_EXCHANGE);
        args.put("x-dead-letter-routing-key", RETRY_QUEUE);
        return new Queue(QUIZ_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue retryQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 5000); // Retry after 5 seconds
        args.put("x-dead-letter-exchange", QUIZ_EXCHANGE);
        args.put("x-dead-letter-routing-key", QUIZ_QUEUE);
        return new Queue(RETRY_QUEUE, true, false, false, args);
    }

    @Bean
    public Queue failQueue() {
        return new Queue(FAIL_QUEUE, true);
    }

    @Bean
    public Binding bindingCreateQuiz(Queue quizQueue, TopicExchange quizExchange) {
        return BindingBuilder
                .bind(quizQueue)
                .to(quizExchange)
                .with(ROUTING_KEY_QUIZ_CREATE);
    }

    @Bean
    public Binding bindingUpdateQuiz(Queue quizQueue, TopicExchange quizExchange) {
        return BindingBuilder
                .bind(quizQueue)
                .to(quizExchange)
                .with(ROUTING_KEY_QUIZ_UPDATE);
    }

    @Bean
    public Binding bindingDeleteQuiz(Queue quizQueue, TopicExchange quizExchange) {
        return BindingBuilder
                .bind(quizQueue)
                .to(quizExchange)
                .with(ROUTING_KEY_QUIZ_DELETE);
    }

    @Bean
    public Binding retryBinding(Queue retryQueue, TopicExchange quizDlxExchange) {
        return BindingBuilder
                .bind(retryQueue)
                .to(quizDlxExchange)
                .with(RETRY_QUEUE);
    }

    @Bean
    public Binding failBinding(Queue failQueue, TopicExchange quizDlxExchange) {
        return BindingBuilder
                .bind(failQueue)
                .to(quizDlxExchange)
                .with(ROUTING_KEY_QUIZ_FAILED);
    }
}
