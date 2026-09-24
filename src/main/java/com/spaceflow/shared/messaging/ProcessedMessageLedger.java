package com.spaceflow.shared.messaging;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Processed-message ledger backing idempotent consumers. Claiming a message is a
 * single atomic insert keyed by (consumer, messageId), so concurrent redeliveries
 * race safely: exactly one caller wins. Must run inside the consumer's transaction
 * so a failed handler rolls the claim back and the message is retried.
 */
@Component
public class ProcessedMessageLedger {

    private static final String CLAIM_SQL = """
            INSERT INTO processed_messages (consumer, message_id)
            VALUES (?, ?)
            ON CONFLICT DO NOTHING
            """;

    private final JdbcTemplate jdbc;

    public ProcessedMessageLedger(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /** @return {@code true} if this is the first time {@code consumer} sees the message. */
    @Transactional(propagation = Propagation.MANDATORY)
    public boolean claim(String consumer, UUID messageId) {
        return jdbc.update(CLAIM_SQL, consumer, messageId) == 1;
    }
}
