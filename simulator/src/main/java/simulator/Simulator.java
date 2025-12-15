package simulator;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Simulator {
    // «««Class Constants»»»
    public static final Clutch[] CLUTCHES = {
            new Clutch("Jednotarczowe suche", 8.9, 1400.00),
            new Clutch("Z kołem dwumasowym", 12.5, 2800.00),
            new Clutch("Ceramiczne wielotarczowe", 6.8, 4500.00)
    };
    public static final Gearbox[] GEARBOXES = {
            new Gearbox("Standardowa", 35.0, 3200.00,
                    CLUTCHES[0],
                    new double[]{3.91, 2.14, 1.36, 1.03, 0.84}),
            new Gearbox("Z reduktorem drgań", 39.0, 5200.00,
                    CLUTCHES[1],
                    new double[]{3.65, 2.05, 1.35, 1.02, 0.81}),
            new Gearbox("Sportowa sekwencyjna", 31.0, 7500.00,
                    CLUTCHES[2],
                    new double[]{3.25, 2.10, 1.45, 1.12, 0.92})
    };
    public static final Engine[] ENGINES = {
            new Engine("Benzynowy 1.0", 85.0, 8000.00, 6500),
            new Engine("Benzynowy 1.6 turbo", 120.0, 15000.00, 7000),
            new Engine("Benzynowy 2.0 turbo", 140.0, 22000.00, 7500)
    };



    // «««Core Identity»»»
    private final ObservableList<Car> cars;

    // «««Runtime State»»»
    private Car selectedCar;

    // «««Constructors»»»
    public Simulator() {
        this.cars = FXCollections.observableArrayList();
        this.selectedCar = null;
    }



    // «««Basic Getters»»»
    public ObservableList<Car> getCars() {
        return this.cars;
    }
    public Car getSelectedCar() {
        return this.selectedCar;
    }



    // «««Query Methods»»»
    public Car findCarByPlateNumber(String plateNumber) {
        for (Car car: this.cars) {
            if (car.getPlateNumber().equals(plateNumber)) {
                return car;
            }
        }
        return null;
    }
    public void selectCarByPlateNumber(String plateNumber) {
        this.selectedCar = this.findCarByPlateNumber(plateNumber);
    }
    public void addCar(Car car) {
        this.cars.add(car);
    }
    public void removeCar(Car car) {
        this.cars.remove(car);
    }
    public void removeSelectedCar() {
        this.removeCar(this.selectedCar);
        this.selectedCar = null;
    }
}
