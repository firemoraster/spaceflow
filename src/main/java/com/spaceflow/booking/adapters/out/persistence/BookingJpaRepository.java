package com.spaceflow.booking.adapters.out.persistence;

import com.spaceflow.booking.domain.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

interface BookingJpaRepository extends JpaRepository<Booking, UUID> {

    @Query("""
            select count(b) > 0 from Booking b
            where b.resourceId = :resourceId
              and b.status = com.spaceflow.booking.domain.model.BookingStatus.ACTIVE
              and b.startAt < :endAt
              and b.endAt > :startAt
            """)
    boolean existsOverlapping(@Param("resourceId") UUID resourceId,
                              @Param("startAt") Instant startAt,
                              @Param("endAt") Instant endAt);
}
