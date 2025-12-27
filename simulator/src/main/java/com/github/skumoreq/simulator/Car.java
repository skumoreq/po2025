package com.github.skumoreq.simulator;

import com.github.skumoreq.simulator.exception.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>Represents a car with a clutch, a gearbox, and an engine.</p>
 * <p>This class provides the following functionality:</p>
 * <ul>
 *   <li>Tracks position, destination, engine state, and speed.</li>
 *   <li>Runs driving simulation in a separate <b>thread</b>.</li>
 *   <li>Notifies registered listeners of state changes via the <b>observer pattern</b>.</li>
 *   <li>Provides control methods for its components, which may throw appropriate
 *   {@link CarException} subclasses.</li>
 * </ul>
 */
public class Car extends Thread {
    // region > Thread Implementation

    private static final int THREAD_SLEEP = 100;

    @Override
    public void run() {
        while (true) {
            synchronized (this) { driveToDestination(); }
            try { //noinspection BusyWait
                Thread.sleep(THREAD_SLEEP);
            }
            catch (InterruptedException e) {
                break; // terminate thread on interrupt
            }
        }
    }
    // endregion

    // region > Observer Pattern

    private final List<Listener> listeners = new ArrayList<>();

    public void addListener(Listener listener) {
        // Ensures no null objects or duplicate listeners
        if (listener == null || listeners.contains(listener)) return;
        listeners.add(listener);
    }
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }
    private void notifyListeners() {
        // Create a copy in case a listener tries to unsubscribe during update()
        List<Listener> listenersCopy = new ArrayList<>(listeners);
        for (Listener listener : listenersCopy) listener.update();
    }
    // endregion

    // region > Class Constants

    public static final double WEIGHT_CONSTANT = 1000.0;
    public static final double SPEED_CONSTANT = 0.03;
    // endregion

    // region > Class Methods

    public static double calculateSpeed(double rpm, double gearRatio) {
        return gearRatio > 0.0 ? rpm / gearRatio * SPEED_CONSTANT : 0.0;
    }
    // endregion

    // region > Instance Identity

    private final Gearbox gearbox;
    private final Engine engine;
    private final String plateNumber;
    private final String modelName;
    private final Point position;
    private final Point destination;
    // endregion

    // region > Instance State

    private boolean isEngineOn;
    private double speed;
    // endregion

    // region > Initialization

    public Car(Gearbox gearbox, Engine engine, String plateNumber, String modelName) {
        this.gearbox = new Gearbox(gearbox);
        this.engine = new Engine(engine);
        this.plateNumber = plateNumber;
        this.modelName = modelName;

        position = new Point();
        destination = new Point();

        isEngineOn = false;
        speed = 0.0;

        start(); // starts the thread after object is initialized
    }
    // endregion

    // region > Getters

    public Gearbox getGearbox() {
        return gearbox;
    }
    public Engine getEngine() {
        return engine;
    }
    public String getPlateNumber() {
        return plateNumber;
    }
    public String getModelName() {
        return modelName;
    }
    public Point getPosition() {
        return position;
    }
    public Point getDestination() {
        return destination;
    }
    // endregion

    // region > Calculations

    public double calculateTopSpeed() {
        return calculateSpeed(engine.getMaxRpm(), gearbox.getGearRatios()[Gearbox.NUM_GEARS - 1]);
    }
    public double calculateTotalWeight() {
        return gearbox.getClutch().getWeight() + gearbox.getWeight() + engine.getWeight() + WEIGHT_CONSTANT;
    }
    public double calculateTotalPrice() {
        return gearbox.getClutch().getPrice() + gearbox.getPrice() + engine.getPrice();
    }
    // endregion

    // region > Display Methods

    public String getSpeedDisplay() {
        return String.format("%.0f km/h", speed);
    }
    public String getTopSpeedDisplay() {
        return String.format("%.0f km/h", calculateTopSpeed());
    }
    public String getTotalWeightDisplay() {
        return String.format("%.2f kg", calculateTotalWeight());
    }
    public String getTotalPriceDisplay() {
        return String.format("%.2f zł", calculateTotalPrice());
    }
    // endregion

    // region > Control Methods

    private void updateSpeed() {
        speed = calculateSpeed(engine.getRpm(), gearbox.getGearRatio());
    }
    private void handlePossibleEngineStall() {
        if (engine.getRpm() < Engine.RPM_IDLE) {
            stopEngine();
            throw new EngineStalledException();
        }
    }

    public void startEngine() {
        if (isEngineOn) return;
        if (!gearbox.isInNeutral()) throw new GearboxNotInNeutralException();

        isEngineOn = true;
        engine.start();

        gearbox.clearPreviousGear(); // sets previousGear to 0, so that adjustRpmAfterGearChange works correctly

        notifyListeners();
    }
    public void stopEngine() {
        if (!isEngineOn) return;

        isEngineOn = false;
        engine.stop();

        updateSpeed(); // sets speed to 0, because after stop() rpm is set to 0

        notifyListeners();
    }

    public void pressClutch() {
        if (!gearbox.getClutch().isEngaged()) return;

        gearbox.getClutch().disengage();

        gearbox.updatePreviousGear();
        gearbox.updateGearRatio(); // sets gear ratio to 0.0, because car is in neutral

        notifyListeners();
    }
    public void releaseClutch() {
        if (gearbox.getClutch().isEngaged()) return;

        gearbox.getClutch().engage();

        gearbox.updateGearRatio();

        if (isEngineOn && !gearbox.isInNeutral()) {
            engine.adjustRpmAfterGearChange(gearbox.getPreviousGear(), gearbox.getGear());
            handlePossibleEngineStall();
            updateSpeed();
        }

        notifyListeners();
    }

    public void shiftUp() {
        if (gearbox.getClutch().isEngaged()) throw new ClutchEngagedException();

        gearbox.shiftUp();

        notifyListeners();
    }
    public void shiftDown() {
        if (gearbox.getClutch().isEngaged()) throw new ClutchEngagedException();

        gearbox.shiftDown();

        notifyListeners();
    }

    public void revUp() {
        if (!isEngineOn) return;

        engine.increaseRpm();
        if (!gearbox.isInNeutral()) updateSpeed();

        notifyListeners();
    }
    public void revDown() {
        if (!isEngineOn) return;

        engine.decreaseRpm();
        handlePossibleEngineStall();
        if (!gearbox.isInNeutral()) updateSpeed();

        notifyListeners();
    }

    public void driveToDestination() {
        if (!isEngineOn) return;

        position.moveTo(destination, speed, THREAD_SLEEP);

        notifyListeners();
    }
    // endregion
}