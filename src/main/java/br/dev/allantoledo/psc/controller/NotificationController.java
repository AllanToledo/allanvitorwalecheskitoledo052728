package br.dev.allantoledo.psc.controller;

import br.dev.allantoledo.psc.dto.websocket.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
public class NotificationController {
    @SubscribeMapping("/albums")
    public Payload albums() {
        return Payload.createSubscribedType();
    }
}
