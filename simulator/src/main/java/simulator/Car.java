package simulator;

import java.util.ArrayList;
import java.util.List;

public class Car {
    // «««Observer Pattern: List of registered listeners»»»
    private final List<Listener> listeners = new ArrayList<>();

    public void addListener(Listener listener) {
        // Ensures no null Objects or duplicate listeners
        if (listener != null && !this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }
    public void removeListener(Listener listener) {
        this.listeners.remove(listener);
    }
    private void notifyListeners() {
        // Create a copy in case a listener tries to unsubscribe during update()
        List<Listener> listenersCopy = new ArrayList<>(this.listeners);
        for (Listener listener : listenersCopy) {
            listener.update();
        }
    }



    // «««Class Constants»»»
    public static final double WEIGHT_CONSTANT = 1000.0;
    public static final double SPEED_CONSTANT = 0.03;

    // «««Class Methods»»»
    public static double calculateSpeed(double rpm, double gearRatio) {
        return gearRatio > 0.0 ? (rpm * Car.SPEED_CONSTANT) / gearRatio : 0.0;
    }



    // «««Core Identity»»»
    private final Position position;
    private final Gearbox gearbox;
    private final Engine engine;
    private final String plateNumber;
    private final String modelName;

    // «««Dynamic State»»»
    private boolean engineOn = false;
    private double speed = 0.0;

    // «««Initialization»»»
    public Car(Position position, Gearbox gearbox, Engine engine, String plateNumber, String modelName) {
        this.position = position;

        // Clone gearbox Object
        this.gearbox = new Gearbox(
                gearbox.getName(),
                gearbox.getWeight(),
                gearbox.getPrice(),
                gearbox.getClutch(),
                gearbox.getGearRatios()
        );

        // Clone engine Object
        this.engine = new Engine(
                engine.getName(),
                engine.getWeight(),
                engine.getPrice(),
                engine.getMaxRpm()
        );

        this.plateNumber = plateNumber;
        this.modelName = modelName;
    }



    // «««Accessors»»»
    public Position getPosition() {
        return this.position;
    }
    public Gearbox getGearbox() {
        return this.gearbox;
    }
    public Engine getEngine() {
        return this.engine;
    }
    public String getPlateNumber() {
        return this.plateNumber;
    }
    public String getModelName() {
        return this.modelName;
    }
    public double getSpeed() {
        return this.speed;
    }

    // «««Calculations»»»
    public double calculateTopSpeed() {
        return Car.calculateSpeed(this.engine.getMaxRpm(), this.gearbox.getGearRatios()[Gearbox.NUM_GEARS - 1]);
    }
    public double calculateTotalWeight() {
        return this.gearbox.getClutch().getWeight() + this.gearbox.getWeight()
                + this.engine.getWeight() + Car.WEIGHT_CONSTANT;
    }
    public double calculateTotalPrice() {
        return this.gearbox.getClutch().getPrice() + this.gearbox.getPrice() + this.engine.getPrice();
    }

    // «««Display Methods»»»
    public String getSpeedText() {
        return String.format("%.0f km/h", this.getSpeed());
    }
    public String getTopSpeedText() {
        return String.format("%.0f km/h", this.calculateTopSpeed());
    }
    public String getTotalWeightText() {
        return String.format("%.2f kg", this.calculateTotalWeight());
    }
    public String getTotalPriceText() {
        return String.format("%.2f PLN", this.calculateTotalPrice());
    }


    // «««Control Methods»»»
    private void updateSpeed() {
        this.speed = Car.calculateSpeed(this.engine.getRpm(), this.gearbox.getGearRatio());
    }
    private void handleEngineStall() {
        if (this.engine.getRpm() < Engine.RPM_IDLE) {
            System.out.println("Zbyt niskie obroty. Silnik zgasł.");
            this.stopEngine();
        }
    }

    public void startEngine() {
        if (this.engineOn) return;

        if (this.gearbox.isInNeutral()) {
            this.gearbox.clearPreviousGear();

            this.engineOn = true;
            this.engine.startIdle();
        } else {
            System.out.println("Przed uruchomieniem silnika wrzuć luz (sprzęgło lub bieg).");
        }

        this.notifyListeners();
    }
    public void stopEngine() {
        if (!this.engineOn) return;

        this.engineOn = false;
        this.engine.stop();
        this.updateSpeed();

        this.notifyListeners();
    }
    public void pressClutch() {
        if (!this.gearbox.getClutch().isEngaged()) return;

        this.gearbox.getClutch().disengage();

        this.gearbox.updateGearRatio();
        this.gearbox.storePreviousGear();

        this.notifyListeners();
    }
    public void releaseClutch() {
        if (this.gearbox.getClutch().isEngaged()) return;

        this.gearbox.getClutch().engage();

        this.gearbox.updateGearRatio();

        if (this.engineOn && !this.gearbox.isInNeutral()) {
            this.engine.adjustRpmForGearChange(this.gearbox.getPreviousGear(), this.gearbox.getGear());
            this.handleEngineStall();
            this.updateSpeed();
        }

        this.notifyListeners();
    }
    public void shiftUp() {
        if (this.engineOn && this.gearbox.getClutch().isEngaged()) {
            System.out.println("Wciśnij sprzęgło przed zmianą biegu.");
        } else {
            this.gearbox.shiftUp();
        }

        this.notifyListeners();
    }
    public void shiftDown() {
        if (this.engineOn && this.gearbox.getClutch().isEngaged()) {
            System.out.println("Wciśnij sprzęgło przed zmianą biegu.");
        } else {
            this.gearbox.shiftDown();
        }

        this.notifyListeners();
    }
    public void revUp() {
        if (!this.engineOn) return;

        this.engine.increaseRpm();

        if (!this.gearbox.isInNeutral()) this.updateSpeed();

        this.notifyListeners();
    }
    public void revDown() {
        if (!this.engineOn) return;

        this.engine.decreaseRpm();
        this.handleEngineStall();

        if (!this.gearbox.isInNeutral()) this.updateSpeed();

        this.notifyListeners();
    }
}
