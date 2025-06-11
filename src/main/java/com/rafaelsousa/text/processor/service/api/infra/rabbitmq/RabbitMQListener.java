package com.rafaelsousa.text.processor.service.api.infra.rabbitmq;

import com.rafaelsousa.text.processor.service.api.model.TextProcessorData;
import com.rafaelsousa.text.processor.service.api.model.TextResultData;
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
    @RabbitListener(queues = TEXT_PROCESSOR_QUEUE, concurrency = "2-3")
    public void handlePostService(@Payload TextProcessorData textProcessorData) {
        log.info("Post sent for processing: {}", textProcessorData);

        if (textProcessorData.getPostBody().contains("Text processing failure test")) {
            throw new RuntimeException(String.format("Failed to process post with ID: %s", textProcessorData.getPostId()));
        }

        @SuppressWarnings("WrapperTypeMayBePrimitive")
        Long numberOfWords = Arrays.stream(textProcessorData.getPostBody().split("\\s+")).count();
        Double totalValue = numberOfWords * 0.10;

        TextResultData textResultData = TextResultData.builder()
                .postId(textProcessorData.getPostId())
                .wordCount(numberOfWords)
                .calculatedValue(totalValue)
                .build();

        rabbitTemplate.convertAndSend(FANOUT_EXCHANGE_PROCESSING_RESULT, FANOUT_ROUTING_KEY, textResultData);
    }
}