package com.spaceflow.booking.domain.event;

import com.spaceflow.shared.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

/** Emitted when a booking is confirmed. Serialized to the outbox as JSON. */
public record BookingCreated(
        UUID bookingId,
        UUID resourceId,
        UUID userId,
        Instant startAt,
        Instant endAt,
        Instant occurredAt
) implements DomainEvent {

    @Override
    public UUID aggregateId() {
        return bookingId;
    }

    @Override
    public String eventType() {
        return "BookingCreated";
    }
}
