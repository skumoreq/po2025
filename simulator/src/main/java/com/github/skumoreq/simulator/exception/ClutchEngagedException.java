package com.github.skumoreq.simulator.exception;

public class ClutchEngagedException extends CarException {

    public ClutchEngagedException() {
        super("Cannot shift gear while the clutch is engaged");
    }
}
