package com.github.skumoreq.simulator;

import com.github.skumoreq.simulator.exception.ClutchEngagedException;
import com.github.skumoreq.simulator.exception.EngineStalledException;
import com.github.skumoreq.simulator.exception.TorqueTransferActiveException;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Core mechanical logic and simulation engine for a car entity.
 * <p>
 * This class acts as a central controller for car components ({@link Engine},
 * {@link Transmission}, {@link Clutch}) and manages the vehicle's state in a
 * multithreaded environment.
 * <p>
 * The implementation focuses on three primary systems:
 * <ul>
 * <li><b>Simulation Engine:</b> A dedicated background thread handles real-time
 * movement calculations using a wait/notify mechanism to minimize CPU usage
 * when the car is idle.</li>
 * <li><b>Thread-Safe Wrapper:</b> Implements a strict synchronization policy.
 * As a gateway to internal components, it ensures that all state changes are
 * atomic and visible across threads.</li>
 * <li><b>State Observation:</b> Implements the Observer pattern to notify
 * listeners about property changes (e.g., speed, RPM). Notifications are
 * dispatched asynchronously on the JavaFX Application Thread.</li>
 * </ul>
 *
 * @see CarComponent
 * @see CarObserver
 *
 * @version 1.0
 * @author skumoreq
 */

    public class Car extends Thread {

    // region ⮞ Thread Execution

    public static final long THREAD_SLEEP = 20L;

    /**
     * Starts in a paused state. Using {@code volatile} ensures immediate
     * visibility across threads and prevents JIT caching of the state.
     */
    private volatile boolean paused = true;

    public synchronized void pause() {
        paused = true;
    }

    public synchronized void resume() {
        paused = false;
        notifyAll();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            synchronized (this) {
                while (paused) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                // Re-calculating angle and moving in one atomic step
                // to ensure movement is always consistent with the target.
                updateAngle();
                driveToDestination();
            }

            try {
                //noinspection BusyWait
                Thread.sleep(THREAD_SLEEP);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    // endregion

    // region ⮞ Observer Logic

    private final @NotNull List<CarObserver> observers = new ArrayList<>();

    public synchronized void addObserver(@NotNull CarObserver observer) {
        // Ensures no duplicate observers are added.
        if (!observers.contains(observer))
            observers.add(observer);
    }

    public synchronized void removeObserver(@NotNull CarObserver observer) {
        observers.remove(observer);
    }

    public synchronized void removeAllObservers() {
        observers.clear();
    }

    public synchronized void notifyObservers(CarObserver.ChangedProperty @NotNull ... properties) {
        if (observers.isEmpty() || properties.length == 0) return;

        // Create a snapshot to avoid ConcurrentModificationException and ensure
        // thread safety during asynchronous notification on the JavaFX thread.
        List<CarObserver> snapshot = List.copyOf(observers);

        Platform.runLater(() -> {
            for (CarObserver observer : snapshot) {
                for (CarObserver.ChangedProperty property : properties) {
                    observer.onCarUpdate(this, property);
                }
            }
        });
    }
    // endregion

    // region ⮞ Constants

    private static final String UI_FORMAT_WEIGHT = "%.1f kg";
    private static final String UI_FORMAT_PRICE = "%.2f zł";
    private static final String UI_FORMAT_SPEED = "%.0f km/h";

    private static final double WEIGHT_CONSTANT = 1000.0;
    private static final double PRICE_CONSTANT = 800.0;
    private static final double PRICE_MULTIPLIER = 1.23;

    private static final double SPEED_MULTIPLIER = 0.03;
    private static final double UNIT_SCALE = 10.0;
    // endregion

    // region ⮞ Instance Fields

    private final @NotNull String plateNumber;
    private final @NotNull String modelName;

    private final @NotNull Clutch clutch;
    private final @NotNull Transmission transmission;
    private final @NotNull Engine engine;

    private final @NotNull Point position = new Point();
    private final @NotNull Point destination = new Point();

    private double speed = 0.0;
    private double angle = 0.0;
    // endregion

    // region ⮞ Initialization

    public Car(
            @NotNull String plateNumber, @NotNull String modelName,
            @NotNull Transmission transmission, @NotNull Engine engine
    ) {
        // Sets the plate number as the name of the thread.
        super("CarThread-" + plateNumber);

        this.plateNumber = plateNumber;
        this.modelName = modelName;

        this.transmission = new Transmission(transmission);
        this.engine = new Engine(engine);

        // Assign the clutch directly from the transmission to ensure
        // mechanical consistency.
        clutch = this.transmission.clutch();
    }
    // endregion

    // region ⮞ Getters & Setters

    public @NotNull String getPlateNumber() {
        return plateNumber;
    }

    public @NotNull String getModelName() {
        return modelName;
    }

    public @NotNull CarComponent getClutch() {
        return clutch;
    }

    public @NotNull CarComponent getGearbox() {
        return transmission;
    }

    public @NotNull CarComponent getEngine() {
        return engine;
    }

    public synchronized double getPositionX() {
        return position.getX();
    }

    public synchronized double getPositionY() {
        return position.getY();
    }

    public synchronized double getSpeed() {
        return speed;
    }

    public synchronized double getAngle() {
        return angle;
    }

    public synchronized void setPosition(double x, double y) {
        position.set(x, y);
    }

    public synchronized void setDestination(double x, double y) {
        // Updates the destination only if it is beyond an 8-unit threshold.
        // This prevents visual jitter in the CarIcon rotation, as the angleTo
        // method can produce erratic values when the distance to the target
        // is negligible.
        if (position.squaredDistanceTo(x, y) >= 64.0) // 8² = 64
            destination.set(x, y);
    }
    // endregion

    // region ⮞ Calculations

    public double calculateTotalWeight() {
        double baseWeight = clutch.getWeight() + transmission.getWeight() + engine.getWeight();
        return baseWeight + WEIGHT_CONSTANT;
    }

    public double calculateTotalPrice() {
        double basePrice = clutch.getPrice() + transmission.getPrice() + engine.getPrice();
        return (basePrice + PRICE_CONSTANT) * PRICE_MULTIPLIER;
    }

    public double calculateTopSpeed() {
        return calculateSpeed(engine.getMaxRpm(), transmission.getGearRatio(transmission.getGearCount()));
    }
    // endregion

    // region ⮞ Helper Methods

    private static double calculateSpeed(double rpm, double ratio) {
        return ratio > 0.0 ? rpm / ratio * SPEED_MULTIPLIER : 0.0;
    }

    /**
     * @implNote This helper does not need {@code synchronized} as long as it
     * is called exclusively from other synchronized methods of this class.
     */
    private boolean updateSpeed() {
        if (!transmission.isTorqueTransferred()) return false;

        speed = calculateSpeed(engine.getRpm(), transmission.getEffectiveRatio());

        return true;
    }
    // endregion

    // region ⮞ Control Methods

    public synchronized void startEngine() throws TorqueTransferActiveException {
        if (!engine.start(transmission.isTorqueTransferred())) return;

        transmission.clearPreviousGear();

        notifyObservers(CarObserver.ChangedProperty.ENGINE_STATE,
                        CarObserver.ChangedProperty.RPM);
    }

    public synchronized void stopEngine() {
        if (!engine.stop()) return;

        speed = 0.0;

        notifyObservers(CarObserver.ChangedProperty.ENGINE_STATE,
                        CarObserver.ChangedProperty.RPM,
                        CarObserver.ChangedProperty.SPEED);
    }

    public synchronized void pressClutch() {
        if (!clutch.disengage()) return;

        transmission.updatePreviousGear();

        notifyObservers(CarObserver.ChangedProperty.CLUTCH_STATE);
    }

    public synchronized void releaseClutch() throws EngineStalledException {
        if (!clutch.engage()) return;

        try {
            if (engine.adjustRpmAfterGearChange(transmission.getGearShiftDelta())) {
                if (updateSpeed()) {
                    notifyObservers(CarObserver.ChangedProperty.CLUTCH_STATE,
                                    CarObserver.ChangedProperty.RPM,
                                    CarObserver.ChangedProperty.SPEED);
                } else {
                    notifyObservers(CarObserver.ChangedProperty.CLUTCH_STATE,
                                    CarObserver.ChangedProperty.RPM);
                }
            } else {
                notifyObservers(CarObserver.ChangedProperty.CLUTCH_STATE);
            }
        } catch (EngineStalledException e) {
            speed = 0.0;

            notifyObservers(CarObserver.ChangedProperty.CLUTCH_STATE,
                            CarObserver.ChangedProperty.ENGINE_STATE,
                            CarObserver.ChangedProperty.RPM,
                            CarObserver.ChangedProperty.SPEED);

            throw e;
        }
    }

    public synchronized void shiftUp() throws ClutchEngagedException {
        if (!transmission.shiftUp()) return;

        notifyObservers(CarObserver.ChangedProperty.GEAR);
    }

    public synchronized void shiftDown() throws ClutchEngagedException {
        if (!transmission.shiftDown()) return;

        notifyObservers(CarObserver.ChangedProperty.GEAR);
    }

    public synchronized void revUp(double intensity) {
        if (!engine.increaseRpm(intensity)) return;

        if (updateSpeed()) {
            notifyObservers(CarObserver.ChangedProperty.RPM,
                            CarObserver.ChangedProperty.SPEED);
        } else {
            notifyObservers(CarObserver.ChangedProperty.RPM);
        }
    }

    public synchronized void revDown(double intensity) throws EngineStalledException {
        try {
            if (!engine.decreaseRpm(intensity)) return;

            if (updateSpeed()) {
                notifyObservers(CarObserver.ChangedProperty.RPM,
                                CarObserver.ChangedProperty.SPEED);
            } else {
                notifyObservers(CarObserver.ChangedProperty.RPM);
            }
        } catch (EngineStalledException e) {
            speed = 0.0;

            notifyObservers(CarObserver.ChangedProperty.ENGINE_STATE,
                            CarObserver.ChangedProperty.RPM,
                            CarObserver.ChangedProperty.SPEED);

            throw e;
        }
    }

    public synchronized void updateAngle() {
        double angle = position.angleTo(destination);

        if (Double.isNaN(angle)) return;

        this.angle = angle;

        notifyObservers(CarObserver.ChangedProperty.ANGLE);
    }

    public synchronized void driveToDestination() {
        if (!position.moveTowards(destination, speed, THREAD_SLEEP, UNIT_SCALE)) return;

        notifyObservers(CarObserver.ChangedProperty.POSITION);
    }
    // endregion

    // region ⮞ Display Methods

    public @NotNull String getTotalWeightDisplay() {
        return String.format(UI_FORMAT_WEIGHT, calculateTotalWeight());
    }

    public @NotNull String getTotalPriceDisplay() {
        return String.format(UI_FORMAT_PRICE, calculateTotalPrice());
    }

    public @NotNull String getTopSpeedDisplay() {
        return String.format(UI_FORMAT_SPEED, calculateTopSpeed());
    }

    public synchronized @NotNull String getSpeedDisplay() {
        return String.format(UI_FORMAT_SPEED, speed);
    }

    public synchronized @NotNull String getClutchStateDisplay() {
        return clutch.getClutchStateDisplay();
    }

    public synchronized @NotNull String getGearDisplay() {
        return transmission.getGearDisplay();
    }

    public synchronized @NotNull String getEngineStateDisplay() {
        return engine.getEngineStateDisplay();
    }

    public synchronized @NotNull String getRpmDisplay() {
        return engine.getRpmDisplay();
    }
    // endregion
}
