package com.spaceflow.booking.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

/**
 * Booking aggregate root. {@code version} drives optimistic locking so two
 * concurrent requests for the same slot cannot both commit.
 */
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID resourceId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private Instant startAt;

    @Column(nullable = false)
    private Instant endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Version
    private long version;

    protected Booking() {
        // for JPA
    }

    private Booking(UUID id, UUID resourceId, UUID userId, Instant startAt, Instant endAt, BookingStatus status) {
        this.id = id;
        this.resourceId = resourceId;
        this.userId = userId;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
    }

    public static Booking create(UUID resourceId, UUID userId, Instant startAt, Instant endAt) {
        if (!endAt.isAfter(startAt)) {
            throw new IllegalArgumentException("endAt must be after startAt");
        }
        return new Booking(UUID.randomUUID(), resourceId, userId, startAt, endAt, BookingStatus.ACTIVE);
    }

    public void cancel() {
        this.status = BookingStatus.CANCELLED;
    }

    public UUID getId() { return id; }
    public UUID getResourceId() { return resourceId; }
    public UUID getUserId() { return userId; }
    public Instant getStartAt() { return startAt; }
    public Instant getEndAt() { return endAt; }
    public BookingStatus getStatus() { return status; }
    public long getVersion() { return version; }
}
