package com.spaceflow.notification.adapters.in.messaging;

import com.spaceflow.shared.event.BookingTopics;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Consumes booking domain events and (for now) logs a would-be notification.
 * TODO(next iteration): idempotent handling — dedupe by the {@code messageId}
 * header so redelivered events are processed at-most-once.
 */
@Component
public class BookingEventsConsumer {

    private static final Logger log = LoggerFactory.getLogger(BookingEventsConsumer.class);

    @KafkaListener(topics = BookingTopics.BOOKING_EVENTS, groupId = "notification")
    public void onBookingEvent(ConsumerRecord<String, String> record) {
        String eventType = header(record, "eventType");
        log.info("[notification] {} for aggregate {} -> sending notification (payload={})",
                eventType, record.key(), record.value());
    }

    private String header(ConsumerRecord<String, String> record, String name) {
        var header = record.headers().lastHeader(name);
        return header == null ? "unknown" : new String(header.value(), StandardCharsets.UTF_8);
    }
}
