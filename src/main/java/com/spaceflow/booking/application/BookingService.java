package com.spaceflow.booking.application;

import com.spaceflow.booking.application.port.in.CreateBookingUseCase;
import com.spaceflow.booking.application.port.out.BookingRepositoryPort;
import com.spaceflow.booking.domain.model.Booking;
import com.spaceflow.shared.exception.BookingConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class BookingService implements CreateBookingUseCase {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingRepositoryPort repository;

    public BookingService(BookingRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public UUID create(CreateBookingCommand cmd) {
        if (repository.existsOverlapping(cmd.resourceId(), cmd.startAt(), cmd.endAt())) {
            throw new BookingConflictException("Slot already booked for resource " + cmd.resourceId());
        }
        Booking booking = Booking.create(cmd.resourceId(), cmd.userId(), cmd.startAt(), cmd.endAt());
        Booking saved = repository.save(booking);
        // TODO(next iteration): write BookingCreated to outbox in the same tx, publish to Kafka.
        log.info("Booking {} created for resource {}", saved.getId(), saved.getResourceId());
        return saved.getId();
    }
}
