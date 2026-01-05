package com.github.skumoreq.simulator.exception;

public class EngineStalledException extends CarException {

    public EngineStalledException() {
        super("Engine stalled because the rotational speed fell below the idle threshold.");
    }
}
