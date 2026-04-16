package com.tandem.profile_service.api.util;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import com.tandem.profile_service.api.config.ApiConfig;

public final class KafkaHelper {

    private static final String TOPIC_USER_REGISTERED = "user.registered";

    private KafkaHelper() {
    }

    public static void publishUserRegistered(String userId, String phone, String email) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ApiConfig.kafkaBootstrapServers());
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        String message = String.format(
                "{\"eventType\":\"USER_REGISTERED\",\"userId\":\"%s\","
                        + "\"timestamp\":\"%s\","
                        + "\"metadata\":{\"phoneNumber\":\"%s\",\"email\":\"%s\"}}",
                userId,
                java.time.Instant.now().toString(),
                phone != null ? phone : "",
                email != null ? email : "");

        try (KafkaProducer<String, String> producer = new KafkaProducer<>(props)) {
            producer.send(new ProducerRecord<>(TOPIC_USER_REGISTERED, userId, message)).get();
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish user.registered event for " + userId, e);
        }
    }
}
