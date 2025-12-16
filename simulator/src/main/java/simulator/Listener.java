package simulator;

public interface Listener {
    // Called by the observed object when its state changes.
    //
    // This method should contain the logic to react to state changes in the observed object. For GUI controllers,
    // this typically means refreshing the display to show updated values.
    void update();
}
