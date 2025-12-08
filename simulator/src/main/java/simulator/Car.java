package simulator;

public class Car {
    private final Position position;
    private final Gearbox gearbox;
    private final Engine engine;
    private final String plateNumber;
    private final String modelName;
    private final int maxSpeed;
    private boolean isEngineOn;

    // Constrcutor
    public Car(Position position, Gearbox gearbox, Engine engine, String plateNumber, String modelName) {
        this.position = position;
        this.gearbox = gearbox;
        this.engine = engine;
        this.plateNumber = plateNumber;
        this.modelName = modelName;
        this.maxSpeed = 200;
        this.isEngineOn = false;
    }

    // Basic getters
    public Position getPosition() { return this.position; }
    public Gearbox getGearbox() { return this.gearbox; }
    public Engine getEngine() { return this.engine; }
    public String getPlateNumber() { return this.plateNumber; }
    public String getModelName() { return this.modelName; }
    public int getMaxSpeed() { return this.maxSpeed; }
    public boolean getIsEngineOn() { return this.isEngineOn; }

    // Advanced getters - not yet implemented
    public double getWeight() { return 0; }
    public double getCurrentSpeed() { return 0; }

    // Car control methods - not yet implemented
    public void switchOn() {}
    public void switchOff() {}
    public void driveTo(Position destination) {}
}
