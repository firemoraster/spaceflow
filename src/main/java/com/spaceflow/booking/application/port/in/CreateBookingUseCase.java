package com.spaceflow.booking.application.port.in;

import java.time.Instant;
import java.util.UUID;

/** Inbound port (command side). Driven by web adapter, implemented by application service. */
public interface CreateBookingUseCase {

    UUID create(CreateBookingCommand command);

    record CreateBookingCommand(UUID resourceId, UUID userId, Instant startAt, Instant endAt) {}
}
