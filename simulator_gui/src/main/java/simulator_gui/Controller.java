package simulator_gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import simulator.*;

public class Controller {
    private Tournament tournament;
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    @FXML private ComboBox<String> carChoiceComboBox;
    @FXML private Button addCarButton;
    @FXML private Button deleteCarButton;
    @FXML private TextField carNameTextField;
    @FXML private TextField carLicensePlateNumberTextField;
    @FXML private TextField carWeightTextField;
    @FXML private TextField carSpeedTextField;
    @FXML private Button carSwitchOnButton;
    @FXML private Button carSwitchOffButton;
    @FXML private TextField gearboxNameTextField;
    @FXML private TextField gearboxPriceTextField;
    @FXML private TextField gearboxWeightTextField;
    @FXML private TextField gearboxCurrentGearTextField;
    @FXML private Button gearboxShiftUpButton;
    @FXML private Button gearboxShiftDownButton;
    @FXML private TextField engineNameTextField;
    @FXML private TextField enginePriceTextField;
    @FXML private TextField engineWeightTextField;
    @FXML private TextField engineCurrentRPMTextField;
    @FXML private Button engineIncreaseRPMButton;
    @FXML private Button engineDecreaseRPMButton;
    @FXML private TextField clutchNameTextField;
    @FXML private TextField clutchPriceTextField;
    @FXML private TextField clutchWeightTextField;
    @FXML private TextField clutchStateTextField;
    @FXML private Button clutchEngageButton;
    @FXML private Button clutchDisengageButton;

    @FXML private void onCarChoiceComboBox() {
        Car car = tournament.getCarByLicensePlateNumber(carChoiceComboBox.getValue());

        carLicensePlateNumberTextField.setText(car.getPlateNumber());
    }
    @FXML private void onAddCarButton() {
        Clutch newClutch = new Clutch(
                clutchNameTextField.getText(),
                Double.parseDouble(clutchWeightTextField.getText()),
                Double.parseDouble(clutchPriceTextField.getText())
        );
        Gearbox newGearbox = new Gearbox(
                gearboxNameTextField.getText(),
                Double.parseDouble(gearboxWeightTextField.getText()),
                Double.parseDouble(gearboxPriceTextField.getText()),
                newClutch
        );
        Engine newEngine = new Engine(
                engineNameTextField.getText(),
                Double.parseDouble(engineWeightTextField.getText()),
                Double.parseDouble(enginePriceTextField.getText())
        );
        Car newCar = new Car(
                new Position(0, 0), newGearbox, newEngine, carLicensePlateNumberTextField.getText(),
                carNameTextField.getText()
        );

        tournament.addCarToTournament(newCar);
        carChoiceComboBox.getItems().add(carLicensePlateNumberTextField.getText());
    }
    @FXML private void onDeleteCarButton() {
        tournament.removeCarFromTournamentByLicensePlateNumber(carChoiceComboBox.getValue());
    }
}
