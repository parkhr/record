package com.example.demo.push;

import com.example.demo.common.exception.ApplicationException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class PushSendResolver {

    private final Map<String, PushSender> senderMap;
    private final String SENDER_NAME = "PushAppSender";

    public PushSendResolver(List<PushSender> senders) {
        this.senderMap = senders.stream().collect(Collectors.toMap(
            sender -> sender.getClass().getSimpleName(),
            sender -> sender
        ));
    }

    public PushSender resolve() {
        PushSender pushSender = senderMap.get(SENDER_NAME);
        if (pushSender == null) {
            throw new ApplicationException("not found pushSender");
        }

        return pushSender;
    }
}
