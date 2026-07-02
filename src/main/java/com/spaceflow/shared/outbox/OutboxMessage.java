package com.spaceflow.shared.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * A domain event persisted in the same transaction as the aggregate that raised it.
 * Relayed to Kafka by {@link OutboxRelay} and then flagged {@code published}.
 */
@Entity
@Table(name = "outbox")
public class OutboxMessage {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID aggregateId;

    @Column(nullable = false)
    private String eventType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false, columnDefinition = "jsonb")
    private String payload;

    @Column(nullable = false)
    private boolean published;

    @Column(nullable = false)
    private Instant createdAt;

    protected OutboxMessage() {
        // for JPA
    }

    private OutboxMessage(UUID aggregateId, String eventType, String payload) {
        this.id = UUID.randomUUID();
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.payload = payload;
        this.published = false;
        this.createdAt = Instant.now();
    }

    public static OutboxMessage create(UUID aggregateId, String eventType, String payload) {
        return new OutboxMessage(aggregateId, eventType, payload);
    }

    public void markPublished() {
        this.published = true;
    }

    public UUID getId() { return id; }
    public UUID getAggregateId() { return aggregateId; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public boolean isPublished() { return published; }
    public Instant getCreatedAt() { return createdAt; }
}
