package com.github.skumoreq.simulator.gui;

import static com.github.skumoreq.simulator.gui.JavaFxUtils.*;
import static com.github.skumoreq.simulator.CarManager.CLUTCHES;
import static com.github.skumoreq.simulator.CarManager.GEARBOXES;
import static com.github.skumoreq.simulator.CarManager.ENGINES;

import com.github.skumoreq.simulator.Car;
import com.github.skumoreq.simulator.Clutch;
import com.github.skumoreq.simulator.Engine;
import com.github.skumoreq.simulator.Gearbox;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class FormController {
    // «««Used Plate Numbers List»»» (received from PrimaryController)
    private List<String> usedPlateNumbers;
    public void setUsedPlateNumbers(List<String> usedPlateNumbers) {
        this.usedPlateNumbers = new ArrayList<>(usedPlateNumbers);
    }

    // «««Created Car»»» (sent to PrimaryController)
    private Car carBeingCreated;
    public Car getCreatedCar() {
        return carBeingCreated;
    }


    // «««JavaFX Scene»»»

    // «Layout»
    @FXML private VBox rootVBox;

    // «TitledPanes»
    @FXML private TitledPane carTitledPane;
    @FXML private TitledPane clutchTitledPane;
    @FXML private TitledPane gearboxTitledPane;
    @FXML private TitledPane engineTitledPane;

    // «ComboBoxes»
    @FXML private ComboBox<String> clutchComboBox;
    @FXML private ComboBox<String> gearboxComboBox;
    @FXML private ComboBox<String> engineComboBox;

    // «Buttons»
    @FXML private Button addCarButton;
    @FXML private Button confirmButton;

    // «TextFields»
    @FXML private TextField carPlateNumberTextField;
    @FXML private TextField carModelNameTextField;
    @FXML private TextField carWeightTextField;
    @FXML private TextField carPriceTextField;
    @FXML private TextField carTopSpeedTextField;
    @FXML private TextField clutchWeightTextField;
    @FXML private TextField clutchPriceTextField;
    @FXML private TextField gearboxWeightTextField;
    @FXML private TextField gearboxPriceTextField;
    @FXML private TextField gearboxGearRatiosTextField;
    @FXML private TextField engineWeightTextField;
    @FXML private TextField enginePriceTextField;
    @FXML private TextField engineMaxRpmTextField;


    // «««Helper Methods»»»
    private Stage getFormStage() {
        return (Stage) rootVBox.getScene().getWindow();
    }

    private String getPlateNumberErrorMessage() {
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

    private boolean isFormEmpty() {
        return getTrimmedText(carPlateNumberTextField).isEmpty()
                && getTrimmedText(carModelNameTextField).isEmpty()
                && isEmpty(gearboxComboBox)
                && isEmpty(engineComboBox);
    }
    private boolean isFormFilled() {
        return !getTrimmedText(carPlateNumberTextField).isEmpty()
                && !getTrimmedText(carModelNameTextField).isEmpty()
                && !isEmpty(gearboxComboBox)
                && !isEmpty(engineComboBox);
    }

    private void populateCarTitledPane() {
        hide(addCarButton);
        show(confirmButton);

        carBeingCreated = new Car(
                GEARBOXES[getSelectedIndex(gearboxComboBox)],
                ENGINES[getSelectedIndex(engineComboBox)],
                getTrimmedText(carPlateNumberTextField),
                getTrimmedText(carModelNameTextField)
        );

        carWeightTextField.setText(carBeingCreated.getTotalWeightDisplay());
        carPriceTextField.setText(carBeingCreated.getTotalPriceDisplay());
        carTopSpeedTextField.setText(carBeingCreated.getTopSpeedDisplay());
        expand(carTitledPane);
    }
    private void clearCarTitledPane() {
        addCarButton.setDisable(!isFormFilled());

        if (!carTitledPane.isExpanded()) return;

        hide(confirmButton);
        show(addCarButton);

        carBeingCreated = null;

        collapse(carTitledPane);
        clear(carWeightTextField, carPriceTextField, carTopSpeedTextField);
    }

    private void populateClutchTitledPane() {
        Clutch clutch = CLUTCHES[getSelectedIndex(clutchComboBox)];

        clutchWeightTextField.setText(clutch.getWeightDisplay());
        clutchPriceTextField.setText(clutch.getPriceDisplay());
        expand(clutchTitledPane);
    }
    private void clearClutchTitledPane() {
        collapse(clutchTitledPane);
        clear(clutchWeightTextField, clutchPriceTextField);
    }

    private void populateGearboxTitledPane() {
        select(clutchComboBox, getSelectedIndex(gearboxComboBox));

        Gearbox gearbox = GEARBOXES[getSelectedIndex(gearboxComboBox)];

        gearboxWeightTextField.setText(gearbox.getWeightDisplay());
        gearboxPriceTextField.setText(gearbox.getPriceDisplay());
        gearboxGearRatiosTextField.setText(gearbox.getGearRatiosDisplay());
        expand(gearboxTitledPane);
    }
    private void clearGearboxTitledPane() {
        clearSelection(clutchComboBox);

        collapse(gearboxTitledPane);
        clear(gearboxWeightTextField, gearboxPriceTextField, gearboxGearRatiosTextField);
    }

    private void populateEngineTitledPane() {
        Engine engine = ENGINES[getSelectedIndex(engineComboBox)];

        engineWeightTextField.setText(engine.getWeightDisplay());
        enginePriceTextField.setText(engine.getPriceDisplay());
        engineMaxRpmTextField.setText(engine.getMaxRpmDisplay());
        expand(engineTitledPane);
    }
    private void clearEngineTitledPane() {
        collapse(engineTitledPane);
        clear(engineWeightTextField, enginePriceTextField, engineMaxRpmTextField);
    }


    // «««JavaFX Event Handlers»»»
    @FXML private void initialize() {
        collapse(carTitledPane, clutchTitledPane, gearboxTitledPane, engineTitledPane);

        for (Clutch clutch : CLUTCHES) clutchComboBox.getItems().add(clutch.getName());
        for (Gearbox gearbox : GEARBOXES) gearboxComboBox.getItems().add(gearbox.getName());
        for (Engine engine : ENGINES) engineComboBox.getItems().add(engine.getName());

        addCarButton.setDisable(true);
        hide(confirmButton);

        carPlateNumberTextField.textProperty().addListener(ignored -> clearCarTitledPane());
        carModelNameTextField.textProperty().addListener(ignored -> clearCarTitledPane());
    }

    // «ComboBox Event Handlers»
    @FXML private void handleClutchComboBox() {
        clearCarTitledPane();

        if (clutchComboBox.getValue() == null) clearClutchTitledPane();
        else populateClutchTitledPane();
    }
    @FXML private void handleGearboxComboBox() {
        clearCarTitledPane();

        if (gearboxComboBox.getValue() == null) clearGearboxTitledPane();
        else populateGearboxTitledPane();
    }
    @FXML private void handleEngineComboBox() {
        clearCarTitledPane();

        if (engineComboBox.getValue() == null) clearEngineTitledPane();
        else populateEngineTitledPane();
    }

    // «Button Event Handlers»
    @FXML private void handleAddCarButton() {
        populateCarTitledPane();
    }
    @FXML private void handleConfirmButton() {
        String plateNumberErrorMessage = getPlateNumberErrorMessage();
        if (plateNumberErrorMessage.isEmpty()) {
            getFormStage().close();
            return;
        }

        showWarningAlert(rootVBox, "Niepoprawny numer rejestracyjny", plateNumberErrorMessage);

        clearCarTitledPane(); // waits for warningAlert to close
    }
    @FXML private void handleCancelButton() {
        if (isFormEmpty()) {
            getFormStage().close();
            return;
        }

        clear(carPlateNumberTextField, carModelNameTextField);

        clearSelection(gearboxComboBox);
        clearSelection(engineComboBox);
    }
}
