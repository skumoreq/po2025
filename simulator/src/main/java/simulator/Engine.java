package simulator;

public class Engine extends Component{
    // «««Core Identity»»»
    private final double maxRPM;

    // «««Runtime State»»»
    private double currentRPM;

    // «««Constructors»»»
    public Engine(String name, double weight, double price, double maxRPM) {
        super(name, weight, price);
        this.maxRPM = maxRPM;
        this.currentRPM = 0;
    }



    // «««Basic Getters»»»
    public double getMaxRPM() {
        return this.maxRPM;
    }
    public double getCurrentRPM() {
        return this.currentRPM;
    }

    // «««String Representations»»»
    public String maxRPMToString() {
        return String.format("%.0f RPM", this.maxRPM);
    }
    public String currentRPMToString() {
        return String.format("%.0f RPM%s", this.currentRPM, (this.currentRPM > this.maxRPM * 0.9) ? " ⚠" : "");
    }



    // «««Action Methods»»»
    // !!! Not implemented yet !!!
    public void increaseRPM() {

    }
    public void decreaseRPM() {

    }
}
