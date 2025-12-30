package com.github.skumoreq.simulator.exception;

import com.github.skumoreq.simulator.Gearbox;

/**
 * Thrown when attempting to start the car's engine while the gearbox is not in
 * the neutral state.
 *
 * @see Gearbox#isInNeutral()
 */
public class GearboxNotInNeutralException extends CarException {

    public GearboxNotInNeutralException() {
        super("Engine start attempted while the gearbox is not in neutral.");
    }
}
