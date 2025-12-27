package com.github.skumoreq.simulator.exception;

/**
 * <p>Base class for all car-related exceptions.</p>
 * <p>Indicates an invalid car state or improper action within the simulator domain.
 * Exception messages are intended for developers and debugging purposes, not end users.</p>
 */
public abstract class CarException extends RuntimeException {

    public CarException(String message) {
        super(message);
    }
}