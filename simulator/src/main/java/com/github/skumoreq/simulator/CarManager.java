package com.github.skumoreq.simulator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
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
 * managing their lifecycle (starting threads) and cleanup (interruption/observer
 * removal) and tracks the currently selected car for synchronized UI updates.</li>
 * </ul>
 */
public class CarManager {

    // region ⮞ Static Data Initialization

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String COMPONENTS_DATA_FILENAME = "components.json";
    private static final JsonNode COMPONENTS_DATA_ROOT;

    static {
        String path = "data/" + COMPONENTS_DATA_FILENAME;

        try (InputStream inputStream = CarManager.class.getResourceAsStream(path)) {
            if (inputStream == null)
                throw new RuntimeException("Resource file not found: " + path);

            COMPONENTS_DATA_ROOT = MAPPER.readTree(inputStream);
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize CarManager data.", e);
        }
    }

    private static <T extends CarComponent> List<T> loadComponents(
            String componentName,
            Function<JsonNode, T> mapperFunction
    ) {
        try {
            List<T> loadedComponents = new ArrayList<>();

            JsonNode componentsData = COMPONENTS_DATA_ROOT.get(componentName);

            if (componentsData == null || !componentsData.isArray())
                throw new RuntimeException("Required JSON field is missing or is not an array.");

            for (JsonNode componentData : componentsData)
                loadedComponents.add(mapperFunction.apply(componentData));

            if (loadedComponents.isEmpty())
                throw new RuntimeException("The list contains no entries.");

            return Collections.unmodifiableList(loadedComponents);
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load " + componentName + " component data.", exception);
        }
    }

    public static final @NotNull List<Clutch> CLUTCHES = loadComponents(
            "clutch", node -> new Clutch(
                    node.get("name").asText(),
                    node.get("weight").asDouble(),
                    node.get("price").asDouble()
            )
    );

    public static final @NotNull List<Engine> ENGINES = loadComponents(
            "engine", node -> new Engine(
                    node.get("name").asText(),
                    node.get("weight").asDouble(),
                    node.get("price").asDouble(),
                    node.get("maxRpm").asDouble()
            )
    );

    public static final @NotNull List<Transmission> TRANSMISSIONS = loadComponents(
            "transmission", node -> new Transmission(
                    node.get("name").asText(),
                    node.get("weight").asDouble(),
                    node.get("price").asDouble(),
                    CLUTCHES.get(node.get("clutch").asInt()),
                    MAPPER.convertValue(node.get("ratios"), double[].class)
            )
    );
    // endregion

    // region ⮞ Instance Fields

    private final @NotNull ObservableList<Car> cars = FXCollections.observableArrayList();
    private final @NotNull ObservableList<String> usedPlateNumbers = FXCollections.observableArrayList();

    private volatile @Nullable Car selected = null;
    // endregion

    // region ⮞ Initialization

    public CarManager() {
        cars.addListener((ListChangeListener<Car>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (Car addedCar : change.getAddedSubList()) {
                        usedPlateNumbers.add(addedCar.getPlateNumber());
                    }
                }
                if (change.wasRemoved()) {
                    for (Car removedCar : change.getRemoved()) {
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

    private @Nullable Car findByPlateNumber(@NotNull String plateNumber) {
        for (Car car : cars) {
            if (car.getPlateNumber().equals(plateNumber)) return car;
        }

        return null;
    }

    public void selectByPlateNumber(@NotNull String plateNumber) {
        selected = findByPlateNumber(plateNumber);
    }

    public void addEntry(@NotNull Car car) {
        if (!usedPlateNumbers.contains(car.getPlateNumber())) {
            cars.add(car);

            // Start the simulation thread after successful registration.
            car.start();
        }
    }

    public void removeSelected() {
        cars.remove(selected);
    }
    // endregion
}
