package br.dev.allantoledo.psc.dto.websocket;

import lombok.Data;

@Data
public class Payload {
    public enum MessageType {
        SUBSCRIBED, DATA
    }

    private MessageType messageType;
    private Object data;


    public static Payload createSubscribedType() {
        Payload payload = new Payload();
        payload.setMessageType(MessageType.SUBSCRIBED);
        return payload;
    }

    public static Payload fromData(Object data) {
        Payload payload = new Payload();
        payload.setMessageType(MessageType.DATA);
        payload.setData(data);
        return payload;
    }
}
