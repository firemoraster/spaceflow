package com.spaceflow.booking.application.query;

import java.time.Instant;
import java.util.UUID;

/** Read-model projection of a booking, served from Redis on the query side. */
public record BookingView(
        UUID bookingId,
        UUID resourceId,
        UUID userId,
        Instant startAt,
        Instant endAt
) {}
