package com.spaceflow.notification.adapters.in.messaging;

import com.spaceflow.shared.event.BookingTopics;
import com.spaceflow.shared.messaging.ProcessedMessageLedger;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class BookingEventsConsumerTest {

    private final InMemoryLedger ledger = new InMemoryLedger();
    private final BookingEventsConsumer consumer = new BookingEventsConsumer(ledger);

    @Test
    void claimsMessageIdFromHeaderUnderOwnConsumerName() {
        UUID messageId = UUID.randomUUID();

        consumer.onBookingEvent(record(messageId));

        assertThat(ledger.claims).containsExactly(BookingEventsConsumer.CONSUMER + ":" + messageId);
    }

    @Test
    void redeliveredMessageIsRejectedByLedger() {
        UUID messageId = UUID.randomUUID();

        consumer.onBookingEvent(record(messageId));
        consumer.onBookingEvent(record(messageId));

        assertThat(ledger.claims).hasSize(2);
        assertThat(ledger.accepted).containsExactly(BookingEventsConsumer.CONSUMER + ":" + messageId);
    }

    @Test
    void messageWithoutIdIsProcessedWithoutTouchingLedger() {
        consumer.onBookingEvent(new ConsumerRecord<>(BookingTopics.BOOKING_EVENTS, 0, 0L, "key", "{}"));

        assertThat(ledger.claims).isEmpty();
    }

    private static ConsumerRecord<String, String> record(UUID messageId) {
        var record = new ConsumerRecord<>(BookingTopics.BOOKING_EVENTS, 0, 0L, "key", "{}");
        record.headers().add(new RecordHeader("eventType", "BookingCreated".getBytes(StandardCharsets.UTF_8)));
        record.headers().add(new RecordHeader("messageId", messageId.toString().getBytes(StandardCharsets.UTF_8)));
        return record;
    }

    /** Mimics the (consumer, messageId) primary key without a database. */
    private static final class InMemoryLedger extends ProcessedMessageLedger {
        final List<String> claims = new ArrayList<>();
        final Set<String> accepted = new HashSet<>();

        InMemoryLedger() {
            super(null);
        }

        @Override
        public boolean claim(String consumer, UUID messageId) {
            String key = consumer + ":" + messageId;
            claims.add(key);
            return accepted.add(key);
        }
    }
}
