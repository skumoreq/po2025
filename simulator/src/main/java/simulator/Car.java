package simulator;

public class Car {
    private static final int MAX_SPEED = 200;

    private final Position position;
    private final Gearbox gearbox;
    private final Engine engine;
    private final String plateNumber;
    private final String modelName;
    private boolean isEngineOn;

    // Constrcutor
    public Car(Position position, Gearbox gearbox, Engine engine, String plateNumber, String modelName) {
        if (position == null) {
            throw new IllegalArgumentException("'position' parameter must not be null");
        }
        if (gearbox == null) {
            throw new IllegalArgumentException("'gearbox' parameter must not be null");
        }
        if (engine == null) {
            throw new IllegalArgumentException("'engine' parameter must not be null");
        }
        if (plateNumber == null || plateNumber.isEmpty()) {
            throw new IllegalArgumentException("'plateNumber' parameter must not be null nor empty");
        }
        if (modelName == null || modelName.isEmpty()) {
            throw new IllegalArgumentException("'modelName' parameter must not be null nor empty");
        }

        this.position = position;
        this.gearbox = gearbox;
        this.engine = engine;
        this.plateNumber = plateNumber;
        this.modelName = modelName;
        this.isEngineOn = false;
    }

    // Basic getters
    public Position getPosition() { return this.position; }
    public Gearbox getGearbox() { return this.gearbox; }
    public Engine getEngine() { return this.engine; }
    public String getPlateNumber() { return this.plateNumber; }
    public String getModelName() { return this.modelName; }
    public boolean getIsEngineOn() { return this.isEngineOn; }

    // Advanced getters - not yet implemented
    public double getWeight() {
        return this.gearbox.getWeight() + this.gearbox.getClutch().getWeight() + this.engine.getWeight();
    }
    public double getCurrentSpeed() { return 0; }

    // Car control methods - not yet implemented
    public void switchOn() { throw new UnsupportedOperationException("Not implemented yet"); }
    public void switchOff() { throw new UnsupportedOperationException("Not implemented yet"); }
    public void driveTo(Position destination) { throw new UnsupportedOperationException("Not implemented yet"); }
}
