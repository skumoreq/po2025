package com.github.skumoreq.simulator.gui;

import com.github.skumoreq.simulator.*;
import com.github.skumoreq.simulator.exception.*;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

import static com.github.skumoreq.simulator.gui.JavaFxUtils.*;

public class PrimaryController implements CarObserver {

    // region > CarObserver Interface Implementation

    private void subscribeToSelectedCar() {
        Car car = carManager.selected();
        if (car != null)
            car.addObserver(this);
    }
    private void unsubscribeFromSelectedCar() {
        Car car = carManager.selected();
        if (car != null)
            car.removeObserver(this);
    }

    @Override
    public void onCarUpdate(@NotNull Car observed, @NotNull ChangedProperty event) {
        switch (event) {
            case CLUTCH_STATE -> {
                clutchIsEngaged.setText(observed.getClutchStateDisplay());
                gearboxGear.setText(observed.getGearDisplay());
            }
            case ENGINE_STATE -> carIsEngineOn.setText(observed.getEngineStateDisplay());
            case GEAR -> gearboxGear.setText(observed.getGearDisplay());
            case RPM -> engineRpm.setText(observed.getRpmDisplay());
            case SPEED -> {
                carSpeed.setText(observed.getSpeedDisplay());
                carIcon.updateEffects(observed);
            }
            case POSITION -> carIcon.updateTranslation(observed);
            case ANGLE -> carIcon.updateRotation(observed);
        }
    }
    // endregion

    // region > Instance Fields

    private final CarIcon carIcon = new CarIcon();
    private final CarManager carManager = new CarManager();

    // Logic groups for bulk operations; these require @FXML injection and must
    // be populated within the initialize() method.
    private TitledPane[] allSections;
    private TextField[] allTextFields;
    // endregion

    // region > JavaFX Injected Fields

    @FXML
    private BorderPane root;
    @FXML
    private Pane drivingArea;

    @FXML
    private TitledPane carSection;
    @FXML
    private TitledPane clutchSection;
    @FXML
    private TitledPane gearboxSection;
    @FXML
    private TitledPane engineSection;

    @FXML
    private ComboBox<String> carSelection;

    @FXML
    private Button deleteCar;

    @FXML
    private TextField carModelName;
    @FXML
    private TextField carTotalWeight;
    @FXML
    private TextField carTotalPrice;
    @FXML
    private TextField carIsEngineOn;
    @FXML
    private TextField carSpeed;
    @FXML
    private TextField clutchName;
    @FXML
    private TextField clutchWeight;
    @FXML
    private TextField clutchPrice;
    @FXML
    private TextField clutchIsEngaged;
    @FXML
    private TextField gearboxName;
    @FXML
    private TextField gearboxWeight;
    @FXML
    private TextField gearboxPrice;
    @FXML
    private TextField gearboxGear;
    @FXML
    private TextField engineName;
    @FXML
    private TextField engineWeight;
    @FXML
    private TextField enginePrice;
    @FXML
    private TextField engineRpm;

    @FXML
    private Label cursorPointDisplay;
    // endregion
    
    // region > Helper Methods

    @FunctionalInterface
    private interface CarAction {
        void run(Car car) throws CarException;
    }

    private void performCarAction(CarAction action) {
        Car car = carManager.selected();
        if (car == null) return;

        try {
            action.run(car);
        } catch (CarException exception) {
            handleCarException(exception);
        }
    }

    private Stage getPrimaryStage() {
        return (Stage) root.getScene().getWindow();
    }

    private void initializeDrivingAreaClip() {
        Rectangle clip = new Rectangle();
        clip.setArcWidth(20.0);
        clip.setArcHeight(20.0);

        drivingArea.setClip(clip);
        drivingArea.layoutBoundsProperty().addListener((_, _, newBounds) -> {
            clip.setX(newBounds.getMinX());
            clip.setY(newBounds.getMinY());
            clip.setWidth(newBounds.getWidth());
            clip.setHeight(newBounds.getHeight());
        });
    }

    private void updateStaticTextFields() {
        Car car = carManager.selected();
        if (car == null) return;

        carModelName.setText(car.getModelNameDisplay());
        carTotalWeight.setText(car.getTotalWeightDisplay());
        carTotalPrice.setText(car.getTotalPriceDisplay());

        clutchName.setText(car.getClutch().getNameDisplay());
        clutchWeight.setText(car.getClutch().getWeightDisplay());
        clutchPrice.setText(car.getClutch().getPriceDisplay());

        gearboxName.setText(car.getGearbox().getNameDisplay());
        gearboxWeight.setText(car.getGearbox().getWeightDisplay());
        gearboxPrice.setText(car.getGearbox().getPriceDisplay());

        engineName.setText(car.getEngine().getNameDisplay());
        engineWeight.setText(car.getEngine().getWeightDisplay());
        enginePrice.setText(car.getEngine().getPriceDisplay());
    }
    private void updateDynamicTextFields() {
        Car car = carManager.selected();
        if (car == null) return;

        carIsEngineOn.setText(car.getEngineStateDisplay());
        carSpeed.setText(car.getSpeedDisplay());

        clutchIsEngaged.setText(car.getClutchStateDisplay());
        gearboxGear.setText(car.getGearDisplay());
        engineRpm.setText(car.getRpmDisplay());
    }

    private void populateEverySection() {
        deleteCar.setDisable(false);
        updateStaticTextFields();
        updateDynamicTextFields();
        expand(allSections);
    }
    private void clearEverySection() {
        deleteCar.setDisable(true);
        collapse(allSections);
        clear(allTextFields);
    }

    private void handleCarException(@NotNull CarException exception) {
        Alert.AlertType alertType;
        String header;
        String content;

        switch (exception) {
            case TorqueTransferActiveException _ -> {
                alertType = Alert.AlertType.INFORMATION;
                header = "Nie można uruchomić silnika";
                content = """
                        Silnik nie może zostać uruchomiony, ponieważ samochód jest w biegu.
                        
                        Proszę wrzucić bieg neutralny i spróbować ponownie.
                        """;
            }
            case EngineStalledException _ -> {
                alertType = Alert.AlertType.ERROR;
                header = "Silnik zgasł";
                content = """
                        Silnik zgasł, ponieważ obroty spadły poniżej minimalnego poziomu.
                        
                        Proszę ponownie uruchomić silnik i utrzymywać odpowiednie obroty.
                        """;
            }
            case ClutchEngagedException _ -> {
                alertType = Alert.AlertType.INFORMATION;
                header = "Nie można zmienić biegu";
                content = """
                        Sprzęgło nie zostało wciśnięte, dlatego zmiana biegu jest niemożliwa.
                        
                        Wciśnij sprzęgło i spróbuj ponownie, aby uniknąć uszkodzenia skrzyni biegów.
                        """;
            }
            default -> throw new RuntimeException(exception);
        }

        showAlertAndWait(alertType, root, header, content);
    }
    // endregion

    // region > FXML Event Handlers
    
    @FXML
    private void initialize() {
        Platform.runLater(root::requestFocus);

        allSections = new TitledPane[] {carSection, clutchSection, gearboxSection, engineSection};
        allTextFields = new TextField[] {
                carModelName, carTotalWeight, carTotalPrice, carIsEngineOn, carSpeed,
                clutchName, clutchWeight, clutchPrice, clutchIsEngaged,
                gearboxName, gearboxWeight, gearboxPrice, gearboxGear,
                engineName, engineWeight, enginePrice, engineRpm
        };

        initializeDrivingAreaClip();
        drivingArea.getChildren().add(carIcon);

        carIcon.setVisible(false);

        collapse(allSections);
        deleteCar.setDisable(true);

        carSelection.setItems(carManager.usedPlateNumbers());
    }

    @FXML
    private void carSelectionOnAction() {
        if (isEmpty(carSelection)) {
            clearEverySection();
            carIcon.setVisible(false);
            return;
        }



        unsubscribeFromSelectedCar();

        carManager.selectByPlateNumber(carSelection.getValue());

        subscribeToSelectedCar();


        populateEverySection();

        carIcon.updateVisuals(Objects.requireNonNull(carManager.selected()));
        carIcon.setVisible(true);
    }
    
    @FXML
    private void addCarOnAction() throws IOException {
        root.setOpacity(0.5);

        Stage formStage = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(SimulatorApp.class.getResource("Form.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        FormController formController = fxmlLoader.getController();
        formController.setUsedPlateNumbers(carManager.getUsedPlateNumbers());

        formStage.initStyle(StageStyle.UNDECORATED);
        formStage.initModality(Modality.WINDOW_MODAL);
        formStage.initOwner(getPrimaryStage());

        formStage.setOnHidden(_ -> {
            Car createdCar = formController.getCreatedCar();
            createdCar.setPosition(drivingArea.getLayoutBounds().getCenterX(), drivingArea.getLayoutBounds().getCenterY());
            carManager.addEntry(createdCar);
            root.setOpacity(1.0);
        });

        formStage.setScene(scene);
        formStage.show();
    }
    @FXML
    private void deleteCarOnAction() {
        carManager.removeSelected();
    }

    @FXML
    private void startEngineOnAction() {
        performCarAction(Car::startEngine);
    }
    @FXML
    private void stopEngineOnAction() {
        performCarAction(Car::stopEngine);
    }
    @FXML
    private void pressClutchOnAction() {
        performCarAction(Car::pressClutch);
    }
    @FXML
    private void releaseClutchOnAction() {
        performCarAction(Car::releaseClutch);
    }
    @FXML
    private void shiftUpOnAction() {
        performCarAction(Car::shiftUp);
    }
    @FXML
    private void shiftDownOnAction() {
        performCarAction(Car::shiftDown);
    }
    @FXML
    private void revUpOnAction() {
        performCarAction(Car::revUp);
    }
    @FXML
    private void revDownOnAction() {
        performCarAction(Car::revDown);
    }

    @FXML
    private void drivingAreaOnKeyPressed(@NotNull KeyEvent keyEvent) {
        switch (keyEvent.getCode()) {
            case R -> performCarAction(Car::startEngine);
            case F -> performCarAction(Car::stopEngine);
            case SPACE -> performCarAction(Car::pressClutch);
            case E -> performCarAction(Car::shiftUp);
            case Q -> performCarAction(Car::shiftDown);
        }
    }
    @FXML
    private void drivingAreaOnKeyReleased(@NotNull KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.SPACE) performCarAction(Car::releaseClutch);
    }

    @FXML
    private void drivingAreaOnScroll(@NotNull ScrollEvent scrollEvent) {
        if (scrollEvent.getDeltaY() > 0) performCarAction(Car::revUp);
        else if (scrollEvent.getDeltaY() < 0) performCarAction(Car::revDown);
    }

    @FXML
    private void drivingAreaOnMouseEntered() {
        drivingArea.requestFocus();

        performCarAction(Car::resume);
    }

    @FXML
    private void drivingAreaOnMouseMoved(@NotNull MouseEvent mouseEvent) {
        double mouseX = mouseEvent.getX();
        double mouseY = mouseEvent.getY();

        cursorPointDisplay.setText(String.format("%.0f %.0f",mouseX, mouseY));

        performCarAction(car -> car.setDestination(mouseX, mouseY));
    }
    @FXML
    private void drivingAreaOnMouseExited() {
        root.requestFocus();
        cursorPointDisplay.setText("");

        performCarAction(Car::pause);
    }
    // endregion
}
