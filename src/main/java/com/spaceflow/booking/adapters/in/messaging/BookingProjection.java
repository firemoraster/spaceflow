package com.spaceflow.booking.adapters.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceflow.booking.application.port.out.BookingReadModelPort;
import com.spaceflow.booking.application.query.BookingView;
import com.spaceflow.booking.domain.event.BookingCreated;
import com.spaceflow.shared.event.BookingTopics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * Query-side projector: consumes booking events (own consumer group) and
 * materializes the Redis read model. Independent of the notification consumer.
 */
@Component
public class BookingProjection {

    private static final Logger log = LoggerFactory.getLogger(BookingProjection.class);

    private final BookingReadModelPort readModel;
    private final ObjectMapper objectMapper;

    public BookingProjection(BookingReadModelPort readModel, ObjectMapper objectMapper) {
        this.readModel = readModel;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = BookingTopics.BOOKING_EVENTS, groupId = "booking-projection")
    public void on(String payload) throws Exception {
        BookingCreated event = objectMapper.readValue(payload, BookingCreated.class);
        readModel.add(new BookingView(
                event.bookingId(), event.resourceId(), event.userId(),
                event.startAt(), event.endAt()));
        log.debug("Projected booking {} into read model", event.bookingId());
    }
}
