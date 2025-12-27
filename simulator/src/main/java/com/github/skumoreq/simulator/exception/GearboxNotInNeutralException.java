package com.github.skumoreq.simulator.exception;

/**
 * <p>Thrown when engine start is attempted while the gearbox is not in neutral.</p>
 */
public class GearboxNotInNeutralException extends CarException {

    public GearboxNotInNeutralException() {
        super("Engine start attempted while gearbox is not in neutral.");
    }
}