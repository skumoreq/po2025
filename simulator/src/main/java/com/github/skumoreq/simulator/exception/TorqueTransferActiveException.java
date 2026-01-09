package com.github.skumoreq.simulator.exception;

public class TorqueTransferActiveException extends CarException {

    public TorqueTransferActiveException() {
        super("Cannot start engine while torque transfer is active");
    }
}
