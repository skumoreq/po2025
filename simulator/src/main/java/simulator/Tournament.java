package simulator;

import java.util.ArrayList;
import java.util.Date;

public class Tournament {
    private String name;
    private Date date;
    private ArrayList<Car> competitors;

    public Tournament(String name, Date date) {
        this.name = name;
        this.date = date;
        competitors = new ArrayList<Car>();
    }

    public ArrayList<Car> getCompetitors() {
        return competitors;
    }
    public void addCarToTournament(Car car) {
        competitors.add(car);
    }
    public void removeCarFromTournamentByLicensePlateNumber(String licensePlateNumber) {
        competitors.remove(getCarByLicensePlateNumber(licensePlateNumber));
    }
    public Car getCarByLicensePlateNumber(String licensePlateNumber) {
        for (Car car : competitors) {
            if (car.getLicensePlateNumber().equals(licensePlateNumber)) {
                return car;
            }
        }
        return null;
    }

    public void begin() {}
}
