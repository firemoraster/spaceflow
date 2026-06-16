package com.spaceflow.shared.exception;

/** Thrown when a requested time slot overlaps an existing active booking. */
public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}
