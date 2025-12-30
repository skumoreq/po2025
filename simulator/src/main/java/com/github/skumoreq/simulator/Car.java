package com.github.skumoreq.simulator;

import com.github.skumoreq.simulator.exception.CarException;
import com.github.skumoreq.simulator.exception.ClutchEngagedException;
import com.github.skumoreq.simulator.exception.EngineStalledException;
import com.github.skumoreq.simulator.exception.GearboxNotInNeutralException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a car with a clutch, a gearbox, and an engine.
 * <p>
 * This class provides the following functionality:
 * </p>
 * <ul>
 *   <li>Runs driving simulation in a separate thread.</li>
 *   <li>Notifies registered listeners of state changes via the observer pattern.</li>
 *   <li>Tracks position, destination, engine state, and speed.</li>
 *   <li>Provides control methods for its components, which may throw appropriate
 *   {@link CarException} subclasses.</li>
 * </ul>
 *
 * @see Clutch
 * @see Gearbox
 * @see Engine
 */
public class Car extends Thread {

    // region > Thread Implementation

    private static final int THREAD_SLEEP = 50;

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                driveToDestination();
            }
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
        // Ensures no null objects or duplicate listeners are added.
        if (listener == null || listeners.contains(listener)) return;
        listeners.add(listener);
    }
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }
    private void notifyListeners() {
        // Create a copy in case a listener tries to unsubscribe during update().
        List<Listener> listenersCopy = new ArrayList<>(listeners);
        for (Listener listener : listenersCopy) listener.update();
    }
    // endregion

    // region > Constants

    public static final double WEIGHT_CONSTANT = 1000.0;
    public static final double PRICE_CONSTANT = 800.0;
    public static final double PRICE_MULTIPLIER = 1.23;
    public static final double SPEED_MULTIPLIER = 0.03;
    // endregion

    // region > Instance Fields

    private final Gearbox gearbox;
    private final Engine engine;
    private final String plateNumber;
    private final String modelName;
    private final Point position;
    private final Point destination;

    private boolean isEngineOn;
    private double speed;
    // endregion

    // region > Initialization

    public Car(Gearbox gearbox, Engine engine, String plateNumber, String modelName) {
        this.gearbox = new Gearbox(gearbox);
        this.engine = new Engine(engine);
        this.plateNumber = plateNumber;
        this.modelName = modelName;

        position = new Point(100, 100);
        destination = new Point(position);

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
    public boolean isEngineOn() {
        return isEngineOn;
    }
    public double getSpeed() {
        return speed;
    }
    // endregion

    // region > Calculations

    public double calculateTotalWeight() {
        double baseWeight = gearbox.getClutch().getWeight() + gearbox.getWeight() + engine.getWeight();
        return baseWeight + WEIGHT_CONSTANT;
    }
    public double calculateTotalPrice() {
        double basePrice = gearbox.getClutch().getPrice() + gearbox.getPrice() + engine.getPrice();
        return (basePrice + PRICE_CONSTANT) * PRICE_MULTIPLIER;
    }
    public double calculateTopSpeed() {
        return calculateSpeed(engine.getMaxRpm(), gearbox.getGearRatios()[Gearbox.NUM_GEARS - 1]);
    }
    // endregion

    // region > Display Methods

    public String getTotalWeightDisplay() {
        return String.format("%.1f kg", calculateTotalWeight());
    }
    public String getTotalPriceDisplay() {
        return String.format("%.2f zł", calculateTotalPrice());
    }
    public String getTopSpeedDisplay() {
        return String.format("%.0f km/h", calculateTopSpeed());
    }
    public String getEngineStatusDisplay() {
        return isEngineOn ? "Włączony" : "Wyłączony";
    }
    public String getSpeedDisplay() {
        return String.format("%.0f km/h", speed);
    }
    // endregion

    // region > Helper Methods

    /** @return speed from given RPM and gear ratio */
    private double calculateSpeed(double rpm, double gearRatio) {
        return gearRatio > 0.0 ? rpm / gearRatio * SPEED_MULTIPLIER : 0.0;
    }

    /** Updates the current speed using current RPM and gear ratio. */
    private void updateSpeed() {
        speed = calculateSpeed(engine.getRpm(), gearbox.getGearRatio());
    }

    /**
     * Stops the engine if RPM drops below idle.
     * Should be called anytime RPM could decrease.
     *
     * @throws EngineStalledException if the engine stalls
     */
    private void handlePossibleEngineStall() {
        if (engine.getRpm() < Engine.RPM_IDLE) {
            stopEngine();
            throw new EngineStalledException();
        }
    }
    // endregion

    // region > Control Methods

    public void startEngine() {
        if (isEngineOn) return;
        if (!gearbox.isInNeutral()) throw new GearboxNotInNeutralException();

        isEngineOn = true;
        engine.start();
        gearbox.clearPreviousGear(); // required for correct RPM adjustment after first gear change

        notifyListeners();
    }
    public void stopEngine() {
        if (!isEngineOn) return;

        isEngineOn = false;
        engine.stop();
        updateSpeed();

        notifyListeners();
    }

    public void pressClutch() {
        if (!gearbox.getClutch().isEngaged()) return;

        gearbox.getClutch().disengage();

        gearbox.updatePreviousGear();
        gearbox.updateGearRatio();

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

        position.moveTo(destination, speed, THREAD_SLEEP, 5.0);

        notifyListeners();
    }
    // endregion
}
