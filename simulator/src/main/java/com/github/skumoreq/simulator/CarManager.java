package com.github.skumoreq.simulator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the collection of cars and provides quick access to a selected car.
 * <p>
 * The cars are stored in an {@link javafx.collections.ObservableList}, so GUI
 * elements like combo boxes can automatically update when cars are added or
 * removed.
 * </p>
 * <p>
 * This class also holds preconfigured clutch, gearbox, and engine components
 * as public constants for easy car creation.
 * </p>
 */
public class CarManager {

    // region > Constants

    public static final Clutch[] CLUTCHES = {
            new Clutch("Jednotarczowe suche", 8.9, 1400.0),
            new Clutch("Z kołem dwumasowym", 12.5, 2800.0),
            new Clutch("Ceramiczne wielotarczowe", 6.8, 4500.0)
    };
    public static final Gearbox[] GEARBOXES = {
            new Gearbox("Standardowa", 35.0, 3200.0,
                    CLUTCHES[0],
                    new double[]{3.91, 2.14, 1.36, 1.03, 0.84}),
            new Gearbox("Z reduktorem drgań", 39.0, 5200.0,
                    CLUTCHES[1],
                    new double[]{3.65, 2.05, 1.35, 1.02, 0.81}),
            new Gearbox("Sportowa sekwencyjna", 31.0, 7500.0,
                    CLUTCHES[2],
                    new double[]{3.25, 2.10, 1.45, 1.12, 0.92})
    };
    public static final Engine[] ENGINES = {
            new Engine("Benzynowy 1.0", 85.0, 8000.0, 6500.0),
            new Engine("Benzynowy 1.6 turbo", 120.0, 15000.0, 7000.0),
            new Engine("Benzynowy 2.0 turbo", 140.0, 22000.0, 7500.0)
    };
    // endregion

    // region > Instance Fields

    private final ObservableList<Car> cars;

    private Car selectedCar;
    // endregion

    // region > Initialization

    public CarManager() {
        cars = FXCollections.observableArrayList();
        selectedCar = null;
    }
    // endregion

    // region > Accessors

    public ObservableList<Car> getCars() {
        return cars;
    }
    public Car getSelectedCar() {
        return selectedCar;
    }
    // endregion

    // region > Query Methods

    public Car getCarByPlateNumber(String plateNumber) {
        for (Car car: cars) if (car.getPlateNumber().equals(plateNumber)) return car;
        return null;
    }
    public List<String> getAllPlateNumbers() {
        List<String> allPlateNumbers = new ArrayList<>();
        for (Car car : cars) allPlateNumbers.add(car.getPlateNumber());
        return allPlateNumbers;
    }

    public void setSelectedCarByPlateNumber(String plateNumber) {
        selectedCar = getCarByPlateNumber(plateNumber);
    }

    public void addCar(Car car) {
        if (car == null || cars.contains(car)) return;
        cars.add(car);
    }
    public void removeSelectedCar() {
        if (selectedCar == null) return;

        selectedCar.interrupt(); // stop the car's background thread first

        cars.remove(selectedCar);
        selectedCar = null;
    }
    // endregion
}
