package com.github.skumoreq.simulator.exception;

import com.github.skumoreq.simulator.Engine;

/**
 * Thrown when engine RPM falls below its defined threshold
 * ({@value Engine#RPM_IDLE} RPM).
 */
public class EngineStalledException extends CarException {

    public EngineStalledException() {
        super("Engine stalled due to RPM falling below the idle threshold.");
    }
}
