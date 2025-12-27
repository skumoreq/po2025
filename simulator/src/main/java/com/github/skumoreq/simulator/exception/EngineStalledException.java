package com.github.skumoreq.simulator.exception;

/**
 * <p>Thrown when the engine rpm falls below the idle threshold, causing a stall.</p>
 */
public class EngineStalledException extends CarException {

    public EngineStalledException() {
        super("Engine stall occurred due to rpm below idle threshold.");
    }
}