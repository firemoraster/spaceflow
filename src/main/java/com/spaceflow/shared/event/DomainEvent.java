package com.spaceflow.shared.event;

import java.util.UUID;

/** Marker for domain events routed through the Transactional Outbox. */
public interface DomainEvent {

    /** Id of the aggregate that emitted the event (used as Kafka partition key). */
    UUID aggregateId();

    /** Stable event name, e.g. {@code BookingCreated}. */
    String eventType();
}
