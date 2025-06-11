package com.rafaelsousa.text.processor.service.api.infra.rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class RabbitMQConfig {
    private static final String TEXT_PROCESSOR_PREFIX_QUEUE = "text-processor-service.post-processing.v1";
    public static final String TEXT_PROCESSOR_QUEUE = TEXT_PROCESSOR_PREFIX_QUEUE + ".q";
    public static final String TEXT_PROCESSOR_DEAD_LETTER_QUEUE = TEXT_PROCESSOR_PREFIX_QUEUE + ".dlq";

    public static final String FANOUT_EXCHANGE_PROCESS_TEXT = TEXT_PROCESSOR_PREFIX_QUEUE + ".e";
    public static final String FANOUT_EXCHANGE_PROCESSING_RESULT = "post-service.post-processing-result.v1.e";

    public static final String FANOUT_ROUTING_KEY = ""; // Fanout exchanges do not use routing keys

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory factory) {
        return new RabbitAdmin(factory);
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public Queue queueTextProcessorService() {
        Map<String, Object> args = Map.of(
                "x-dead-letter-exchange", "",
                "x-dead-letter-routing-key", TEXT_PROCESSOR_DEAD_LETTER_QUEUE
        );

        return QueueBuilder.durable(TEXT_PROCESSOR_QUEUE)
                .withArguments(args)
                .build();
    }

    @Bean
    public Queue deadLetterQueueTextProcessorService() {
        return QueueBuilder.durable(TEXT_PROCESSOR_DEAD_LETTER_QUEUE).build();
    }

    @Bean
    public Binding bindingTextProcessorService() {
        return BindingBuilder.bind(queueTextProcessorService()).to(exchangeTextProcessorService());
    }

    @Bean
    public FanoutExchange exchangeTextProcessorService() {
        return ExchangeBuilder.fanoutExchange(FANOUT_EXCHANGE_PROCESS_TEXT).build();
    }
}