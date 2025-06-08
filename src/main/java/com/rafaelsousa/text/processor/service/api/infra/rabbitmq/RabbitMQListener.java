package com.rafaelsousa.text.processor.service.api.infra.rabbitmq;

import com.rafaelsousa.text.processor.service.api.model.PostData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.rafaelsousa.text.processor.service.api.infra.rabbitmq.RabbitMQConfig.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQListener {
    private final RabbitTemplate rabbitTemplate;

    @SuppressWarnings("java:S112")
    @RabbitListener(queues = POST_PROCESSING_QUEUE, concurrency = "2-3")
    public void handlePostService(@Payload PostData postData) {
        log.info("Post sent for processing: {}", postData);

        if (postData.getTitle().equalsIgnoreCase("Text processing failure test")) {
            throw new RuntimeException(String.format("Failed to process post with ID: %s", postData.getId()));
        }

        @SuppressWarnings("WrapperTypeMayBePrimitive")
        Long numberOfWords = Arrays.stream(postData.getBody().split("\\s+")).count();
        Double totalValue = numberOfWords * 0.10;

        postData.setWordCount(numberOfWords);
        postData.setCalculatedValue(totalValue);

        rabbitTemplate.convertAndSend(FANOUT_EXCHANGE_POST_RECEIVED, FANOUT_ROUTING_KEY, postData);
    }
}