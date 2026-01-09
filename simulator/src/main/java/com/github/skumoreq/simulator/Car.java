package com.github.skumoreq.simulator;

import com.github.skumoreq.simulator.exception.ClutchEngagedException;
import com.github.skumoreq.simulator.exception.EngineStalledException;
import com.github.skumoreq.simulator.exception.TorqueTransferActiveException;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.github.skumoreq.simulator.CarObserver.ChangedProperty.*;

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

                // Re-calculating angle and moving in one atomic step to
                // ensure movement is always consistent with the target.
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
        if (!observers.contains(observer)) observers.add(observer);
    }

    public synchronized void removeObserver(@NotNull CarObserver observer) {
        observers.remove(observer);
    }

    public synchronized void removeAllObservers() {
        observers.clear();
    }

    public synchronized void notifyAllObservers(CarObserver.ChangedProperty @NotNull ... properties) {
        if (observers.isEmpty() || properties.length == 0) return;

        // Create a snapshot to avoid ConcurrentModificationException and ensure
        // thread safety during asynchronous notification on the JavaFX thread.
        var snapshot = List.copyOf(observers);

        Platform.runLater(() -> {
            for (var observer : snapshot)
                for (var property : properties)
                    observer.onCarUpdate(this, property);
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
    private static final double ANGLE_THRESHOLD = 1.0;
    private static final double METERS_TO_PIXELS = 10.0;
    // endregion

    // region ⮞ Instance Fields

    private final @NotNull String plateNumber;
    private final @NotNull String modelName;

    private final @NotNull Clutch clutch;
    private final @NotNull Transmission transmission;
    private final @NotNull Engine engine;

    private final @NotNull Point position;
    private final @NotNull Point destination;

    private double speed = 0.0;
    private double angle = 0.0;
    // endregion

    // region ⮞ Initialization

    public Car(
            @NotNull String plateNumber, @NotNull String modelName,
            @NotNull Transmission transmission, @NotNull Engine engine,
            @NotNull Point initialPosition
    ) {
        // Sets the plate number as the name of the thread.
        super("CarThread-" + plateNumber);

        this.plateNumber = plateNumber;
        this.modelName = modelName;

        this.transmission = new Transmission(transmission);
        this.engine = new Engine(engine);

        // Assign the clutch directly from the transmission to ensure mechanical consistency.
        clutch = this.transmission.clutch();

        this.position = new Point(initialPosition);
        this.destination = new Point(position);
    }
    // endregion

    // region ⮞ Getters

    public @NotNull String getPlateNumber() {
        return plateNumber;
    }

    public @NotNull String getModelName() {
        return modelName;
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
    // endregion

    // region ⮞ Component Accessors

    /*
     * Security Policy: Each component is returned as a generic CarComponent.
     * This restricts access to basic metadata (name, weight, price) and
     * prevents external callers from invoking internal mechanical logic (e.g.
     * shifting gears or engaging the clutch) outside the car's synchronized
     * control flow.
     *
     * To modify the vehicle's state, use the dedicated control methods
     * provided in the Control Methods region.
     */

    /**
     * @return the clutch component containing only metadata and display
     * methods.
     */
    public @NotNull CarComponent getClutch() {
        return clutch;
    }

    /**
     * @return the transmission component containing only metadata and display
     * methods.
     */
    public @NotNull CarComponent getGearbox() {
        return transmission;
    }

    /**
     * @return the engine component containing only metadata and display
     * methods.
     */
    public @NotNull CarComponent getEngine() {
        return engine;
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

    /**
     * @param rpm       must be non-negative
     * @param gearRatio must be positive to avoid division by zero
     *
     * @return Calculated speed based on given RPM and gear ratio.
     */
    private static double calculateSpeed(double rpm, double gearRatio) {
        if (rpm < 0.0)
            throw new IllegalArgumentException("RPM cannot be negative: %.2f".formatted(rpm));
        if (gearRatio <= 0.0)
            throw new IllegalArgumentException("Gear ratio must be positive: %.2f".formatted(gearRatio));

        return rpm / gearRatio * SPEED_MULTIPLIER;
    }

    /**
     * @implNote This helper does not need {@code synchronized} as long as it is
     * called exclusively from other synchronized methods of this class.
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

        notifyAllObservers(ENGINE_STATE, RPM);
    }

    public synchronized void stopEngine() {
        if (!engine.stop()) return;

        speed = 0.0;

        notifyAllObservers(ENGINE_STATE, RPM, SPEED);
    }

    public synchronized void pressClutch() {
        if (!clutch.disengage()) return;

        transmission.updatePreviousGear();

        notifyAllObservers(CLUTCH_STATE);
    }

    public synchronized void releaseClutch() throws EngineStalledException {
        if (!clutch.engage()) return;

        try {
            if (engine.adjustRpmAfterGearChange(transmission.getGearShiftDelta(), transmission.getDropFactor())) {
                if (updateSpeed()) {
                    notifyAllObservers(CLUTCH_STATE, RPM, SPEED);
                } else {
                    notifyAllObservers(CLUTCH_STATE, RPM);
                }
            } else {
                notifyAllObservers(CLUTCH_STATE);
            }
        } catch (EngineStalledException e) {
            speed = 0.0;

            notifyAllObservers(CLUTCH_STATE, ENGINE_STATE, RPM, SPEED);

            throw e;
        }
    }

    public synchronized void shiftUp() throws ClutchEngagedException {
        if (!transmission.shiftUp()) return;

        notifyAllObservers(GEAR);
    }

    public synchronized void shiftDown() throws ClutchEngagedException {
        if (!transmission.shiftDown()) return;

        notifyAllObservers(GEAR);
    }

    public synchronized void revUp(double intensity) {
        if (!engine.increaseRpm(intensity)) return;

        if (updateSpeed()) {
            notifyAllObservers(RPM, SPEED
            );
        } else {
            notifyAllObservers(RPM);
        }
    }

    public synchronized void revDown(double intensity) throws EngineStalledException {
        try {
            if (!engine.decreaseRpm(intensity)) return;

            if (updateSpeed()) {
                notifyAllObservers(RPM, SPEED);
            } else {
                notifyAllObservers(RPM);
            }
        } catch (EngineStalledException e) {
            speed = 0.0;

            notifyAllObservers(ENGINE_STATE, RPM, SPEED);

            throw e;
        }
    }

    /**
     * Updates the destination if the distance to the new coordinates exceeds
     * the specified threshold.
     * <p>
     * This threshold prevents "visual jitter" (rapid, erratic rotation) of the
     * car icon, which occurs when calculating an angle towards a point that is
     * mathematically too close to the current position.
     *
     * @implNote Uses squared distance comparison to avoid the performance cost
     * of {@link Math#sqrt(double)}.
     */
    public synchronized void updateDestination(double x, double y, double threshold) {
        if (!engine.isRunning()) return;

        if (position.squaredDistanceTo(x, y) > threshold * threshold)
            destination.set(x, y);
    }

    public synchronized void updateAngle() {
        if (!engine.isRunning()) return;

        double newAngle = position.angleTo(destination);

        // Filter out insignificant angle changes.
        if (!Double.isNaN(newAngle) && Math.abs(angle - newAngle) > ANGLE_THRESHOLD) {
            angle = newAngle;

            notifyAllObservers(ANGLE);
        }
    }

    public synchronized void driveToDestination() {
        if (!position.moveTowards(destination, speed, THREAD_SLEEP, METERS_TO_PIXELS)) return;

        notifyAllObservers(POSITION);
    }
    // endregion

    // region ⮞ Display Methods

    public @NotNull String getTotalWeightDisplay() {
        return UI_FORMAT_WEIGHT.formatted(calculateTotalWeight());
    }

    public @NotNull String getTotalPriceDisplay() {
        return UI_FORMAT_PRICE.formatted(calculateTotalPrice());
    }

    public @NotNull String getTopSpeedDisplay() {
        return UI_FORMAT_SPEED.formatted(calculateTopSpeed());
    }

    public synchronized @NotNull String getSpeedDisplay() {
        return UI_FORMAT_SPEED.formatted(speed);
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
