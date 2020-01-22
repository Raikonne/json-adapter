package com.json.adapter.converter;

import com.json.adapter.enumeration.JsonPacketProperties;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class PacketToJsonConverterTest {

    private PacketToJsonConverter packetToJsonConverter;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        packetToJsonConverter = new PacketToJsonConverter();
    }

    @Test
    public void shouldCreateValidJsonWhenTypeIsOutcome() {
        String message = getBytesAsString("outcomeType");
        Optional<JSONObject> packetJson = packetToJsonConverter.createPacketJsonObject(message);
        assertTrue(packetJson.isPresent());
        JSONAssert.assertEquals(buildTestJson("outcome"), packetJson.get(), JSONCompareMode.STRICT);
    }

    @Test
    public void shouldCreateValidJsonWhenTypeIsMarket() {
        String message = getBytesAsString("marketType");
        Optional<JSONObject> packetJson = packetToJsonConverter.createPacketJsonObject(message);
        assertTrue(packetJson.isPresent());
        JSONAssert.assertEquals(buildTestJson("market"), packetJson.get(), JSONCompareMode.STRICT);
    }

    @Test
    public void shouldCreateValidJsonWhenTypeIsEvent() {
        String message = getBytesAsString("eventType");
        Optional<JSONObject> packetJson = packetToJsonConverter.createPacketJsonObject(message);
        assertTrue(packetJson.isPresent());
        JSONAssert.assertEquals(buildTestJson("event"), packetJson.get(), JSONCompareMode.STRICT);
    }

    @Test
    public void shouldCreateEmptyJsonWhenTypeIsInvalid() {
        String message = getBytesAsString("invalidType");
        Optional<JSONObject> packetJson = packetToJsonConverter.createPacketJsonObject(message);
        assertTrue(packetJson.isEmpty());
    }

    private static String getBytesAsString(String filePath) {
        if (!filePath.startsWith("src/test/resources/")) {
            filePath = "src/test/resources/" + filePath;
        }
        try {
            Path path = Paths.get(filePath);
            return new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private JSONObject buildTestJson(String type) throws JSONException {
        if (type.equals(JsonPacketProperties.OUTCOME.getValue())) {
            return (new JSONObject(getBytesAsString("outcomeTypeJson")));
        } else if (type.equals(JsonPacketProperties.MARKET.getValue())) {
            return (new JSONObject(getBytesAsString("marketTypeJson")));
        } else if (type.equals(JsonPacketProperties.EVENT.getValue())) {
            return (new JSONObject(getBytesAsString("eventTypeJson")));
        }
        return null;
    }
}
