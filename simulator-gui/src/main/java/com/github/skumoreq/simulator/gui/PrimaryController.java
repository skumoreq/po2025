package com.github.skumoreq.simulator.gui;

import static com.github.skumoreq.simulator.gui.JavaFxUtils.*;

import com.github.skumoreq.simulator.Car;
import com.github.skumoreq.simulator.CarManager;
import com.github.skumoreq.simulator.Listener;
import com.github.skumoreq.simulator.Point;
import com.github.skumoreq.simulator.exception.CarException;
import com.github.skumoreq.simulator.exception.ClutchEngagedException;
import com.github.skumoreq.simulator.exception.EngineStalledException;
import com.github.skumoreq.simulator.exception.GearboxNotInNeutralException;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import javafx.application.Platform;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Modality;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.net.URL;


public class PrimaryController implements Listener {
    // «««Main CarManager Instance»»»
    CarManager carManager = new CarManager();

    Point cursorPoint = new Point();
    Image carImage;
    ImageView carImageView;


    // «««Listener Interface Implementation»»»
    @Override public void update() {
        Platform.runLater(() -> {
            updateDynamicTextFields();
            updateCarImageView();
        });
    }
    private void subscribeToSelectedCar() {
        carManager.getSelectedCar().addListener(this);
    }
    private void unsubscribeFromSelectedCar() {
        carManager.getSelectedCar().removeListener(this);
    }


    // «««JavaFX Scene»»»

    // «Layout»
    @FXML private BorderPane rootBorderPane;
    @FXML private Pane drivingAreaStackPane;

    // «TitledPanes»
    @FXML private TitledPane carTitledPane;
    @FXML private TitledPane clutchTitledPane;
    @FXML private TitledPane gearboxTitledPane;
    @FXML private TitledPane engineTitledPane;

    // «ComboBoxes»
    @FXML private ComboBox<String> carSelectionComboBox;

    // «Buttons»
    @FXML private Button deleteCarButton;

    // «TextFields»
    @FXML private TextField carModelNameTextField;
    @FXML private TextField carWeightTextField;
    @FXML private TextField carPriceTextField;
    @FXML private TextField carSpeedTextField;
    @FXML private TextField clutchNameTextField;
    @FXML private TextField clutchWeightTextField;
    @FXML private TextField clutchPriceTextField;
    @FXML private TextField clutchIsEngagedTextField;
    @FXML private TextField gearboxNameTextField;
    @FXML private TextField gearboxWeightTextField;
    @FXML private TextField gearboxPriceTextField;
    @FXML private TextField gearboxGearTextField;
    @FXML private TextField engineNameTextField;
    @FXML private TextField engineWeightTextField;
    @FXML private TextField enginePriceTextField;
    @FXML private TextField engineRpmTextField;
    @FXML private Label cursorPositionTextField;

    // «Control Groups» (For batch operations; Arrays populated in initialize())
    private TitledPane[] allTitledPanes;
    private TextField[] allTextFields;


    // «««Helper Methods»»»
    private void addCarImageView() {
        drivingAreaStackPane.getChildren().add(carImageView);
    }
    private void removeCarImageView() {
        drivingAreaStackPane.getChildren().remove(carImageView);
    }
    private void updateCarImageView() {
        carImageView.setLayoutX(carManager.getSelectedCar().getPosition().getX());
        carImageView.setLayoutY(carManager.getSelectedCar().getPosition().getY());

        if (carManager.getSelectedCar().getPosition().getX() == carManager.getSelectedCar().getDestination().getX()
        && carManager.getSelectedCar().getPosition().getY() == carManager.getSelectedCar().getDestination().getY()) {
            return;
        }
        carImageView.setRotate(carManager.getSelectedCar().getPosition().angleTo(carManager.getSelectedCar().getDestination()));
    }

    private void handleCarException(CarException e) {
        String title;
        String message;

        switch (e) {
            case GearboxNotInNeutralException ignored -> {
                title = "Nie można uruchomić silnika";
                message = """
                        Silnik nie może zostać uruchomiony, ponieważ samochód jest w biegu.
                        
                        Proszę wrzucić bieg neutralny i spróbować ponownie.
                        """;
            }
            case EngineStalledException ignored -> {
                title = "Silnik zgasł";
                message = """
                        Silnik zgasł, ponieważ obroty spadły poniżej minimalnego poziomu.
                        
                        Proszę ponownie uruchomić silnik i utrzymywać odpowiednie obroty.
                        """;
            }
            case ClutchEngagedException ignored -> {
                title = "Nie można zmienić biegu";
                message = """
                        Sprzęgło nie zostało wciśnięte, dlatego zmiana biegu jest niemożliwa.
                        
                        Wciśnij sprzęgło i spróbuj ponownie, aby uniknąć uszkodzenia skrzyni biegów.
                        """;
            }
            default -> {
                title = "Błąd samochodu";
                message = "";
            }
        }

        showWarningAlert(rootBorderPane, title, message);
    }

    private Stage getPrimaryStage() {
        return (Stage) rootBorderPane.getScene().getWindow();
    }

    private void updateStaticTextFields() {
        Car car = carManager.getSelectedCar();

        carModelNameTextField.setText(car.getModelName());
        carWeightTextField.setText(car.getTotalWeightDisplay());
        carPriceTextField.setText(car.getTotalPriceDisplay());

        clutchNameTextField.setText(car.getGearbox().getClutch().getName());
        clutchWeightTextField.setText(car.getGearbox().getClutch().getWeightDisplay());
        clutchPriceTextField.setText(car.getGearbox().getClutch().getPriceDisplay());

        gearboxNameTextField.setText(car.getGearbox().getName());
        gearboxWeightTextField.setText(car.getGearbox().getWeightDisplay());
        gearboxPriceTextField.setText(car.getGearbox().getPriceDisplay());

        engineNameTextField.setText(car.getEngine().getName());
        engineWeightTextField.setText(car.getEngine().getWeightDisplay());
        enginePriceTextField.setText(car.getEngine().getPriceDisplay());
    }
    private void updateDynamicTextFields() {
        Car car = carManager.getSelectedCar();

        carSpeedTextField.setText(car.getSpeedDisplay());
        clutchIsEngagedTextField.setText(car.getGearbox().getClutch().getEngagementStatusDisplay());
        gearboxGearTextField.setText(car.getGearbox().getGearDisplay());
        engineRpmTextField.setText(car.getEngine().getRpmDisplay());
    }


    // «««JavaFX Event Handlers»»»
    @FXML private void initialize() {
        allTitledPanes = new TitledPane[] {
                carTitledPane, clutchTitledPane,
                gearboxTitledPane, engineTitledPane
        };
        allTextFields = new TextField[] {
                carModelNameTextField, carWeightTextField, carPriceTextField, carSpeedTextField,
                clutchNameTextField, clutchWeightTextField, clutchPriceTextField, clutchIsEngagedTextField,
                gearboxNameTextField, gearboxWeightTextField, gearboxPriceTextField, gearboxGearTextField,
                engineNameTextField, engineWeightTextField, enginePriceTextField, engineRpmTextField
        };

        deleteCarButton.setDisable(true);
        collapse(allTitledPanes);

        carManager.getCars().addListener((ListChangeListener<Car>) ignored -> {
            ObservableList<String> items = FXCollections.observableArrayList();
            for (Car car : carManager.getCars()) items.add(car.getPlateNumber());
            carSelectionComboBox.setItems(items);
        });

        URL carImageUrl = SimulatorApplication.class.getResource("images/car.png");
        if (carImageUrl == null)
            throw new IllegalStateException("Could not find 'car.png' in SimulatorApplication path");

        carImage = new Image(carImageUrl.toExternalForm());
        carImageView = new ImageView(carImage);
        carImageView.setFitWidth(100);
        carImageView.setPreserveRatio(true);
    }

    // «Layout Event Handlers»
    @FXML private void handleDrivingAreaOnMouseClicked() {
        carManager.getSelectedCar().getDestination().setX(cursorPoint.getX());
        carManager.getSelectedCar().getDestination().setY(cursorPoint.getY());
    }
    @FXML private void handleDrivingAreaOnMouseMoved(MouseEvent event) {
        cursorPoint.setX(event.getX());
        cursorPoint.setY(event.getY());
        cursorPositionTextField.setText(cursorPoint.getPointDisplay());
    }

    // «ComboBox Event Handlers»
    @FXML private void handleCarComboBox() {
        if (isEmpty(carSelectionComboBox)) {
            unsubscribeFromSelectedCar();

            deleteCarButton.setDisable(true);
            collapse(allTitledPanes);
            clear(allTextFields);
            removeCarImageView();

            return;
        }

        String plateNumber = carSelectionComboBox.getValue();
        carManager.setSelectedCarByPlateNumber(plateNumber);

        subscribeToSelectedCar();

        deleteCarButton.setDisable(false);
        addCarImageView();
        updateCarImageView();
        updateStaticTextFields();
        updateDynamicTextFields();
        expand(allTitledPanes);
    }

    // «Button Event Handlers»
    @FXML private void handleAddCarButton() throws IOException {
        rootBorderPane.setOpacity(0.5);

        Stage formStage = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(SimulatorApplication.class.getResource("FormView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        FormController formController = fxmlLoader.getController();
        formController.setUsedPlateNumbers(carManager.getAllPlateNumbers());

        formStage.initStyle(StageStyle.UNDECORATED);
        formStage.initModality(Modality.WINDOW_MODAL);
        formStage.initOwner(getPrimaryStage());

        // Listen for window close
        formStage.setOnHidden(ignored -> {
            rootBorderPane.setOpacity(1.0);

            Car createdCar = formController.getCreatedCar();
            if (createdCar != null) carManager.addCar(createdCar);
        });

        formStage.setScene(scene);
        formStage.show();
    }
    @FXML private void handleDeleteCarButton() {
        // ComboBox onAction will fire when items change, calling handleCarComboBox()
        // which will unsubscribe via unsubscribeFromSelectedCar()
        carManager.removeSelectedCar();
    }

    @FXML private void handleStartEngineButton() {
        try {
            carManager.getSelectedCar().startEngine();
        } catch (CarException e) {
            handleCarException(e);
        }
    }
    @FXML private void handleStopEngineButton() {
        try {
            carManager.getSelectedCar().stopEngine();
        } catch (CarException e) {
            handleCarException(e);
        }
    }

    @FXML private void handleReleaseClutchButton() {
        try {
            carManager.getSelectedCar().releaseClutch();
        } catch (CarException e) {
            handleCarException(e);
        }
    }
    @FXML private void handlePressClutchButton() {
        try {
            carManager.getSelectedCar().pressClutch();
        } catch (CarException e) {
            handleCarException(e);
        }
    }

    @FXML private void handleShiftUpButton() {
        try {
            carManager.getSelectedCar().shiftUp();
        } catch (CarException e) {
            handleCarException(e);
        }
    }
    @FXML private void handleShiftDownButton() {
        try {
            carManager.getSelectedCar().shiftDown();
        } catch (CarException e) {
            handleCarException(e);
        }
    }

    @FXML private void handleRevUpButton() {
        try {
            carManager.getSelectedCar().revUp();
        } catch (CarException e) {
            handleCarException(e);
        }
    }
    @FXML private void handleRevDownButton() {
        try {
            carManager.getSelectedCar().revDown();
        } catch (CarException e) {
            handleCarException(e);
        }
    }
}
