-- SpaceFlow initial schema (write-side)

CREATE TABLE bookings (
    id          UUID         PRIMARY KEY,
    resource_id UUID         NOT NULL,
    user_id     UUID         NOT NULL,
    start_at    TIMESTAMPTZ  NOT NULL,
    end_at      TIMESTAMPTZ  NOT NULL,
    status      VARCHAR(16)  NOT NULL,
    version     BIGINT       NOT NULL DEFAULT 0,
    CONSTRAINT chk_booking_range CHECK (end_at > start_at)
);

-- speeds up overlap checks per resource
CREATE INDEX idx_bookings_resource_time ON bookings (resource_id, start_at, end_at);

-- Transactional Outbox: events written in the same tx as the aggregate,
-- relayed to Kafka by a poller (next iteration).
CREATE TABLE outbox (
    id           UUID         PRIMARY KEY,
    aggregate_id UUID         NOT NULL,
    event_type   VARCHAR(64)  NOT NULL,
    payload      JSONB        NOT NULL,
    published    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE INDEX idx_outbox_unpublished ON outbox (published, created_at) WHERE published = FALSE;
