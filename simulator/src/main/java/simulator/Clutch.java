package simulator;

public class Clutch extends Component{
    private boolean isEngaged;

    public Clutch(String name, double weight, double price) {
        super(name, weight, price);
        isEngaged = false;
    }

    // Basic getters
    public boolean getIsEngaged() { return this.isEngaged; }

    // Clutch control methods - not yet implemented
    public void engage() { throw new UnsupportedOperationException("Not implemented yet"); }
    public void disengage() { throw new UnsupportedOperationException("Not implemented yet"); }
}
