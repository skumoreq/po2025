package com.github.skumoreq.simulator.exception;

import com.github.skumoreq.simulator.Car;

/**
 * Base class for all {@link Car}-related exceptions.
 * <p>
 * Thrown when an operation fails due to an invalid car state.
 * </p>
 */
public abstract class CarException extends RuntimeException {

    /** @param message developer-oriented message for debugging purposes */
    public CarException(String message) {
        super(message);
    }
}
