package com.github.skumoreq.simulator.exception;

import com.github.skumoreq.simulator.Car;
import org.jetbrains.annotations.NotNull;

/**
 * Base class for exceptions indicating an invalid car state or mechanical
 * violation.
 * <p>
 * This is a <strong>checked exception</strong>, meaning it must be either
 * caught or declared in the method signature.
 *
 * @see Car
 */
public abstract class CarException extends Exception {

    public CarException(@NotNull String message) {
        super(message);
    }
}
