package com.spaceflow.booking.adapters.out.messaging;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceflow.booking.application.port.out.DomainEventPort;
import com.spaceflow.shared.event.DomainEvent;
import com.spaceflow.shared.outbox.OutboxMessage;
import com.spaceflow.shared.outbox.OutboxRepository;
import org.springframework.stereotype.Component;

/**
 * Writes domain events to the outbox table. Runs inside the caller's transaction,
 * so the event and the aggregate change commit atomically.
 */
@Component
class OutboxEventAdapter implements DomainEventPort {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    OutboxEventAdapter(OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(DomainEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            outboxRepository.save(OutboxMessage.create(event.aggregateId(), event.eventType(), payload));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize event " + event.eventType(), e);
        }
    }
}
