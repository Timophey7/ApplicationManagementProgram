package com.notservice.config;

import com.notservice.models.MessageResponse;
import com.notservice.service.SendMessageService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class KafkaListenerService {

    SendMessageService sendMessageService;

    @KafkaListener(topics = "invite",groupId = "messages")
    public void listen(MessageResponse messageResponse){
        sendMessageService.sendMessage(messageResponse);
    }

}