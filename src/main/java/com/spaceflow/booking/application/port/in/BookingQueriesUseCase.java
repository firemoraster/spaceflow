package com.spaceflow.booking.application.port.in;

import com.spaceflow.booking.application.query.BookingView;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Inbound port (query side). Served entirely from the read model. */
public interface BookingQueriesUseCase {

    List<BookingView> resourceAvailability(UUID resourceId, LocalDate date);

    List<BookingView> bookingsOfUser(UUID userId);
}
