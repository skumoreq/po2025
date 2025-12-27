package com.github.skumoreq.simulator.exception;

/**
 * <p>Thrown when a gear shift is attempted while the clutch is engaged.</p>
 */
public class ClutchEngagedException extends CarException {

    public ClutchEngagedException() {
        super("Gear shift attempted while clutch is engaged.");
    }
}