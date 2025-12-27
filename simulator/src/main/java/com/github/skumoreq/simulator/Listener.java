package com.github.skumoreq.simulator;

/**
 * <h3>Observer Pattern</h3>
 * <p>Listener interface for objects that need to react to state changes.</p>
 * <p>Implementers of this interface are notified when an observed object changes state.
 * Typical use case is for GUI controllers that refresh the display in response to updates.</p>
 */
public interface Listener {

    /**
     * <p>Called by the observed object when its state changes.</p>
     */
    void update();
}