/**
 * Notification module — Kafka consumer reacting to booking domain events
 * (see {@code adapters.in.messaging.BookingEventsConsumer}). Sends email/push
 * (logged stub for now). Idempotent dedupe by messageId is a planned follow-up.
 */
package com.spaceflow.notification;
