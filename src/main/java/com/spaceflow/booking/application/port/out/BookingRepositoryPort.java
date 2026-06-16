package com.spaceflow.booking.application.port.out;

import com.spaceflow.booking.domain.model.Booking;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/** Outbound port. Implemented by the persistence adapter. */
public interface BookingRepositoryPort {

    Booking save(Booking booking);

    Optional<Booking> findById(UUID id);

    /** True if an ACTIVE booking overlaps [startAt, endAt) for the given resource. */
    boolean existsOverlapping(UUID resourceId, Instant startAt, Instant endAt);
}
