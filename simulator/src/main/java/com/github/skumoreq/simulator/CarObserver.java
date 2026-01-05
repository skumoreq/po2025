package com.github.skumoreq.simulator;

import org.jetbrains.annotations.NotNull;

/**
 * Defines a contract for objects that need to be notified about changes
 * in a car instance property.
 * <p>
 * This interface is a part of the <b>Observer pattern</b>
 * implementation.
 *
 * @see Car
 */
public interface CarObserver {

    enum ChangedProperty {
        CLUTCH_STATE, ENGINE_STATE, GEAR, RPM, SPEED, POSITION, ANGLE
    }

    /**
     * Invoked whenever the car instance dispatches a notification regarding a
     * change in one of its internal properties.
     *
     * @param car      the car instance that triggered the update
     * @param property the specific property that has been modified
     */
    void onCarUpdate(@NotNull Car car, @NotNull ChangedProperty property);
}
