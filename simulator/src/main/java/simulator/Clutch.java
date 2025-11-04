package simulator;

public class Clutch extends Component{
    private boolean isEngaded;

    public Clutch(String name, double weight, double price) {
        super(name, weight, price);
        isEngaded = false;
    }

    public void engage() {}
    public void disengage() {}
}
