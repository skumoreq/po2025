package com.github.skumoreq.simulator;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Central manager for the car simulation system.
 * <p>
 * This class serves two primary purposes:
 * <ul>
 * <li><b>Static Data Provider:</b> Loads and exposes pre-defined car component
 * templates (engines, transmissions, clutches) from JSON resources.</li>
 * <li><b>Active Registry:</b> Maintains the collection of active car instances,
 * managing their lifecycle (starting threads) and cleanup (interruption/
 * observer removal) and tracks the currently selected car for synchronized UI
 * updates.</li>
 * </ul>
 */
public class CarManager {

    // region ⮞ Static Data Initialization

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String COMPONENTS_DATA_FILENAME = "components.json";
    private static final JsonNode COMPONENTS_DATA_ROOT;

    static {
        var path = "data/" + COMPONENTS_DATA_FILENAME;

        try (var inputStream = CarManager.class.getResourceAsStream(path)) {
            if (inputStream == null)
                throw new RuntimeException("Resource file not found: " + path);

            COMPONENTS_DATA_ROOT = MAPPER.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize CarManager data.", e);
        }
    }

    @Unmodifiable
    private static <T extends CarComponent> @NotNull List<T> loadComponents(
            String componentType,
            Function<JsonNode, T> mapperFunction
    ) {
        try {
            var loadedComponents = new ArrayList<T>();
            var componentsData = COMPONENTS_DATA_ROOT.get(componentType);

            if (componentsData == null || !componentsData.isArray())
                throw new RuntimeException("Required JSON field is missing or is not an array.");

            for (var componentData : componentsData)
                loadedComponents.add(mapperFunction.apply(componentData));

            if (loadedComponents.isEmpty())
                throw new RuntimeException("The list contains no entries.");

            return List.copyOf(loadedComponents);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + componentType + " component data.", e);
        }
    }

    public static final @NotNull List<Clutch> CLUTCHES = loadComponents(
            "clutch", node -> new Clutch(
                    node.get("name").asString(),
                    node.get("weight").asDouble(),
                    node.get("price").asDouble()
            )
    );

    private static @NotNull Clutch findClutchByName(@NotNull String name) {
        for (var clutch : CLUTCHES)
            if (clutch.getName().equals(name)) return clutch;

        throw new RuntimeException("Clutch '" + name + "' not found in CLUTCHES registry.");
    }

    public static final @NotNull List<Engine> ENGINES = loadComponents(
            "engine", node -> new Engine(
                    node.get("name").asString(),
                    node.get("weight").asDouble(),
                    node.get("price").asDouble(),
                    node.get("maxRpm").asDouble()
            )
    );

    public static final @NotNull List<Transmission> TRANSMISSIONS = loadComponents(
            "transmission", node -> new Transmission(
                    node.get("name").asString(),
                    node.get("weight").asDouble(),
                    node.get("price").asDouble(),
                    findClutchByName(node.get("clutch").asString()),
                    MAPPER.convertValue(node.get("ratios"), double[].class)
            )
    );
    // endregion

    // region ⮞ Instance Fields

    private final @NotNull ObservableList<Car> cars = FXCollections.observableArrayList();
    private final @NotNull ObservableList<String> usedPlateNumbers = FXCollections.observableArrayList();

    /**
     * Currently selected car instance. Using {@code volatile} ensures that any
     * thread always sees the most recent selection.
     */
    private volatile @Nullable Car selected = null;
    // endregion

    // region ⮞ Initialization

    public CarManager() {
        cars.addListener((ListChangeListener<Car>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (var addedCar : change.getAddedSubList())
                        usedPlateNumbers.add(addedCar.getPlateNumber());
                } else if (change.wasRemoved()) {
                    for (var removedCar : change.getRemoved()) {
                        usedPlateNumbers.remove(removedCar.getPlateNumber());

                        // CRITICAL: Stop the simulation thread and clear references
                        // to prevent memory leaks and background processing of removed cars.
                        removedCar.interrupt();
                        removedCar.removeAllObservers();

                        if (selected == removedCar) selected = null;
                    }
                }
            }
        });
    }
    // endregion

    // region ⮞ Getters

    public @Nullable Car selected() {
        return selected;
    }

    public @NotNull ObservableList<String> usedPlateNumbers() {
        return usedPlateNumbers;
    }

    public @NotNull List<String> getUsedPlateNumbers() {
        return List.copyOf(usedPlateNumbers);
    }
    // endregion

    // region ⮞ Query Methods

    public @Nullable Car findByPlateNumber(@NotNull String plateNumber) {
        for (var car : cars)
            if (car.getPlateNumber().equals(plateNumber)) return car;
        return null;
    }

    public void select(@NotNull Car car) {
        selected = car;
    }

    public void addEntry(@NotNull Car car) {
        if (!usedPlateNumbers.contains(car.getPlateNumber())) {
            cars.add(car);

            // Start the simulation thread after successful registration.
            car.start();
        }
    }

    public void removeEntry(@Nullable Car car) {
        cars.remove(car);
    }
    // endregion
}
