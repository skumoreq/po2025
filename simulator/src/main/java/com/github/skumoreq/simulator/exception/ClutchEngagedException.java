package com.github.skumoreq.simulator.exception;

/** Thrown when attempting to shift gears while the clutch is mechanically engaged. */
public class ClutchEngagedException extends CarException {

    public ClutchEngagedException() {
        super("Gear shift attempted while the clutch is engaged.");
    }
}
