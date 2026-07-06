package com.spaceflow.booking.adapters.in.web;

import com.spaceflow.booking.application.port.in.BookingQueriesUseCase;
import com.spaceflow.booking.application.query.BookingView;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Query side (CQRS): reads are served from the Redis read model. */
@RestController
@RequestMapping("/api/v1")
public class BookingQueryController {

    /** Fallback identity used only when running without auth (dev profile). */
    private static final UUID DEMO_USER = UUID.fromString("00000000-0000-0000-0000-000000000001");

    private final BookingQueriesUseCase queries;

    public BookingQueryController(BookingQueriesUseCase queries) {
        this.queries = queries;
    }

    @GetMapping("/resources/{resourceId}/availability")
    public List<BookingView> availability(@PathVariable UUID resourceId,
                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return queries.resourceAvailability(resourceId, date);
    }

    @GetMapping("/bookings")
    public List<BookingView> myBookings(@AuthenticationPrincipal Jwt jwt) {
        UUID userId = jwt != null ? UUID.fromString(jwt.getSubject()) : DEMO_USER;
        return queries.bookingsOfUser(userId);
    }
}
