package com.github.skumoreq.simulator.gui;

import com.github.skumoreq.simulator.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class FormController {

    // region ⮞ FXML Controller Data Exchange

    private @NotNull List<String> usedPlateNumbers = new ArrayList<>();
    private @Nullable Car targetCar = null;

    private boolean confirmed = false;

    public void importUsedPlateNumbers(@NotNull List<String> usedPlateNumbers) {
        this.usedPlateNumbers = usedPlateNumbers;
    }

    public @Nullable Car exportResult() {
        return confirmed ? targetCar : null;
    }
    // endregion

    // region ⮞ FXML Injected Fields

    private @FXML VBox root;

    private @FXML TitledPane targetCarSection;
    private @FXML TitledPane clutchSection;
    private @FXML TitledPane transmissionSection;
    private @FXML TitledPane engineSection;

    private @FXML ComboBox<String> clutchSelection;
    private @FXML ComboBox<String> transmissionSelection;
    private @FXML ComboBox<String> engineSelection;

    private @FXML Button addTargetCar;
    private @FXML Button confirmForm;

    private @FXML TextField carPlateNumber;
    private @FXML TextField carModelName;
    private @FXML TextField carTotalWeight;
    private @FXML TextField carTotalPrice;
    private @FXML TextField carTopSpeed;
    private @FXML TextField clutchWeight;
    private @FXML TextField clutchPrice;
    private @FXML TextField transmissionWeight;
    private @FXML TextField transmissionPrice;
    private @FXML TextField transmissionRatios;
    private @FXML TextField engineWeight;
    private @FXML TextField enginePrice;
    private @FXML TextField engineMaxRpm;
    // endregion

    // region ⮞ Constants

    private static final String INVALID_PLATE_HEADER = "Niepoprawny numer rejestracyjny";
    private static final Pattern PLATE_NUMBER_PATTERN = Pattern.compile("^[A-Z]{1,3} [0-9ACE-HJ-NP-Y]{5}$");
    // endregion

    // region ⮞ Helper Methods

    private Stage getFormStage() {
        return (Stage) root.getScene().getWindow();
    }

    private boolean isFormComplete() {
        return !JavaFXUtils.isBlank(carPlateNumber)
                && !JavaFXUtils.isBlank(carModelName)
                && !JavaFXUtils.isEmpty(transmissionSelection)
                && !JavaFXUtils.isEmpty(engineSelection);
    }

    private boolean isFormBlank() {
        return JavaFXUtils.isBlank(carPlateNumber)
                && JavaFXUtils.isBlank(carModelName)
                && JavaFXUtils.isEmpty(transmissionSelection)
                && JavaFXUtils.isEmpty(engineSelection);
    }

    private void populateTargetCarSection() {
        if (targetCar == null) return;

        JavaFXUtils.hide(addTargetCar);

        carTotalWeight.setText(targetCar.getTotalWeightDisplay());
        carTotalPrice.setText(targetCar.getTotalPriceDisplay());
        carTopSpeed.setText(targetCar.getTopSpeedDisplay());

        JavaFXUtils.forceExpand(targetCarSection);
        JavaFXUtils.show(confirmForm);
    }

    private void clearTargetCarSection() {
        // This is called anytime something changes in the form.
        addTargetCar.setDisable(!isFormComplete());

        if (!targetCarSection.isExpanded()) return; // avoid reapplying reset logic

        JavaFXUtils.hide(confirmForm);

        JavaFXUtils.forceCollapse(targetCarSection);
        JavaFXUtils.clear(carTotalWeight, carTotalPrice, carTopSpeed);
        JavaFXUtils.show(addTargetCar);
    }

    private void populateClutchSection() {
        Clutch clutch = CarManager.CLUTCHES.get(JavaFXUtils.getSelectedIndex(clutchSelection));

        clutchWeight.setText(clutch.getWeightDisplay());
        clutchPrice.setText(clutch.getPriceDisplay());

        JavaFXUtils.forceExpand(clutchSection);
    }

    private void clearClutchSection() {
        JavaFXUtils.forceCollapse(clutchSection);
        JavaFXUtils.clear(clutchWeight, clutchPrice);
    }

    private void populateTransmissionSection() {
        Transmission transmission = CarManager.TRANSMISSIONS.get(JavaFXUtils.getSelectedIndex(transmissionSelection));

        transmissionWeight.setText(transmission.getWeightDisplay());
        transmissionPrice.setText(transmission.getPriceDisplay());
        transmissionRatios.setText(transmission.getGearRatiosDisplay());

        JavaFXUtils.forceExpand(transmissionSection);

        JavaFXUtils.select(clutchSelection, transmission.clutch().getName());
    }

    private void clearTransmissionSection() {
        JavaFXUtils.clearSelection(clutchSelection);
        JavaFXUtils.forceCollapse(transmissionSection);
        JavaFXUtils.clear(transmissionWeight, transmissionPrice, transmissionRatios);
    }

    private void populateEngineSection() {
        Engine engine = CarManager.ENGINES.get(JavaFXUtils.getSelectedIndex(engineSelection));

        engineWeight.setText(engine.getWeightDisplay());
        enginePrice.setText(engine.getPriceDisplay());
        engineMaxRpm.setText(engine.getMaxRpmDisplay());

        JavaFXUtils.forceExpand(engineSection);
    }

    private void clearEngineSection() {
        JavaFXUtils.forceCollapse(engineSection);
        JavaFXUtils.clear(engineWeight, enginePrice, engineMaxRpm);
    }

    private @NotNull String validatePlateNumber(@NotNull String plateNumber) {
        if (!PLATE_NUMBER_PATTERN.matcher(plateNumber).matches()) {
            return """
                    Wprowadzony numer rejestracyjny ma nieprawidłowy format.
                    Przykład poprawnego numeru: KR 12345
                    
                    Wymagania:
                    • Prefiks: 1-3 wielkie litery (A-Z)
                    • Separator: pojedyncza spacja
                    • Sufiks: dokładnie 5 znaków (cyfry i dozwolone litery)
                    
                    Uwaga: System nie akceptuje liter B, D, I, O, Z w sufiksie.
                    """;
        }
        if (usedPlateNumbers.contains(plateNumber)) {
            return """
                    Numer rejestracyjny "%s" jest już w użyciu.
                    
                    Ten numer jest aktualnie przypisany do innego pojazdu w systemie.
                    Proszę wpisać unikalny numer rejestracyjny.
                    """.formatted(plateNumber);
        }
        return "";
    }
    // endregion

    // region ⮞ FXML Event Handlers

    @FXML
    private void initialize() {
        JavaFXUtils.forceCollapse(targetCarSection, clutchSection, transmissionSection, engineSection);
        JavaFXUtils.hide(confirmForm);

        addTargetCar.setDisable(true);
        carPlateNumber.textProperty().addListener((_, _, newVal) -> {
            String upper = newVal.toUpperCase();

            if (!newVal.equals(upper)) {
                carPlateNumber.setText(upper);
                return;
            }

            clearTargetCarSection();
        });
        carModelName.textProperty().addListener(_ -> clearTargetCarSection());

        for (Clutch clutch : CarManager.CLUTCHES) {
            clutchSelection.getItems().add(clutch.getName());
        }
        for (Transmission transmission : CarManager.TRANSMISSIONS) {
            transmissionSelection.getItems().add(transmission.getName());
        }
        for (Engine engine : CarManager.ENGINES) {
            engineSelection.getItems().add(engine.getName());
        }
    }

    @FXML
    private void clutchSelectionOnAction() {
        clearTargetCarSection();

        if (JavaFXUtils.isEmpty(clutchSelection)) clearClutchSection();
        else populateClutchSection();
    }

    @FXML
    private void transmissionSelectionOnAction() {
        clearTargetCarSection();

        if (JavaFXUtils.isEmpty(transmissionSelection)) clearTransmissionSection();
        else populateTransmissionSection();
    }

    @FXML
    private void engineSelectionOnAction() {
        clearTargetCarSection();

        if (JavaFXUtils.isEmpty(engineSelection)) clearEngineSection();
        else populateEngineSection();
    }

    @FXML
    private void addTargetCarOnAction() {
        String plateNumber = JavaFXUtils.getTrimmedText(carPlateNumber);

        targetCar = new Car(
                plateNumber, JavaFXUtils.getTrimmedText(carModelName),
                CarManager.TRANSMISSIONS.get(JavaFXUtils.getSelectedIndex(transmissionSelection)),
                CarManager.ENGINES.get(JavaFXUtils.getSelectedIndex(engineSelection))
        );

        String validationResult = validatePlateNumber(plateNumber);

        if (validationResult.isBlank()) {
            populateTargetCarSection();
        } else {
            JavaFXUtils.showAlertAndWait(root, new JavaFXUtils.AlertInfo(
                    Alert.AlertType.WARNING,
                    INVALID_PLATE_HEADER,
                    validationResult)
            );
        }
    }

    @FXML
    private void confirmFormOnAction() {
        confirmed = true;
        getFormStage().close();
    }

    @FXML
    private void cancelFormOnAction() {
        if (isFormBlank()) {
            getFormStage().close();
        } else {
            JavaFXUtils.clear(carPlateNumber, carModelName);
            JavaFXUtils.clearSelection(transmissionSelection);
            JavaFXUtils.clearSelection(engineSelection);
        }
    }
    // endregion
}
