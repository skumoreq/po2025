package com.github.skumoreq.simulator;

/**
 * Listener interface for implementing the <strong>observer pattern</strong>.
 * <p>
 * Classes implementing this interface can be notified when an observed object
 * changes state.
 * </p>
 */
public interface Listener {

    /** Called by the observed object when its state changes. */
    void update();
}
