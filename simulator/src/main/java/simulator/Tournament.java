package simulator;

import java.util.ArrayList;
import java.util.Date;

public class Tournament {
    private final String title;
    private final Date date;
    private final ArrayList<Car> competitors;

    // Constructors
    public Tournament() { this("Turniej", new Date()); }
    public Tournament(String title, Date date) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("'title' parameter must not be null nor empty");
        }
        if (date == null) {
            throw new IllegalArgumentException("'date' parameter must not be null");
        }

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
        if (plateNumber == null || plateNumber.isEmpty()) {
            throw new IllegalArgumentException("'plateNumber' parameter must not be null nor empty");
        }

        for (Car competitor: this.competitors) {
            if (competitor.getPlateNumber().equals(plateNumber)) { return competitor; }
        }
        return null;
    }
    public void addCompetitor(Car competitor) {
        // Check for unique plate number
        if (this.findCompetitor(competitor.getPlateNumber()) == null) {
            this.competitors.add(competitor);
        } else {
            throw new IllegalArgumentException("Duplicate plate number: " + competitor.getPlateNumber());
        }
    }
    public void removeCompetitor(Car competitor) {
        if (competitor == null) {
            throw new IllegalArgumentException("'competitor' parameter must not be null");
        }
        this.competitors.remove(competitor);
    }

    // Tournament control methods - not yet implemented
    public void begin() { throw new UnsupportedOperationException("Not implemented yet"); }
}
