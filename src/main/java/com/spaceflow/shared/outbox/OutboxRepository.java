package com.spaceflow.shared.outbox;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OutboxRepository extends JpaRepository<OutboxMessage, UUID> {

    /** Oldest unpublished messages, capped to keep each relay batch bounded. */
    List<OutboxMessage> findTop100ByPublishedFalseOrderByCreatedAtAsc();
}
