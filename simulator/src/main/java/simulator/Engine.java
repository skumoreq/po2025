package simulator;

import static java.lang.Math.min;

public class Engine extends Component{
    // «««Class Constants»»»
    public static final double RPM_IDLE = 800.0;
    public static final double RPM_STEP = 100.0;



    // «««Core Identity»»»
    private final double maxRpm;

    // «««Dynamic State»»»
    private double rpm = 0.0;

    // «««Initialization»»»
    public Engine(String name, double weight, double price, double maxRpm) {
        super(name, weight, price);
        this.maxRpm = maxRpm;
    }



    // «««Accessors»»»
    public double getMaxRpm() {
        return this.maxRpm;
    }
    public double getRpm() {
        return this.rpm;
    }

    // «««Display Methods»»»
    public String getMaxRpmText() {
        return String.format("%.0f RPM", this.maxRpm);
    }
    public String getRpmText() {
        return String.format("%.0f RPM%s", rpm, (rpm > maxRpm * 0.9) ? " ⚠" : "");
    }



    // «««Control Methods»»»
    public void startIdle() {
        this.rpm = Engine.RPM_IDLE;
    }
    public void stop() {
        this.rpm = 0.0;
    }
    public void adjustRpmForGearChange(int previousGear, int gear) {
        this.rpm += (previousGear - gear) * Engine.RPM_IDLE;
    }
    public void increaseRpm() {
        this.rpm += RPM_STEP;
        this.rpm = min(this.rpm, this.maxRpm);
    }
    public void decreaseRpm() {
        this.rpm -= RPM_STEP;
    }
}
