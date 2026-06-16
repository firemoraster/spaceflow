package com.spaceflow.booking.domain.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BookingTest {

    @Test
    void createsActiveBookingForValidRange() {
        Instant start = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant end = start.plus(1, ChronoUnit.HOURS);

        Booking booking = Booking.create(UUID.randomUUID(), UUID.randomUUID(), start, end);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ACTIVE);
        assertThat(booking.getId()).isNotNull();
    }

    @Test
    void rejectsEndBeforeStart() {
        Instant start = Instant.now().plus(2, ChronoUnit.HOURS);
        Instant end = start.minus(1, ChronoUnit.HOURS);

        assertThatThrownBy(() -> Booking.create(UUID.randomUUID(), UUID.randomUUID(), start, end))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void cancelChangesStatus() {
        Instant start = Instant.now().plus(1, ChronoUnit.HOURS);
        Booking booking = Booking.create(UUID.randomUUID(), UUID.randomUUID(), start, start.plus(1, ChronoUnit.HOURS));

        booking.cancel();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
    }
}
