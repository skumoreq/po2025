package simulator;

import java.util.Date;

public class Tournament {
    private String name;
    private Date date;
    private Car[] competitors;

    public Tournament(String name, Date date, Car[] competitors) {
        this.name = name;
        this.date = date;
        this.competitors = competitors;
    }

    public void begin() {}

    public static void main() {
        Car car1 = new Car("KTA30607", "Honda Civic VI Liftback", 220,
                new Position(0, 0),
                new Gearbox("Skrzynia biegów", 50.0, 1000.0, 5,
                    new Clutch("Sprzęgło", 15.0, 500.0)
                ),
                new Engine("Silnik 1.4", 200.0, 10000.0, 9000));
    }
}
