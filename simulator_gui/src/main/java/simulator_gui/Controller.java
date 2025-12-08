package simulator_gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

import simulator.*;

import java.util.Arrays;

public class Controller {
    private static final String COMPETITORS_COMBO_BOX_DEFAULT_VALUE = "Nowy samochód";

    Tournament tournament = new Tournament();

    // Competitors controls
    @FXML private ComboBox<String> competitorsComboBox;
    @FXML private Button addCompetitorButton;
    @FXML private Button removeCompetitorButton;

    // Car controls
    @FXML private TextField carModelNameTextField;
    @FXML private TextField carPlateNumberTextField;
    @FXML private TextField carWeightTextField;
    @FXML private TextField carSpeedTextField;
    @FXML private Button carSwitchOnButton;
    @FXML private Button carSwitchOffButton;

    // Gearbox controls
    @FXML private TextField gearboxNameTextField;
    @FXML private TextField gearboxPriceTextField;
    @FXML private TextField gearboxWeightTextField;
    @FXML private TextField gearboxCurrentGearTextField;
    @FXML private Button gearboxShiftUpButton;
    @FXML private Button gearboxShiftDownButton;

    // Engine controls
    @FXML private TextField engineNameTextField;
    @FXML private TextField enginePriceTextField;
    @FXML private TextField engineWeightTextField;
    @FXML private TextField engineCurrentRPMTextField;
    @FXML private Button engineIncreaseRPMButton;
    @FXML private Button engineDecreaseRPMButton;

    // Clutch controls
    @FXML private TextField clutchNameTextField;
    @FXML private TextField clutchPriceTextField;
    @FXML private TextField clutchWeightTextField;
    @FXML private TextField clutchIsEngagedTextField;
    @FXML private Button clutchEngageButton;
    @FXML private Button clutchDisengageButton;

    // Initializer
    @FXML private void initialize() {
        competitorsComboBox.getItems().add(COMPETITORS_COMBO_BOX_DEFAULT_VALUE);
        competitorsComboBox.setValue(COMPETITORS_COMBO_BOX_DEFAULT_VALUE);
    }

    // Event handlers
    @FXML private void handleCompetitorsComboBox() {
        if (competitorsComboBox.getValue().equals(COMPETITORS_COMBO_BOX_DEFAULT_VALUE)) {
            addCompetitorButton.setDisable(false);
            for (TextField textField : Arrays.asList(carModelNameTextField, carPlateNumberTextField,
                    gearboxNameTextField, gearboxPriceTextField, gearboxWeightTextField,
                    engineNameTextField, enginePriceTextField, engineWeightTextField,
                    clutchNameTextField, clutchPriceTextField, clutchWeightTextField)) {
                textField.setDisable(false);
            }
            for (Button button : Arrays.asList(removeCompetitorButton,
                    carSwitchOnButton, carSwitchOffButton,
                    gearboxShiftUpButton, gearboxShiftDownButton,
                    engineIncreaseRPMButton, engineDecreaseRPMButton,
                    clutchEngageButton, clutchDisengageButton)) {
                button.setDisable(true);
            }

            for (TextField textField : Arrays.asList(
                    carModelNameTextField, carPlateNumberTextField, carWeightTextField, carSpeedTextField,
                    gearboxNameTextField, gearboxPriceTextField, gearboxWeightTextField, gearboxCurrentGearTextField,
                    engineNameTextField, enginePriceTextField, engineWeightTextField, engineCurrentRPMTextField,
                    clutchNameTextField, clutchPriceTextField, clutchWeightTextField, clutchIsEngagedTextField)) {
                textField.setText("");
            }
        } else {
            addCompetitorButton.setDisable(true);
            for (TextField textField : Arrays.asList(carModelNameTextField, carPlateNumberTextField,
                    gearboxNameTextField, gearboxPriceTextField, gearboxWeightTextField,
                    engineNameTextField, enginePriceTextField, engineWeightTextField,
                    clutchNameTextField, clutchPriceTextField, clutchWeightTextField)) {
                textField.setDisable(true);
            }
            for (Button button : Arrays.asList(removeCompetitorButton,
                    carSwitchOnButton, carSwitchOffButton,
                    gearboxShiftUpButton, gearboxShiftDownButton,
                    engineIncreaseRPMButton, engineDecreaseRPMButton,
                    clutchEngageButton, clutchDisengageButton)) {
                button.setDisable(false);
            }

            Car competitor = tournament.findCompetitor(competitorsComboBox.getValue());

            carModelNameTextField.setText(competitor.getModelName());
            carPlateNumberTextField.setText(competitor.getPlateNumber());
            carWeightTextField.setText(String.valueOf(competitor.getWeight()));
            carSpeedTextField.setText(String.valueOf(competitor.getCurrentSpeed()));

            gearboxNameTextField.setText(competitor.getGearbox().getName());
            gearboxPriceTextField.setText(String.valueOf(competitor.getGearbox().getPrice()));
            gearboxWeightTextField.setText(String.valueOf(competitor.getGearbox().getWeight()));
            gearboxCurrentGearTextField.setText(String.valueOf(competitor.getGearbox().getCurrentGear()));

            engineNameTextField.setText(competitor.getEngine().getName());
            enginePriceTextField.setText(String.valueOf(competitor.getEngine().getPrice()));
            engineWeightTextField.setText(String.valueOf(competitor.getEngine().getWeight()));
            engineCurrentRPMTextField.setText(String.valueOf(competitor.getEngine().getCurrentRPM()));

            clutchNameTextField.setText(competitor.getGearbox().getClutch().getName());
            clutchPriceTextField.setText(String.valueOf(competitor.getGearbox().getClutch().getPrice()));
            clutchWeightTextField.setText(String.valueOf(competitor.getGearbox().getClutch().getWeight()));
            clutchIsEngagedTextField.setText(String.valueOf(competitor.getGearbox().getClutch().getIsEngaged()));
        }
    }
    @FXML private void handleAddCompetitorButton() {
        Clutch clutch = new Clutch(
                clutchNameTextField.getText().trim(),
                Double.parseDouble(clutchWeightTextField.getText().trim()),
                Double.parseDouble(clutchPriceTextField.getText().trim())
        );
        Gearbox gearbox = new Gearbox(
                gearboxNameTextField.getText().trim(),
                Double.parseDouble(gearboxWeightTextField.getText().trim()),
                Double.parseDouble(gearboxPriceTextField.getText().trim()),
                clutch
        );
        Engine engine = new Engine(
                engineNameTextField.getText().trim(),
                Double.parseDouble(engineWeightTextField.getText().trim()),
                Double.parseDouble(enginePriceTextField.getText().trim())
        );
        String plateNumber = carPlateNumberTextField.getText().trim();
        String modelName = carModelNameTextField.getText().trim();

        Car competitor = new Car(new Position(), gearbox, engine, plateNumber, modelName );

        tournament.addCompetitor(competitor);

        competitorsComboBox.getItems().add(plateNumber);
        competitorsComboBox.setValue(plateNumber);
    }
    @FXML private void handleRemoveCompetitorButton() {
        String plateNumber = competitorsComboBox.getValue();

        tournament.removeCompetitor(tournament.findCompetitor(plateNumber));
        competitorsComboBox.getItems().remove(plateNumber);
    }

    @FXML private void handleCarSwitchOnButton() {
        Car competitor = tournament.findCompetitor(competitorsComboBox.getValue());
        competitor.switchOn();
    }
    @FXML private void handleCarSwtchOffButton() {
        Car competitor = tournament.findCompetitor(competitorsComboBox.getValue());
        competitor.switchOff();
    }

    @FXML private void handleGearboxShiftUpButton() {
        Car competitor = tournament.findCompetitor(competitorsComboBox.getValue());
        competitor.getGearbox().shiftUp();
    }
    @FXML private void handleGearboxShiftDownButton() {
        Car competitor = tournament.findCompetitor(competitorsComboBox.getValue());
        competitor.getGearbox().shiftDown();
    }

    @FXML private void handleEngineIncreaseRPMButton() {
        Car competitor = tournament.findCompetitor(competitorsComboBox.getValue());
        competitor.getEngine().increaseRPM();
    }
    @FXML private void handleEngineDecreaseRPMButton() {
        Car competitor = tournament.findCompetitor(competitorsComboBox.getValue());
        competitor.getEngine().decreaseRPM();
    }

    @FXML private void handleClutchEngageButton() {
        Car competitor = tournament.findCompetitor(competitorsComboBox.getValue());
        competitor.getGearbox().getClutch().engage();
    }
    @FXML private void handleClutchDisengageButton() {
        Car competitor = tournament.findCompetitor(competitorsComboBox.getValue());
        competitor.getGearbox().getClutch().disengage();
    }
}
