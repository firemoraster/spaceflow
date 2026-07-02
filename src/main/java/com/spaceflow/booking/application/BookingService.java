package com.spaceflow.booking.application;

import com.spaceflow.booking.application.port.in.CreateBookingUseCase;
import com.spaceflow.booking.application.port.out.BookingRepositoryPort;
import com.spaceflow.booking.application.port.out.DomainEventPort;
import com.spaceflow.booking.domain.event.BookingCreated;
import com.spaceflow.booking.domain.model.Booking;
import com.spaceflow.shared.exception.BookingConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class BookingService implements CreateBookingUseCase {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepositoryPort repository;
    private final DomainEventPort events;

    public BookingService(BookingRepositoryPort repository, DomainEventPort events) {
        this.repository = repository;
        this.events = events;
    }

    @Override
    @Transactional
    public UUID create(CreateBookingCommand cmd) {
        if (repository.existsOverlapping(cmd.resourceId(), cmd.startAt(), cmd.endAt())) {
            throw new BookingConflictException("Slot already booked for resource " + cmd.resourceId());
        }
        Booking booking = Booking.create(cmd.resourceId(), cmd.userId(), cmd.startAt(), cmd.endAt());
        Booking saved = repository.save(booking);

        // Written to the outbox in the same tx; relayed to Kafka asynchronously.
        events.publish(new BookingCreated(
                saved.getId(), saved.getResourceId(), saved.getUserId(),
                saved.getStartAt(), saved.getEndAt(), Instant.now()));

        log.info("Booking {} created for resource {}", saved.getId(), saved.getResourceId());
        return saved.getId();
    }
}
