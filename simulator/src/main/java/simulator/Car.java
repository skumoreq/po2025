package simulator;

public class Car {
    // «««Class Constants»»»
    public static final double BASE_CAR_WEIGHT = 1000.0;
    public static final double SPEED_CONSTANT = 0.03;

    // «««Class Methods»»»
    public static double getSpeed(double RPM, double gearRatio) {
        return gearRatio > 0.0 ? (RPM * Car.SPEED_CONSTANT) / gearRatio : 0.0;
    }



    // «««Core Identity»»»
    private final Position position;
    private final Gearbox gearbox;
    private final Engine engine;
    private final String plateNumber;
    private final String modelName;

    // «««Runtime State»»»
    private boolean isEngineOn;

    // «««Constructors»»»
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
                engine.getMaxRPM()
        );

        this.plateNumber = plateNumber;
        this.modelName = modelName;
        this.isEngineOn = false;
    }



    // «««Basic Getters»»»
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
    public boolean getIsEngineOn() {
        return this.isEngineOn;
    }

    // «««Calculations»»»
    public double getTotalWeight() {
        return this.gearbox.getClutch().getWeight() + this.gearbox.getWeight()
                + this.engine.getWeight() + Car.BASE_CAR_WEIGHT;
    }
    public double getTotalPrice() {
        return this.gearbox.getClutch().getPrice() + this.gearbox.getPrice() + this.engine.getPrice();
    }
    public double getMaxSpeed() {
        return Car.getSpeed(this.engine.getMaxRPM(), this.gearbox.getGearRatios()[Gearbox.NUM_GEARS - 1]);
    }
    public double getCurrentSpeed() {
        return Car.getSpeed(this.engine.getCurrentRPM(), this.gearbox.getCurrentGearRatio());
    }

    // «««String Representations»»»
    public String isEngineOnToString() {
        return this.isEngineOn ? "włączony" : "wyłączony";
    }
    public String totalWeightToString() {
        return String.format("%.2f kg", this.getTotalWeight());
    }
    public String totalPriceToString() {
        return String.format("%.2f PLN", this.getTotalPrice());
    }
    public String maxSpeedToString() {
        return String.format("%.0f km/h", this.getMaxSpeed());
    }
    public String currentSpeedToString() {
        return String.format("%.0f km/h", this.getCurrentSpeed());
    }



    // «««Action Methods»»»
    // !!! Not implemented yet !!!
    public void switchOn() {

    }
    public void switchOff() {

    }
    public void driveTo(Position destination) {

    }
}
