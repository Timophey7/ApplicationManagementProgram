package com.notservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.notservice.models.MessageResponse;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

public class MessageResponseDeserializer implements Deserializer<MessageResponse> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        Deserializer.super.configure(configs, isKey);
    }

    @Override
    public MessageResponse deserialize(String s, byte[] bytes) {
        if (bytes == null){
            return null;
        }

        try{
            return objectMapper.readValue(bytes, MessageResponse.class);
        }catch (Exception e){
            throw new SerializationException("exception during deserialization",e);
        }
    }

    @Override
    public void close() {
        Deserializer.super.close();
    }
}
