package com.spaceflow.booking.application.port.out;

import com.spaceflow.shared.event.DomainEvent;

/**
 * Outbound port for emitting domain events. The persistence-backed adapter writes
 * them to the outbox within the caller's transaction (Transactional Outbox pattern),
 * so the event is durable iff the aggregate change committed.
 */
public interface DomainEventPort {

    void publish(DomainEvent event);
}
