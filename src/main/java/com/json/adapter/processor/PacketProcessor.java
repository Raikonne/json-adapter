package com.json.adapter.processor;

import com.json.adapter.configuration.RabbitConfiguration;
import com.json.adapter.converter.PacketToJsonConverter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PacketProcessor {

    private final PacketToJsonConverter packetToJsonConverter;
    private final MessageProperties rabbitMessageProperties;
    private final RabbitTemplate rabbitTemplate;
    private final int retryAttempts;

    public PacketProcessor(PacketToJsonConverter packetToJsonConverter, MessageProperties rabbitMessageProperties, RabbitTemplate rabbitTemplate, @Value("${batch.retry.attempts:3}") int retryAttempts) {
        this.packetToJsonConverter = packetToJsonConverter;
        this.rabbitMessageProperties = rabbitMessageProperties;
        this.rabbitTemplate = rabbitTemplate;
        this.retryAttempts = retryAttempts;
    }

    public void processMessage(String message) {
        packetToJsonConverter.createPacketJsonObject(message).ifPresent(this::sendToTheRabbitQueue);
    }

    private void sendToTheRabbitQueue(JSONObject jsonPacket) {
        Message message = new Message(jsonPacket.toString(2).getBytes(), rabbitMessageProperties);
        for (int i = 0; i < retryAttempts; i++) {
            try {
                rabbitTemplate.send(RabbitConfiguration.destinationExchangeName, RabbitConfiguration.routingKey, message);
                return;
            } catch (AmqpException exception) {
                log.warn(String.format("Failed to send packet to RabbitMQ. Will Retry %d times, exception %s cause %s", retryAttempts, exception.getMessage(), exception.getCause()));
            }
        }
        log.error("Failed to send track event to RabbitMQ");
    }
}
