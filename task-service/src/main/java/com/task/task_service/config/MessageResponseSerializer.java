package com.task.task_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.task_service.models.MessageResponse;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public class MessageResponseSerializer implements Serializer<MessageResponse> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Serializer.super.configure(configs, isKey);
    }

    @Override
    public byte[] serialize(String s, MessageResponse messageResponse) {
        if (messageResponse == null) {
            return null;
        }

        try {
            return mapper.writeValueAsBytes(messageResponse);
        }catch (Exception e){
            throw new SerializationException("Exception in serialization of userInfo", e);
        }
    }

    @Override
    public void close() {
        Serializer.super.close();
    }
}
