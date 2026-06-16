package com.spaceflow.booking.adapters.in.web.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.util.UUID;

public record CreateBookingRequest(
        @NotNull UUID resourceId,
        @NotNull @Future Instant startAt,
        @NotNull @Future Instant endAt
) {}
