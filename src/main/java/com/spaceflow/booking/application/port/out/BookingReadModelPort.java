package com.spaceflow.booking.application.port.out;

import com.spaceflow.booking.application.query.BookingView;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Outbound port for the CQRS read model (backed by Redis). */
public interface BookingReadModelPort {

    /** Idempotent: adding the same view twice is a no-op. */
    void add(BookingView view);

    List<BookingView> findByResourceAndDate(UUID resourceId, LocalDate date);

    List<BookingView> findByUser(UUID userId);
}
