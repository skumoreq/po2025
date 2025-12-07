package simulator;

public class Clutch extends Component{
    private boolean isEngaged;

    public Clutch(String name, double weight, double price) {
        super(name, weight, price);
        isEngaged = false;
    }

    public void engage() {}
    public void disengage() {}
}
