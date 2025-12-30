package com.github.skumoreq.simulator.gui;

import com.github.skumoreq.simulator.Car;
import com.github.skumoreq.simulator.Clutch;
import com.github.skumoreq.simulator.Engine;
import com.github.skumoreq.simulator.Gearbox;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.github.skumoreq.simulator.CarManager.*;
import static com.github.skumoreq.simulator.gui.JavaFxUtils.*;

public class FormController {

    // region > FXML Controllers Shared Data

    private List<String> usedPlateNumbers; // received from PrimaryController

    public void setUsedPlateNumbers(List<String> usedPlateNumbers) {
        this.usedPlateNumbers = new ArrayList<>(usedPlateNumbers);
    }

    private Car carBeingCreated; // sent to PrimaryController

    public Car getCreatedCar() {
        return carBeingCreated;
    }
    // endregion

    // region > FXML Injected Fields

    @FXML
    private VBox root;

    @FXML
    private TitledPane carSection;
    @FXML
    private TitledPane clutchSection;
    @FXML
    private TitledPane gearboxSection;
    @FXML
    private TitledPane engineSection;

    @FXML
    private ComboBox<String> clutchSelection;
    @FXML
    private ComboBox<String> gearboxSelection;
    @FXML
    private ComboBox<String> engineSelection;

    @FXML
    private Button addCar;
    @FXML
    private Button confirm;

    @FXML
    private TextField carPlateNumber;
    @FXML
    private TextField carModelName;
    @FXML
    private TextField carTotalWeight;
    @FXML
    private TextField carTotalPrice;
    @FXML
    private TextField carTopSpeed;
    @FXML
    private TextField clutchWeight;
    @FXML
    private TextField clutchPrice;
    @FXML
    private TextField gearboxWeight;
    @FXML
    private TextField gearboxPrice;
    @FXML
    private TextField gearboxGearRatios;
    @FXML
    private TextField engineWeight;
    @FXML
    private TextField enginePrice;
    @FXML
    private TextField engineMaxRpm;
    // endregion

    // region > Helper Methods

    private Stage getFormStage() {
        return (Stage) root.getScene().getWindow();
    }

    private boolean isFormComplete() {
        return !isEmpty(carPlateNumber)
                && !isEmpty(carModelName)
                && !isEmpty(gearboxSelection)
                && !isEmpty(engineSelection);
    }
    private boolean isFormBlank() {
        return isEmpty(carPlateNumber)
                && isEmpty(carModelName)
                && isEmpty(gearboxSelection)
                && isEmpty(engineSelection);
    }

    private void populateCarSection() {
        hide(addCar);
        show(confirm);

        carBeingCreated = new Car(
                GEARBOXES[getSelectedIndex(gearboxSelection)],
                ENGINES[getSelectedIndex(engineSelection)],
                getTrimmedText(carPlateNumber),
                getTrimmedText(carModelName)
        );

        carTotalWeight.setText(carBeingCreated.getTotalWeightDisplay());
        carTotalPrice.setText(carBeingCreated.getTotalPriceDisplay());
        carTopSpeed.setText(carBeingCreated.getTopSpeedDisplay());
        expand(carSection);
    }
    private void clearCarSection() {
        // This method is called anytime something changes in the form.
        addCar.setDisable(!isFormComplete());

        if (!carSection.isExpanded()) return; // avoid reapplying reset logic

        hide(confirm);
        show(addCar);

        carBeingCreated = null;

        collapse(carSection);
        clear(carTotalWeight, carTotalPrice, carTopSpeed);
    }

    private void populateClutchSection() {
        Clutch clutch = CLUTCHES[getSelectedIndex(clutchSelection)];

        clutchWeight.setText(clutch.getWeightDisplay());
        clutchPrice.setText(clutch.getPriceDisplay());
        expand(clutchSection);
    }
    private void clearClutchSection() {
        collapse(clutchSection);
        clear(clutchWeight, clutchPrice);
    }

    private void populateGearboxSection() {
        select(clutchSelection, getSelectedIndex(gearboxSelection));

        Gearbox gearbox = GEARBOXES[getSelectedIndex(gearboxSelection)];

        gearboxWeight.setText(gearbox.getWeightDisplay());
        gearboxPrice.setText(gearbox.getPriceDisplay());
        gearboxGearRatios.setText(gearbox.getGearRatiosDisplay());
        expand(gearboxSection);
    }
    private void clearGearboxSection() {
        clearSelection(clutchSelection);

        collapse(gearboxSection);
        clear(gearboxWeight, gearboxPrice, gearboxGearRatios);
    }

    private void populateEngineSection() {
        Engine engine = ENGINES[getSelectedIndex(engineSelection)];

        engineWeight.setText(engine.getWeightDisplay());
        enginePrice.setText(engine.getPriceDisplay());
        engineMaxRpm.setText(engine.getMaxRpmDisplay());
        expand(engineSection);
    }
    private void clearEngineSection() {
        collapse(engineSection);
        clear(engineWeight, enginePrice, engineMaxRpm);
    }

    private @NotNull String getPlateNumberErrorMessage() {
        Pattern pattern = Pattern.compile("^[A-Z]{1,3} [0-9ACE-HJ-NP-Y]{5}$");
        String plateNumber = carBeingCreated.getPlateNumber();

        if (!pattern.matcher(plateNumber).matches()) {
            return """
                    Wprowadzony numer rejestracyjny ma nieprawidłowy format.
                    
                    Wymagany format zgodny ze specyfikacją: [LITERY]{1-3} [ZNAKI]{5}
                    
                    Struktura numeru:
                    1. Prefiks literowy: od 1 do 3 wielkich liter (A-Z)
                    2. Separator: pojedyncza spacja
                    3. Sufiks znakowy: dokładnie 5 znaków alfanumerycznych:
                       - cyfry 0-9
                       - litery alfabetu łacińskiego z wyłączeniem: B, D, I, O, Z
                    
                    Uwaga: Niedozwolone jest stosowanie małych liter, znaków specjalnych oraz pominięcie separatora.
                    """;
        }
        if (usedPlateNumbers.contains(plateNumber)) {
            return """
                    Numer rejestracyjny "%s" już istnieje w systemie.
                    
                    Każdy pojazd w symulatorze musi posiadać unikalny numer rejestracyjny.
                    Wprowadzony numer jest już przypisany do innego pojazdu.
                    
                    Proszę wprowadzić inny, nieużywany numer rejestracyjny.
                    """.formatted(plateNumber);
        }
        return "";
    }
    // endregion

    // region > FXML Event Handlers

    @FXML
    private void initialize() {
        collapse(carSection, clutchSection, gearboxSection, engineSection);

        for (Clutch clutch : CLUTCHES) clutchSelection.getItems().add(clutch.getName());
        for (Gearbox gearbox : GEARBOXES) gearboxSelection.getItems().add(gearbox.getName());
        for (Engine engine : ENGINES) engineSelection.getItems().add(engine.getName());

        addCar.setDisable(true);
        hide(confirm);

        carPlateNumber.textProperty().addListener(_ -> clearCarSection());
        carModelName.textProperty().addListener(_ -> clearCarSection());
    }

    @FXML
    private void clutchSelectionOnAction() {
        clearCarSection();

        if (isEmpty(clutchSelection)) clearClutchSection();
        else populateClutchSection();
    }
    @FXML
    private void gearboxSelectionOnAction() {
        clearCarSection();

        if (isEmpty(gearboxSelection)) clearGearboxSection();
        else populateGearboxSection();
    }
    @FXML
    private void engineSelectionOnAction() {
        clearCarSection();

        if (isEmpty(engineSelection)) clearEngineSection();
        else populateEngineSection();
    }

    @FXML
    private void addCarOnAction() {
        populateCarSection();
    }
    @FXML
    private void confirmOnAction() {
        String plateNumberErrorMessage = getPlateNumberErrorMessage();
        if (plateNumberErrorMessage.isEmpty()) {
            getFormStage().close();
            return;
        }

        showAlertAndWait(
                Alert.AlertType.WARNING, root, "Niepoprawny numer rejestracyjny", plateNumberErrorMessage);

        clearCarSection();
    }
    @FXML
    private void cancelOnAction() {
        if (isFormBlank()) {
            getFormStage().close();
            return;
        }

        clear(carPlateNumber, carModelName);

        clearSelection(gearboxSelection); // will also clear clutch selection
        clearSelection(engineSelection);
    }
    // endregion
}
