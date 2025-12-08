package simulator;

import java.util.ArrayList;
import java.util.Date;

public class Tournament {
    private final String title;
    private final Date date;
    private final ArrayList<Car> competitors;

    // Constructors
    public Tournament() {
        this("Turniej", new Date());
    }
    public Tournament(String title, Date date) {
        this.title = title;
        this.date = date;
        competitors = new ArrayList<>();
    }

    // Basic getters
    public String getTitle() { return this.title; }
    public Date getDate() { return this.date; }
    public ArrayList<Car> getCompetitors() { return this.competitors; }

    // Competitors ArrayList methods
    public Car findCompetitor(String plateNumber) {
        for (Car competitor: this.competitors) {
            if (competitor.getPlateNumber().equals(plateNumber)) { return competitor; }
        }
        return null;
    }
    public void addCompetitor(Car car) {
        // Check for unique plate number
        if (this.findCompetitor(car.getPlateNumber()) == null) {
            this.competitors.add(car);
        }
    }
    public void removeCompetitor(String plateNumber) {
        Car competitor = this.findCompetitor(plateNumber);
        if (competitor != null) {
            this.competitors.remove(competitor);
        }
    }

    // Torunament control methods - not yet implemented
    public void begin() {}
}
