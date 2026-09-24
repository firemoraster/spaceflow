-- Idempotent consumer ledger: one row per (consumer, message) that has been handled.
-- The row is inserted in the same transaction as the consumer's side effects, so a
-- redelivered message is recognised and skipped, while a failed attempt leaves no trace.
CREATE TABLE processed_messages (
    consumer     VARCHAR(64)  NOT NULL,
    message_id   UUID         NOT NULL,
    processed_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    PRIMARY KEY (consumer, message_id)
);

-- supports time-based pruning of old ledger entries
CREATE INDEX idx_processed_messages_processed_at ON processed_messages (processed_at);
