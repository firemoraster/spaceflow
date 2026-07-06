package com.spaceflow.booking.adapters.out.readmodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spaceflow.booking.application.port.out.BookingReadModelPort;
import com.spaceflow.booking.application.query.BookingView;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Redis-backed read model. Views are stored in per-resource-per-day and per-user
 * SETs, so re-processing the same event (identical JSON) is naturally idempotent.
 */
@Component
class RedisBookingReadModelAdapter implements BookingReadModelPort {

    private final StringRedisTemplate redis;
    private final ObjectMapper objectMapper;

    RedisBookingReadModelAdapter(StringRedisTemplate redis, ObjectMapper objectMapper) {
        this.redis = redis;
        this.objectMapper = objectMapper;
    }

    @Override
    public void add(BookingView view) {
        String json = serialize(view);
        redis.opsForSet().add(resourceKey(view.resourceId(), dateOf(view)), json);
        redis.opsForSet().add(userKey(view.userId()), json);
    }

    @Override
    public List<BookingView> findByResourceAndDate(UUID resourceId, LocalDate date) {
        return read(resourceKey(resourceId, date));
    }

    @Override
    public List<BookingView> findByUser(UUID userId) {
        return read(userKey(userId));
    }

    private List<BookingView> read(String key) {
        Set<String> members = redis.opsForSet().members(key);
        if (members == null || members.isEmpty()) {
            return List.of();
        }
        return members.stream()
                .map(this::deserialize)
                .sorted(Comparator.comparing(BookingView::startAt))
                .toList();
    }

    private LocalDate dateOf(BookingView view) {
        return view.startAt().atZone(ZoneOffset.UTC).toLocalDate();
    }

    private String resourceKey(UUID resourceId, LocalDate date) {
        return "rm:resource:" + resourceId + ":" + date;
    }

    private String userKey(UUID userId) {
        return "rm:user:" + userId;
    }

    private String serialize(BookingView view) {
        try {
            return objectMapper.writeValueAsString(view);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize BookingView", e);
        }
    }

    private BookingView deserialize(String json) {
        try {
            return objectMapper.readValue(json, BookingView.class);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to deserialize BookingView", e);
        }
    }
}
