package com.task.task_service.config;

import com.task.task_service.models.MessageResponse;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.TopicConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    ProducerFactory<String, MessageResponse> producerFactory(){
        Map<String, Object> properties = new HashMap<>();
        properties.put("bootstrap.servers", bootstrapServers);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, MessageResponseSerializer.class);
        return new DefaultKafkaProducerFactory<>(properties);
    }

    @Bean
    public NewTopic newTopic(){
        return TopicBuilder.name("invite")
                .partitions(4)
                .replicas(1)
                .config(TopicConfig.RETENTION_MS_CONFIG,String.valueOf(Duration.ofDays(7).toMillis()))
                .build();
    }

    @Bean
    public KafkaTemplate<String, MessageResponse> kafkaTemplate(){
        return new KafkaTemplate<>(producerFactory());
    }

}
