package com.spaceflow.booking.application;

import com.spaceflow.booking.application.port.in.BookingQueriesUseCase;
import com.spaceflow.booking.application.port.out.BookingReadModelPort;
import com.spaceflow.booking.application.query.BookingView;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BookingQueryService implements BookingQueriesUseCase {

    private final BookingReadModelPort readModel;

    public BookingQueryService(BookingReadModelPort readModel) {
        this.readModel = readModel;
    }

    @Override
    public List<BookingView> resourceAvailability(UUID resourceId, LocalDate date) {
        return readModel.findByResourceAndDate(resourceId, date);
    }

    @Override
    public List<BookingView> bookingsOfUser(UUID userId) {
        return readModel.findByUser(userId);
    }
}
