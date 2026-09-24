package com.spaceflow.notification.adapters.in.messaging;

import com.spaceflow.shared.event.BookingTopics;
import com.spaceflow.shared.messaging.ProcessedMessageLedger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

/**
 * Consumes booking domain events and (for now) logs a would-be notification.
 * Idempotent: each {@code messageId} is claimed in the {@link ProcessedMessageLedger}
 * within the same transaction, so redelivered events are processed at most once.
 */
@Component
public class BookingEventsConsumer {

    static final String CONSUMER = "notification";

    private static final Logger log = LoggerFactory.getLogger(BookingEventsConsumer.class);

    private final ProcessedMessageLedger ledger;

    public BookingEventsConsumer(ProcessedMessageLedger ledger) {
        this.ledger = ledger;
    }

    @KafkaListener(topics = BookingTopics.BOOKING_EVENTS, groupId = CONSUMER)
    @Transactional
    public void onBookingEvent(ConsumerRecord<String, String> record) {
        String eventType = Objects.requireNonNullElse(header(record, "eventType"), "unknown");
        String messageId = header(record, "messageId");

        if (messageId == null) {
            // can't dedupe without an id; process rather than drop the event
            log.warn("[notification] {} for aggregate {} has no messageId header, processing without dedupe",
                    eventType, record.key());
        } else if (!ledger.claim(CONSUMER, UUID.fromString(messageId))) {
            log.debug("[notification] duplicate message {} skipped", messageId);
            return;
        }

        log.info("[notification] {} for aggregate {} -> sending notification (payload={})",
                eventType, record.key(), record.value());
    }

    private String header(ConsumerRecord<String, String> record, String name) {
        var header = record.headers().lastHeader(name);
        return header == null ? null : new String(header.value(), StandardCharsets.UTF_8);
    }
}
