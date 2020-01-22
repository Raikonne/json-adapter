package com.json.adapter.web;

import com.json.adapter.processor.PacketProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;


@Slf4j
@RestController
@RequestMapping("packet")
public class PacketController {

    private final PacketProcessor packetProcessor;
    private final String packetDataUrl;
    private final int port;

    public PacketController(PacketProcessor packetProcessor, @Value("${inbound.host}") String packetDataUrl, @Value("${inbound.port}") int port) {
        this.packetProcessor = packetProcessor;
        this.packetDataUrl = packetDataUrl;
        this.port = port;
    }

    @GetMapping(value = "retrieve/all", produces = "text/plain")
    public void getData() throws IOException {
        try (Socket socket = new Socket(packetDataUrl, port)) {
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            while ((reader.readLine()) != null) {
                String line = reader.readLine();
                packetProcessor.processMessage(line);
            }
        }
    }
}
