package com.paquetrack.shipment.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;
import org.springframework.retry.policy.SimpleRetryPolicy;

@Configuration
public class RabbitMQConfig {

    @Bean
    public RabbitAdmin rabbitAdmin(@NonNull ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    // ─── Producer ────────────────────────────────────────────────────
    public static final String EXCHANGE = "logistics.exchange";
    public static final String ROUTING_KEY_CREATED = "shipment.created";

    // ─── Consumer ────────────────────────────────────────────────────
    public static final String ROUTING_KEY_STATUS = "tracking.status.updated";
    public static final String STATUS_QUEUE = "shipment.status.queue";

    // ─── Dead Letter ─────────────────────────────────────────────────
    public static final String DLQ_EXCHANGE = "shipment.dlq.exchange";
    public static final String DLQ_QUEUE = "shipment.status.dlq";
    public static final String DLQ_ROUTING_KEY = "shipment.status.dead";

    // ─── Dead Letter Exchange ─────────────────────────────────────────
    @Bean
    public DirectExchange dlqExchange() {
        return new DirectExchange(DLQ_EXCHANGE, true, false);
    }

    // ─── Dead Letter Queue ────────────────────────────────────────────
    @Bean
    public Queue dlqQueue() {
        return new Queue(DLQ_QUEUE, true, false, false);
    }

    @Bean
    public Binding dlqBinding(Queue dlqQueue, DirectExchange dlqExchange) {
        return BindingBuilder
                .bind(dlqQueue)
                .to(dlqExchange)
                .with(DLQ_ROUTING_KEY);
    }

    // ─── Cola principal con DLQ configurada ──────────────────────────
    @Bean
    public Queue shipmentStatusQueue() {
        Map<String, Object> args = new HashMap<>();
        // Cuando un mensaje falla va al DLQ exchange
        args.put("x-dead-letter-exchange", DLQ_EXCHANGE);
        args.put("x-dead-letter-routing-key", DLQ_ROUTING_KEY);
        return new Queue(STATUS_QUEUE, true, false, false, args);
    }

    @Bean
    public TopicExchange shipmentExchange() {
        return new TopicExchange(EXCHANGE, true, false);
    }

    @Bean
    public Binding shipmentStatusBinding(Queue shipmentStatusQueue,
            TopicExchange shipmentExchange) {
        return BindingBuilder
                .bind(shipmentStatusQueue)
                .to(shipmentExchange)
                .with(ROUTING_KEY_STATUS);
    }

    // ─── Reintentos: 3 intentos con espera entre ellos ───────────────
    @Bean
    public RetryOperationsInterceptor retryInterceptor() {
        return RetryInterceptorBuilder.stateless()
                .retryPolicy(new SimpleRetryPolicy(3))
                .backOffPolicy(new ExponentialBackOffPolicy() {
                    {
                        setInitialInterval(2000);
                        setMultiplier(2.0);
                        setMaxInterval(10000);
                    }
                })
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build();
    }

    // ─── Listener factory con reintentos ─────────────────────────────
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        factory.setAdviceChain(retryInterceptor());
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    // ─── Converter y Template ─────────────────────────────────────────
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(@NonNull ConnectionFactory connectionFactory,
            @NonNull MessageConverter messageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter);
        return template;
    }

}