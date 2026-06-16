package com.spaceflow.booking.adapters.in.web;

import com.spaceflow.booking.adapters.in.web.dto.CreateBookingRequest;
import com.spaceflow.booking.application.port.in.CreateBookingUseCase;
import com.spaceflow.booking.application.port.in.CreateBookingUseCase.CreateBookingCommand;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final CreateBookingUseCase createBooking;

    public BookingController(CreateBookingUseCase createBooking) {
        this.createBooking = createBooking;
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateBookingRequest request,
                                       @AuthenticationPrincipal Jwt jwt,
                                       UriComponentsBuilder uriBuilder) {
        UUID userId = UUID.fromString(jwt.getSubject());
        UUID id = createBooking.create(new CreateBookingCommand(
                request.resourceId(), userId, request.startAt(), request.endAt()));
        URI location = uriBuilder.path("/api/v1/bookings/{id}").buildAndExpand(id).toUri();
        return ResponseEntity.created(location).build();
    }
}
