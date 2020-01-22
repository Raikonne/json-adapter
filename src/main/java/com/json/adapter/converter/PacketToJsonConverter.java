package com.json.adapter.converter;

import com.json.adapter.enumeration.JsonPacketProperties;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PacketToJsonConverter {

    public Optional<JSONObject> createPacketJsonObject(String message) {
        return createValidJson(message);
    }

    private Optional<JSONObject> createValidJson(String message) {
        String[] messageElements = extractMessageElements(message);
        String type = messageElements[3];
        JSONObject jsonPacket = new JSONObject();
        jsonPacket.put(JsonPacketProperties.MSG_ID.getValue(), Long.parseLong(messageElements[1]));
        jsonPacket.put(JsonPacketProperties.OPERATION.getValue(), messageElements[2]);
        jsonPacket.put(JsonPacketProperties.TYPE.getValue(), type);
        jsonPacket.put(JsonPacketProperties.TIMESTAMP.getValue(), Long.parseLong(messageElements[4]));
        return buildBodyBasedOnObjectType(messageElements, type, jsonPacket);
    }

    /* There is probably a better way of doing this more dynamically
       but this is first approach that came to my head and decided to stick with it. */
    private Optional<JSONObject> buildBodyBasedOnObjectType(String[] messageElements, String type, JSONObject jsonPacket) {
        JSONObject jsonPacketBody = new JSONObject();
        if (type.equals(JsonPacketProperties.OUTCOME.getValue())) {
            jsonPacketBody.put(JsonPacketProperties.MARKET_ID.getValue(), messageElements[5]);
            jsonPacketBody.put(JsonPacketProperties.OUTCOME_ID.getValue(), messageElements[6]);
            jsonPacketBody.put(JsonPacketProperties.NAME.getValue(), messageElements[7]);
            jsonPacketBody.put(JsonPacketProperties.PRICE.getValue(), messageElements[8]);
            jsonPacketBody.put(JsonPacketProperties.DISPLAYED.getValue(), Integer.parseInt(messageElements[9]) == 1);
            jsonPacketBody.put(JsonPacketProperties.SUSPENDED.getValue(), Integer.parseInt(messageElements[10]) == 1);
        } else if (type.equals(JsonPacketProperties.MARKET.getValue())) {
            jsonPacketBody.put(JsonPacketProperties.EVENT_ID.getValue(), messageElements[5]);
            jsonPacketBody.put(JsonPacketProperties.MARKET_ID.getValue(), messageElements[6]);
            jsonPacketBody.put(JsonPacketProperties.NAME.getValue(), messageElements[7]);
            jsonPacketBody.put(JsonPacketProperties.DISPLAYED.getValue(), Integer.parseInt(messageElements[8]) == 1);
            jsonPacketBody.put(JsonPacketProperties.SUSPENDED.getValue(), Integer.parseInt(messageElements[9]) == 1);
        } else if (type.equals(JsonPacketProperties.EVENT.getValue())) {
            jsonPacketBody.put(JsonPacketProperties.EVENT_ID.getValue(), messageElements[5]);
            jsonPacketBody.put(JsonPacketProperties.CATEGORY.getValue(), messageElements[6]);
            jsonPacketBody.put(JsonPacketProperties.SUB_CATEGORY.getValue(), messageElements[7]);
            jsonPacketBody.put(JsonPacketProperties.NAME.getValue(), messageElements[8]);
            jsonPacketBody.put(JsonPacketProperties.START_TIME.getValue(), messageElements[9]);
            jsonPacketBody.put(JsonPacketProperties.DISPLAYED.getValue(), Integer.parseInt(messageElements[10]) == 1);
            jsonPacketBody.put(JsonPacketProperties.SUSPENDED.getValue(), Integer.parseInt(messageElements[11]) == 1);
        } else {
            return Optional.empty();
        }
        return Optional.of(jsonPacket.put(JsonPacketProperties.BODY.getValue(), jsonPacketBody));
    }

    private String[] extractMessageElements(String message) {
        String noSlashesMessage = message.replace("\\|", "");
        log.info("Message {} ", noSlashesMessage);
        return noSlashesMessage.split(Pattern.quote("|"));
    }
}
