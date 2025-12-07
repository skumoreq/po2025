package simulator;

public class Car {
    private boolean isEngineOn;
    private String licensePlateNumber;
    private String modelName;
    private int maxSpeed;
    private Position currentPosition;
    private Gearbox gearbox;
    private Engine engine;

    public Car(String licensePlateNumber, String modelName, int maxSpeed,
               Position currentPosition, Gearbox gearbox, Engine engine) {
        this.licensePlateNumber = licensePlateNumber;
        this.modelName = modelName;
        this.maxSpeed = maxSpeed;
        this.currentPosition = currentPosition;
        this.gearbox = gearbox;
        this.engine = engine;
        isEngineOn = false;
    }

    public void switchOn() {}

    public void switchOff() {}

    public void driveTo(Position destination) {}

    public String getLicensePlateNumber() {
        return licensePlateNumber;
    }
    public double getWeight() {
        return 0;
    }
    public double getCurrentSpeed() {
        return 0;
    }
    public Position getCurrentPosition() {
        return currentPosition;
    }
}
