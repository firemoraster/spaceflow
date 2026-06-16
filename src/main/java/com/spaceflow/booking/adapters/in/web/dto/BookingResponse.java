package com.spaceflow.booking.adapters.in.web.dto;

import com.spaceflow.booking.domain.model.Booking;

import java.time.Instant;
import java.util.UUID;

public record BookingResponse(
        UUID id,
        UUID resourceId,
        UUID userId,
        Instant startAt,
        Instant endAt,
        String status
) {
    public static BookingResponse from(Booking b) {
        return new BookingResponse(
                b.getId(), b.getResourceId(), b.getUserId(),
                b.getStartAt(), b.getEndAt(), b.getStatus().name());
    }
}
