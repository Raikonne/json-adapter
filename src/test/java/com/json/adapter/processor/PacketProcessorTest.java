package com.json.adapter.processor;

import com.json.adapter.configuration.RabbitConfiguration;
import com.json.adapter.converter.PacketToJsonConverter;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PacketProcessorTest {

    private PacketProcessor packetProcessor;

    @Mock
    public PacketToJsonConverter packetToJsonConverter;

    @Mock
    public RabbitTemplate rabbitTemplate;

    @Mock
    public MessageProperties messageProperties;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        packetProcessor = new PacketProcessor(packetToJsonConverter, messageProperties, rabbitTemplate, 3);
    }

    @Test
    public void shouldSendRabbitMessage() {
        byte[] messageAsByteArray = getBytes("packet");
        ArgumentCaptor<Message> eventsCaptor = ArgumentCaptor.forClass(Message.class);

        when(packetToJsonConverter.createPacketJsonObject(any())).thenReturn(Optional.of(new JSONObject()));

        packetProcessor.processMessage(new String(messageAsByteArray));

        verify(rabbitTemplate, times(1)).send(eq(RabbitConfiguration.destinationExchangeName), eq("packet"), eventsCaptor.capture());
    }

    @Test
    public void shouldRetryUpTo3Times() throws Exception {
        byte[] messageAsByteArray = getBytes("packet");
        ArgumentCaptor<Message> eventsCaptor = ArgumentCaptor.forClass(Message.class);

        when(packetToJsonConverter.createPacketJsonObject(any())).thenReturn(Optional.of(new JSONObject()));
        doThrow(AmqpException.class).when(rabbitTemplate).send(any(), eq("packet"), any());

        packetProcessor.processMessage(new String(messageAsByteArray));

        verify(rabbitTemplate, times(3)).send(eq(RabbitConfiguration.destinationExchangeName), eq("packet"), eventsCaptor.capture());
    }

    private static byte[] getBytes(String filePath) {
        if (!filePath.startsWith("src/test/resources/")) {
            filePath = "src/test/resources/" + filePath;
        }
        try {
            Path path = Paths.get(filePath);
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
