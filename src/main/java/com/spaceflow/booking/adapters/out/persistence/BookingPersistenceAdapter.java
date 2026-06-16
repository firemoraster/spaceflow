package com.spaceflow.booking.adapters.out.persistence;

import com.spaceflow.booking.application.port.out.BookingRepositoryPort;
import com.spaceflow.booking.domain.model.Booking;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
class BookingPersistenceAdapter implements BookingRepositoryPort {

    private final BookingJpaRepository jpa;

    BookingPersistenceAdapter(BookingJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Booking save(Booking booking) {
        return jpa.save(booking);
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        return jpa.findById(id);
    }

    @Override
    public boolean existsOverlapping(UUID resourceId, Instant startAt, Instant endAt) {
        return jpa.existsOverlapping(resourceId, startAt, endAt);
    }
}
