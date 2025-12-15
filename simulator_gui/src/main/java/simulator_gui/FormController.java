package simulator_gui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import simulator.*;

import java.util.Objects;
import java.util.regex.Pattern;

public class FormController {
    // «««JavaFX Scene Controls»»»

    // TitledPane
    @FXML private TitledPane carSpecsTitledPane;
    @FXML private TitledPane clutchTitledPane;
    @FXML private TitledPane gearboxTitledPane;
    @FXML private TitledPane engineTitledPane;

    // ComboBox
    @FXML private ComboBox<String> clutchComboBox;
    @FXML private ComboBox<String> gearboxComboBox;
    @FXML private ComboBox<String> engineComboBox;

    // Button
    @FXML private Button addCarButton;
    @FXML private Button confirmButton;
    @FXML private Button cancelButton;

    // TextField
    @FXML private TextField carPlateNumberTextField;
    @FXML private TextField carModelNameTextField;
    @FXML private TextField carWeightTextField;
    @FXML private TextField carPriceTextField;
    @FXML private TextField carMaxSpeedTextField;
    @FXML private TextField clutchWeightTextField;
    @FXML private TextField clutchPriceTextField;
    @FXML private TextField gearboxWeightTextField;
    @FXML private TextField gearboxPriceTextField;
    @FXML private TextField gearboxGearRatiosTextField;
    @FXML private TextField engineWeightTextField;
    @FXML private TextField enginePriceTextField;
    @FXML private TextField engineMaxRPMTextField;



    // «««Created Car (null if canceled)»»»
    private Car carBeingCreated = null;
    public Car getCreatedCar() {
        return carBeingCreated;
    }

    // «««Main Simulator Instance (from PrimaryController)»»»
    private Simulator simulator;
    public void setSimulator(Simulator simulator) {
        this.simulator = simulator;
    }



    // «««Helper Methods»»»
    private boolean validatePlateNumberRegex(String plateNumber) {
        String regex = "^[A-Z]{1,3} [0-9ACE-HJ-NP-Y]{5}$";
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(plateNumber).matches();
    }

    private void setTitledPaneExpanded(TitledPane titledPane, boolean expanded) {
        titledPane.setCollapsible(true);
        titledPane.setExpanded(expanded);
        titledPane.setCollapsible(false);
    }
    private void setButtonVisibility(Button button, boolean visible) {
        button.setVisible(visible);
        button.setManaged(visible);
    }

    private boolean isFormEmpty() {
        return carPlateNumberTextField.getText().trim().isEmpty()
                && carModelNameTextField.getText().trim().isEmpty()
                && clutchComboBox.getValue() == null
                && gearboxComboBox.getValue() == null
                && engineComboBox.getValue() == null;
    }
    private boolean isFormFilled() {
        return !carPlateNumberTextField.getText().trim().isEmpty()
                && !carModelNameTextField.getText().trim().isEmpty()
                && clutchComboBox.getValue() != null
                && gearboxComboBox.getValue() != null
                && engineComboBox.getValue() != null;
    }

    private void setFormToAddMode() {
        carBeingCreated = null;

        setButtonVisibility(confirmButton, false);
        setButtonVisibility(addCarButton, true);
        addCarButton.setDisable(!isFormFilled());

        setTitledPaneExpanded(carSpecsTitledPane, false);
        carWeightTextField.clear();
        carPriceTextField.clear();
        carMaxSpeedTextField.clear();
    }



    // «««JavaFX Event Handlers»»»
    @FXML private void initialize() {
        setTitledPaneExpanded(carSpecsTitledPane, false);
        setTitledPaneExpanded(clutchTitledPane, false);
        setTitledPaneExpanded(gearboxTitledPane, false);
        setTitledPaneExpanded(engineTitledPane, false);

        for (Clutch clutch: Simulator.CLUTCHES) {
            clutchComboBox.getItems().add(clutch.getName());
        }
        for (Gearbox gearbox: Simulator.GEARBOXES) {
            gearboxComboBox.getItems().add(gearbox.getName());
        }
        for (Engine engine: Simulator.ENGINES) {
            engineComboBox.getItems().add(engine.getName());
        }

        setFormToAddMode();
        carPlateNumberTextField.textProperty().addListener(_ -> setFormToAddMode());
        carModelNameTextField.textProperty().addListener(_ -> setFormToAddMode());
    }

    // ComboBox handlers
    @FXML private void handleClutchComboBox() {
        setFormToAddMode();

        if (clutchComboBox.getValue() == null) {
            setTitledPaneExpanded(clutchTitledPane, false);
            clutchWeightTextField.clear();
            clutchPriceTextField.clear();
            return;
        }

        setTitledPaneExpanded(clutchTitledPane, true);
        Clutch clutch = Simulator.CLUTCHES[clutchComboBox.getSelectionModel().getSelectedIndex()];
        clutchWeightTextField.setText(clutch.weightToString());
        clutchPriceTextField.setText(clutch.priceToString());
    }
    @FXML private void handleGearboxComboBox() {
        setFormToAddMode();

        if (gearboxComboBox.getValue() == null) {
            // Sync clutch ComboBox ─ clear
            clutchComboBox.setValue(null);

            setTitledPaneExpanded(gearboxTitledPane, false);
            gearboxWeightTextField.clear();
            gearboxPriceTextField.clear();
            gearboxGearRatiosTextField.clear();
            return;
        }

        int gearboxIndex = gearboxComboBox.getSelectionModel().getSelectedIndex();

        // Sync clutch ComboBox ─ select
        clutchComboBox.getSelectionModel().select(gearboxIndex);

        setTitledPaneExpanded(gearboxTitledPane, true);
        Gearbox gearbox = Simulator.GEARBOXES[gearboxIndex];
        gearboxWeightTextField.setText(gearbox.weightToString());
        gearboxPriceTextField.setText(gearbox.priceToString());
        gearboxGearRatiosTextField.setText(gearbox.gearRatiosToString());
    }
    @FXML private void handleEngineComboBox() {
        setFormToAddMode();

        if (engineComboBox.getValue() == null) {
            setTitledPaneExpanded(engineTitledPane, false);
            engineWeightTextField.clear();
            enginePriceTextField.clear();
            engineMaxRPMTextField.clear();
            return;
        }

        setTitledPaneExpanded(engineTitledPane, true);
        Engine engine = Simulator.ENGINES[engineComboBox.getSelectionModel().getSelectedIndex()];
        engineWeightTextField.setText(engine.weightToString());
        enginePriceTextField.setText(engine.priceToString());
        engineMaxRPMTextField.setText(engine.maxRPMToString());
    }

    // Button handlers
    @FXML private void handleAddCarButton() {
        setButtonVisibility(addCarButton, false);
        setButtonVisibility(confirmButton, true);

        setTitledPaneExpanded(carSpecsTitledPane, true);
        carBeingCreated = new Car(
                new Position(),
                Simulator.GEARBOXES[gearboxComboBox.getSelectionModel().getSelectedIndex()],
                Simulator.ENGINES[engineComboBox.getSelectionModel().getSelectedIndex()],
                carPlateNumberTextField.getText().trim(),
                carModelNameTextField.getText().trim()
        );
        carWeightTextField.setText(carBeingCreated.totalWeightToString());
        carPriceTextField.setText(carBeingCreated.totalPriceToString());
        carMaxSpeedTextField.setText(carBeingCreated.maxSpeedToString());
    }
    @FXML private void handleConfirmButton() {
        String plateNumber = carBeingCreated.getPlateNumber();

        String alertContentText = null;
        if (!validatePlateNumberRegex(plateNumber)) {
            alertContentText = """
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
        if (simulator.findCarByPlateNumber(plateNumber) != null) {
            alertContentText = """
                    Numer rejestracyjny "%s" już istnieje w systemie.
                    
                    Każdy pojazd w symulatorze musi posiadać unikalny numer rejestracyjny.
                    Wprowadzony numer jest już przypisany do innego pojazdu.
                    
                    Proszę wprowadzić inny, nieużywany numer rejestracyjny.
                    """.formatted(plateNumber);
        }

        if (alertContentText == null) {
            Stage stage = (Stage) confirmButton.getScene().getWindow();
            stage.close();
            return;
        }

        Stage stage = (Stage) confirmButton.getScene().getWindow();

        VBox root = (VBox) stage.getScene().getRoot();
        root.setOpacity(0.5);

        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.initStyle(StageStyle.UNDECORATED);
        alert.setHeaderText("Niepoprawny numer rejestracyjny");
        alert.setContentText(alertContentText);
        alert.getDialogPane().getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("styles.css")).toExternalForm()
        );

        alert.showAndWait();

        root.setOpacity(1.0);
        setFormToAddMode();
    }
    @FXML private void handleCancelButton() {
        if (isFormEmpty()) {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
            return;
        }

        carPlateNumberTextField.clear();
        carModelNameTextField.clear();
        gearboxComboBox.setValue(null);
        engineComboBox.setValue(null);
    }
}
