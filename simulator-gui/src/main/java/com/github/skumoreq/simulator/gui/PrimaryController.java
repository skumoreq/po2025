package com.github.skumoreq.simulator.gui;

import com.github.skumoreq.simulator.Car;
import com.github.skumoreq.simulator.CarManager;
import com.github.skumoreq.simulator.CarObserver;
import com.github.skumoreq.simulator.exception.CarException;
import com.github.skumoreq.simulator.exception.ClutchEngagedException;
import com.github.skumoreq.simulator.exception.EngineStalledException;
import com.github.skumoreq.simulator.exception.TorqueTransferActiveException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class PrimaryController implements CarObserver {

    // region ⮞ CarObserver Interface Implementation

    private void updateCarSubscription(@NotNull Car newCar) {
        Car oldCar = carManager.selected();

        if (oldCar != null) oldCar.removeObserver(this);

        newCar.addObserver(this);

        carManager.select(newCar);
    }

    @Override
    public void onCarUpdate(@NotNull Car car, @NotNull ChangedProperty property) {
        switch (property) {
            case CLUTCH_STATE -> {
                clutchState.setText(car.getClutchStateDisplay());
                transmissionGear.setText(car.getGearDisplay());
            }
            case ENGINE_STATE -> carEngineState.setText(car.getEngineStateDisplay());
            case GEAR -> transmissionGear.setText(car.getGearDisplay());
            case RPM -> engineRpm.setText(car.getRpmDisplay());
            case SPEED -> {
                carSpeed.setText(car.getSpeedDisplay());
                carIcon.updateEffects(car);
            }
            case POSITION -> carIcon.updateTranslation(car);
            case ANGLE -> carIcon.updateRotation(car);
        }
    }
    // endregion

    // region ⮞ FXML Injected Fields

    private @FXML BorderPane root;
    private @FXML Pane drivingArea;

    private @FXML TitledPane carSection;
    private @FXML TitledPane clutchSection;
    private @FXML TitledPane transmissionSection;
    private @FXML TitledPane engineSection;

    private @FXML ComboBox<String> carSelection;

    private @FXML Button deleteCar;

    private @FXML CheckBox reverseScroll;
    private @FXML CheckBox darkTheme;

    private @FXML TextField carModelName;
    private @FXML TextField carTotalWeight;
    private @FXML TextField carTotalPrice;
    private @FXML TextField carEngineState;
    private @FXML TextField carSpeed;
    private @FXML TextField clutchName;
    private @FXML TextField clutchWeight;
    private @FXML TextField clutchPrice;
    private @FXML TextField clutchState;
    private @FXML TextField transmissionName;
    private @FXML TextField transmissionWeight;
    private @FXML TextField transmissionPrice;
    private @FXML TextField transmissionGear;
    private @FXML TextField engineName;
    private @FXML TextField engineWeight;
    private @FXML TextField enginePrice;
    private @FXML TextField engineRpm;

    private @FXML Label mouseCoords;
    // endregion

    // region ⮞ Instance Fields

    private final @NotNull CarIcon carIcon = new CarIcon();
    private final @NotNull CarManager carManager = new CarManager();

    // Logic groups for bulk operations; these require @FXML injection and must
    // be populated within the initialize() method.
    private @NotNull TitledPane[] allSections;
    private @NotNull TextField[] allFields;
    // endregion

    // region ⮞ Helper Methods

    @FunctionalInterface
    private interface CarAction {
        void run(@NotNull Car car) throws CarException;
    }

    /**
     * Wraps car actions to ensure a car is selected and handles any resulting
     * CarExceptions. Acts as a centralized safety net for all car-related UI
     * commands.
     */
    private void performCarAction(@NotNull CarAction action) {
        Car selectedCar = carManager.selected();

        if (selectedCar == null) return;

        try {
            action.run(selectedCar);
        } catch (CarException exception) {
            handleCarException(exception);
        }
    }

    private void handleCarException(@NotNull CarException e) {
        JavaFXUtils.AlertInfo info = switch (e) {
            case ClutchEngagedException _ -> new JavaFXUtils.AlertInfo(
                    Alert.AlertType.WARNING,
                    "Zgrzyt skrzyni biegów",
                    "Musisz wcisnąć sprzęgło, aby zmienić bieg."
            );
            case TorqueTransferActiveException _ -> new JavaFXUtils.AlertInfo(
                    Alert.AlertType.WARNING,
                    "Silnik zablokowany",
                    "Nie można uruchomić silnika na biegu. Wrzuć luz."
            );
            case EngineStalledException _ -> new JavaFXUtils.AlertInfo(
                    Alert.AlertType.ERROR,
                    "Silnik zgasł",
                    "Zbyt niskie obroty spowodowały zgaśnięcie silnika."
            );
            default -> new JavaFXUtils.AlertInfo(
                    Alert.AlertType.ERROR,
                    "Nieoczekiwany błąd",
                    "Wystąpił problem z systemami pojazdu. Spróbuj ponownie."
            );
        };

        JavaFXUtils.showAlertAndWait(root, info);
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

    private void updateAllFields() {
        Car selectedCar = carManager.selected();

        if (selectedCar == null) return;

        carModelName.setText(selectedCar.getModelName());
        carTotalWeight.setText(selectedCar.getTotalWeightDisplay());
        carTotalPrice.setText(selectedCar.getTotalPriceDisplay());
        carEngineState.setText(selectedCar.getEngineStateDisplay());
        carSpeed.setText(selectedCar.getSpeedDisplay());

        clutchName.setText(selectedCar.getClutch().getName());
        clutchWeight.setText(selectedCar.getClutch().getWeightDisplay());
        clutchPrice.setText(selectedCar.getClutch().getPriceDisplay());
        clutchState.setText(selectedCar.getClutchStateDisplay());

        transmissionName.setText(selectedCar.getGearbox().getName());
        transmissionWeight.setText(selectedCar.getGearbox().getWeightDisplay());
        transmissionPrice.setText(selectedCar.getGearbox().getPriceDisplay());
        transmissionGear.setText(selectedCar.getGearDisplay());

        engineName.setText(selectedCar.getEngine().getName());
        engineWeight.setText(selectedCar.getEngine().getWeightDisplay());
        enginePrice.setText(selectedCar.getEngine().getPriceDisplay());
        engineRpm.setText(selectedCar.getRpmDisplay());
    }

    private void populateEverySection() {
        deleteCar.setDisable(false);

        updateAllFields();
        JavaFXUtils.forceExpand(allSections);
    }

    private void clearEverySection() {
        deleteCar.setDisable(true);

        JavaFXUtils.forceCollapse(allSections);
        JavaFXUtils.clear(allFields);
    }
    // endregion

    // region ⮞ FXML Event Handlers

    @FXML
    private void initialize() {
        JavaFXUtils.applyStyleTheme(root, false);
        Platform.runLater(root::requestFocus);

        allSections = new TitledPane[]{carSection, clutchSection, transmissionSection, engineSection};
        allFields = new TextField[]{
                carModelName, carTotalWeight, carTotalPrice, carEngineState, carSpeed,
                clutchName, clutchWeight, clutchPrice, clutchState,
                transmissionName, transmissionWeight, transmissionPrice, transmissionGear,
                engineName, engineWeight, enginePrice, engineRpm};

        darkTheme.selectedProperty().addListener((_, _, selected) -> JavaFXUtils.applyStyleTheme(root, selected));

        initializeDrivingAreaClip();
        drivingArea.getChildren().add(carIcon);
        drivingArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) event.consume();
        });

        carIcon.setVisible(false);

        JavaFXUtils.forceCollapse(allSections);
        deleteCar.setDisable(true);

        carSelection.setItems(carManager.usedPlateNumbers());
    }

    @FXML
    private void carSelectionOnAction() {
        if (JavaFXUtils.isEmpty(carSelection)) {
            JavaFXUtils.hide(carIcon);
            clearEverySection();
            return;
        }

        Car car = carManager.findByPlateNumber(carSelection.getValue());

        if (car == null) return;

        carIcon.updateAllVisuals(car);

        updateCarSubscription(car);
        populateEverySection();
        JavaFXUtils.show(carIcon);
    }

    @FXML
    private void addCarOnAction() throws IOException {
        root.getStyleClass().add("dimmed");

        Stage formStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(SimulatorApp.class.getResource("Form.fxml"));
        Parent formRoot = fxmlLoader.load();
        Scene formScene = new Scene(formRoot);

        JavaFXUtils.applyStyleTheme(formRoot, root);

        formStage.initOwner(getPrimaryStage());
        formStage.initModality(Modality.WINDOW_MODAL);
        formStage.initStyle(StageStyle.UNDECORATED);
        formStage.setScene(formScene);

        FormController formController = fxmlLoader.getController();
        formController.importUsedPlateNumbers(carManager.getUsedPlateNumbers());

        formStage.setOnHidden(_ -> {
            Car createdCar = formController.exportResult();

            if (createdCar != null) {
                createdCar.setPosition(
                        drivingArea.getLayoutBounds().getCenterX(),
                        drivingArea.getLayoutBounds().getCenterY()
                );
                carManager.addEntry(createdCar);
            }

            root.getStyleClass().remove("dimmed");
        });

        formStage.show();
    }

    @FXML
    private void deleteCarOnAction() {
        carManager.removeEntry(carManager.selected());
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
        performCarAction(car -> car.revUp(1.0));
    }

    @FXML
    private void revDownOnAction() {
        performCarAction(car -> car.revDown(1.0));
    }

    @FXML
    private void drivingAreaOnKeyPressed(@NotNull KeyEvent event) {
        switch (event.getCode()) {
            case R -> performCarAction(Car::startEngine);
            case F -> performCarAction(Car::stopEngine);
            case SPACE -> performCarAction(Car::pressClutch);
            case E -> performCarAction(Car::shiftUp);
            case Q -> performCarAction(Car::shiftDown);
        }
    }

    @FXML
    private void drivingAreaOnKeyReleased(@NotNull KeyEvent event) {
        if (event.getCode() == KeyCode.SPACE) performCarAction(Car::releaseClutch);
    }

    @FXML
    private void drivingAreaOnMousePressed(@NotNull MouseEvent event) {
        switch (event.getButton()) {
            case PRIMARY -> performCarAction(Car::pressClutch);
            case FORWARD -> performCarAction(Car::shiftUp);
            case BACK -> performCarAction(Car::shiftDown);
        }
    }

    @FXML
    private void drivingAreaOnMouseReleased(@NotNull MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) performCarAction(Car::releaseClutch);
    }

    @FXML
    private void drivingAreaOnScroll(@NotNull ScrollEvent event) {
        double delta = event.getDeltaY();
        double multiplier = event.getMultiplierY();

        if (delta == 0) return;

        double normalizedImpulse = Math.abs(delta / multiplier);

        performCarAction(car -> {
            if ((delta > 0) != reverseScroll.isSelected()) car.revUp(normalizedImpulse);
            else car.revDown(normalizedImpulse);
        });
    }

    @FXML
    private void drivingAreaOnMouseEntered() {
        drivingArea.requestFocus();

        performCarAction(Car::resume);
    }

    @FXML
    private void drivingAreaOnMouseExited() {
        root.requestFocus();
        mouseCoords.setText("");

        performCarAction(Car::pause);
    }

    @FXML
    private void drivingAreaOnMouseMoved(@NotNull MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        mouseCoords.setText(String.format("x: %.0f, y: %.0f", mouseX, mouseY));

        performCarAction(car -> car.setDestination(mouseX, mouseY));
    }

    @FXML
    private void drivingAreaOnMouseDragged(@NotNull MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        // This is the only known way to go around JavaFX drag and drop UI implementation.
        // Without this single check you could set out of bounds destination.
        if (drivingArea.getLayoutBounds().contains(mouseX, mouseY))
            drivingAreaOnMouseMoved(event);
    }
    // endregion
}
