package simulator_gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;

import simulator.*;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;

public class PrimaryController implements Listener {
    // «««Listener Interface Implementation»»»
    @Override public void update() {
        Platform.runLater(this::refreshCarDisplay);
    }
    private void refreshCarDisplay() {
        if (simulator.getSelectedCar() != null) {
            updateCarCurrentSpeedTextField();
            updateClutchIsEngagedTextField();
            updateGearboxCurrentGearTextField();
            updateEngineCurrentRPMTextField();
        }
    }
    private void subscribeToSelectedCar() {
        unsubscribeFromSelectedCar();

        Car selectedCar = simulator.getSelectedCar();
        if (selectedCar != null) {
            selectedCar.addListener(this);
        }
    }
    private void unsubscribeFromSelectedCar() {
        Car selectedCar = simulator.getSelectedCar();
        if (selectedCar != null) {
            selectedCar.removeListener(this);
        }
    }



    // «««JavaFX Scene Controls»»»

    // TitledPane
    @FXML private TitledPane carSpecsTitledPane;
    @FXML private TitledPane clutchTitledPane;
    @FXML private TitledPane gearboxTitledPane;
    @FXML private TitledPane engineTitledPane;

    // ComboBox
    @FXML private ComboBox<String> carSelectionComboBox;

    // Button
    @FXML private Button addCarButton;
    @FXML private Button deleteCarButton;
    @FXML private Button carTurnOnButton;
    @FXML private Button catTurnOffButton;
    @FXML private Button clutchEngageButton;
    @FXML private Button clutchDisengageButton;
    @FXML private Button gearboxShiftUpButton;
    @FXML private Button gearboxShiftDownButton;
    @FXML private Button engineIncreaseRPMButton;
    @FXML private Button engineDecreaseRPMButton;

    // TextField
    @FXML private TextField carModelNameTextField;
    @FXML private TextField carWeightTextField;
    @FXML private TextField carPriceTextField;
    @FXML private TextField carCurrentSpeedTextField;
    @FXML private TextField clutchNameTextField;
    @FXML private TextField clutchWeightTextField;
    @FXML private TextField clutchPriceTextField;
    @FXML private TextField clutchIsEngagedTextField;
    @FXML private TextField gearboxNameTextField;
    @FXML private TextField gearboxWeightTextField;
    @FXML private TextField gearboxPriceTextField;
    @FXML private TextField gearboxCurrentGearTextField;
    @FXML private TextField engineNameTextField;
    @FXML private TextField engineWeightTextField;
    @FXML private TextField enginePriceTextField;
    @FXML private TextField engineCurrentRPMTextField;

    // Control groups ─ For batch operations; Arrays populated in initialize()
    private TitledPane[] titledPanes;
    private TextField[] textFields;



    // «««Main Simulator Instance»»»
    Simulator simulator = new Simulator();



    // «««Helper Methods»»»
    private void setAllTitledPanesExpanded(boolean expanded) {
        for (TitledPane titledPane : titledPanes) {
            titledPane.setCollapsible(true);
            titledPane.setExpanded(expanded);
            titledPane.setCollapsible(false);
        }
    }

    private void updateCarCurrentSpeedTextField() {
        carCurrentSpeedTextField.setText(simulator.getSelectedCar().getSpeedText());
    }
    private void updateClutchIsEngagedTextField() {
        clutchIsEngagedTextField.setText(simulator.getSelectedCar().getGearbox().getClutch().getEngagementStatusText());
    }
    private void updateGearboxCurrentGearTextField() {
        gearboxCurrentGearTextField.setText(simulator.getSelectedCar().getGearbox().getGearText());
    }
    private void updateEngineCurrentRPMTextField() {
        engineCurrentRPMTextField.setText(simulator.getSelectedCar().getEngine().getRpmText());
    }

    private void updateAllTextFields() {
        carModelNameTextField.setText(simulator.getSelectedCar().getModelName());
        carWeightTextField.setText(simulator.getSelectedCar().getTotalWeightText());
        carPriceTextField.setText(simulator.getSelectedCar().getTotalPriceText());
        updateCarCurrentSpeedTextField();

        clutchNameTextField.setText(simulator.getSelectedCar().getGearbox().getClutch().getName());
        clutchWeightTextField.setText(simulator.getSelectedCar().getGearbox().getClutch().getWeightText());
        clutchPriceTextField.setText(simulator.getSelectedCar().getGearbox().getClutch().getPriceText());
        updateClutchIsEngagedTextField();

        gearboxNameTextField.setText(simulator.getSelectedCar().getGearbox().getName());
        gearboxWeightTextField.setText(simulator.getSelectedCar().getGearbox().getWeightText());
        gearboxPriceTextField.setText(simulator.getSelectedCar().getGearbox().getPriceText());
        updateGearboxCurrentGearTextField();

        engineNameTextField.setText(simulator.getSelectedCar().getEngine().getName());
        engineWeightTextField.setText(simulator.getSelectedCar().getEngine().getWeightText());
        enginePriceTextField.setText(simulator.getSelectedCar().getEngine().getPriceText());
        updateEngineCurrentRPMTextField();
    }
    private void clearAllTextFields() {
        for (TextField textField : textFields) {
            textField.clear();
        }
    }



    // «««JavaFX Event Handlers»»»
    @FXML private void initialize() {
        titledPanes = new TitledPane[] {
                carSpecsTitledPane, clutchTitledPane,
                gearboxTitledPane, engineTitledPane
        };
        textFields = new TextField[] {
                carModelNameTextField, carWeightTextField, carPriceTextField, carCurrentSpeedTextField,
                clutchNameTextField, clutchWeightTextField, clutchPriceTextField, clutchIsEngagedTextField,
                gearboxNameTextField, gearboxWeightTextField, gearboxPriceTextField, gearboxCurrentGearTextField,
                engineNameTextField, engineWeightTextField, enginePriceTextField, engineCurrentRPMTextField
        };

        deleteCarButton.setDisable(true);
        setAllTitledPanesExpanded(false);

        simulator.getCars().addListener((ListChangeListener<Car>) _ -> {
            ObservableList<String> items = FXCollections.observableArrayList();
            for (Car car : simulator.getCars()) {
                items.add(car.getPlateNumber());
            }
            carSelectionComboBox.setItems(items);
        });
    }

    // ComboBox handlers
    @FXML private void handleCarComboBox() {
        if (carSelectionComboBox.getValue() == null) {
            unsubscribeFromSelectedCar();

            deleteCarButton.setDisable(true);

            setAllTitledPanesExpanded(false);
            clearAllTextFields();

            return;
        }

        String plateNumber = carSelectionComboBox.getValue();
        simulator.selectCarByPlateNumber(plateNumber);

        subscribeToSelectedCar();

        deleteCarButton.setDisable(false);

        setAllTitledPanesExpanded(true);
        updateAllTextFields();
    }

    // Button handlers
    @FXML private void handleAddCarButton() throws IOException {
        Stage primaryStage = (Stage) addCarButton.getScene().getWindow();

        BorderPane root = (BorderPane) primaryStage.getScene().getRoot();
        root.setOpacity(0.5);

        Stage formStage = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FormView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        FormController formController = fxmlLoader.getController();
        formController.setSimulator(simulator);

        formStage.initStyle(StageStyle.UNDECORATED);
        formStage.initModality(Modality.WINDOW_MODAL);
        formStage.initOwner(primaryStage);

        // Listen for window close
        formStage.setOnHidden(_ -> {
            root.setOpacity(1.0);

            Car createdCar = formController.getCreatedCar();
            if (createdCar != null) {
                simulator.addCar(createdCar);
            }
        });

        formStage.setScene(scene);
        formStage.show();
    }
    @FXML private void handleDeleteCarButton() {
        // ComboBox onAction will fire when items change, calling handleCarComboBox()
        // which will unsubscribe via unsubscribeFromSelectedCar()
        simulator.removeSelectedCar();
    }
    @FXML private void handleCarTurnOnButton() {
        simulator.getSelectedCar().startEngine();
    }
    @FXML private void handleCarTurnOffButton() {
        simulator.getSelectedCar().stopEngine();
    }
    @FXML private void handleClutchEngageButton() {
        simulator.getSelectedCar().releaseClutch();
    }
    @FXML private void handleClutchDisengageButton() {
        simulator.getSelectedCar().pressClutch();
    }
    @FXML private void handleGearboxShiftUpButton() {
        simulator.getSelectedCar().shiftUp();
    }
    @FXML private void handleGearboxShiftDownButton() {
        simulator.getSelectedCar().shiftDown();
    }
    @FXML private void handleEngineIncreaseRPMButton() {
        simulator.getSelectedCar().revUp();
    }
    @FXML private void handleEngineDecreaseRPMButton() {
        simulator.getSelectedCar().revDown();
    }
}
