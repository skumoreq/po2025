package com.github.skumoreq.simulator.exception;

public class EngineStalledException extends CarException {

    public EngineStalledException() {
        super("Engine stalled: RPM fell below idle threshold");
    }
}
