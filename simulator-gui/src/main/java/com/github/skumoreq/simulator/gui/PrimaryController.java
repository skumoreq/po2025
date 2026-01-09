package com.github.skumoreq.simulator.gui;

import com.github.skumoreq.simulator.Car;
import com.github.skumoreq.simulator.CarManager;
import com.github.skumoreq.simulator.CarObserver;
import com.github.skumoreq.simulator.Point;
import com.github.skumoreq.simulator.exception.CarException;
import com.github.skumoreq.simulator.exception.ClutchEngagedException;
import com.github.skumoreq.simulator.exception.EngineStalledException;
import com.github.skumoreq.simulator.exception.TorqueTransferActiveException;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
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
        var oldCar = carManager.selected();

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

    // region ⮞ Constants

    private static final JavaFXUtils.AlertInfo CLUTCH_ENGAGED_INFO = new JavaFXUtils.AlertInfo(
            Alert.AlertType.WARNING,
            "Zgrzyt skrzyni biegów",
            "Musisz wcisnąć sprzęgło, aby zmienić bieg."
    );
    private static final JavaFXUtils.AlertInfo TORQUE_TRANSFER_ACTIVE_INFO = new JavaFXUtils.AlertInfo(
            Alert.AlertType.WARNING,
            "Silnik zablokowany",
            "Nie można uruchomić silnika na biegu. Wrzuć luz."
    );
    private static final JavaFXUtils.AlertInfo ENGINE_STALLED_INFO = new JavaFXUtils.AlertInfo(
            Alert.AlertType.WARNING,
            "Silnik zgasł",
            "Zbyt niskie obroty spowodowały zgaśnięcie silnika."
    );
    private static final JavaFXUtils.AlertInfo UNKNOWN_CAR_EXCEPTION = new JavaFXUtils.AlertInfo(
            Alert.AlertType.ERROR,
            "Nieoczekiwany błąd",
            "Wystąpił problem z systemami pojazdu. Spróbuj ponownie."
    );
    // endregion

    // region ⮞ FXML Injected Fields

    private @FXML BorderPane primaryRoot;
    private @FXML Pane drivingArea;

    private @FXML TitledPane carSection;
    private @FXML TitledPane clutchSection;
    private @FXML TitledPane transmissionSection;
    private @FXML TitledPane engineSection;

    private @FXML ComboBox<String> carSelection;

    private @FXML Button deleteCar;
    private @FXML MenuButton options;

    private @FXML CheckBox reverseScroll;
    private @FXML CheckBox useDarkTheme;

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
        var selectedCar = carManager.selected();

        if (selectedCar == null) return;

        try {
            action.run(selectedCar);
        } catch (CarException exception) {
            handleCarException(exception);
        }
    }

    private void handleCarException(@NotNull CarException e) {
        var alertInfo = switch (e) {
            case ClutchEngagedException _ -> CLUTCH_ENGAGED_INFO;
            case TorqueTransferActiveException _ -> TORQUE_TRANSFER_ACTIVE_INFO;
            case EngineStalledException _ -> ENGINE_STALLED_INFO;
            default -> UNKNOWN_CAR_EXCEPTION;
        };

        JavaFXUtils.showAlertAndWait(primaryRoot, alertInfo);
    }

    private Stage getPrimaryStage() {
        return (Stage) primaryRoot.getScene().getWindow();
    }

    private void initializeDrivingAreaClip() {
        var clip = new Rectangle();

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

    private void initializeCarSelection() {
        var plates = carManager.usedPlateNumbers();

        carSelection.setItems(plates);

        plates.addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    carSelection.setValue(change.getAddedSubList().getLast());
                } else if (change.wasRemoved()) {
                    if (plates.isEmpty()) {
                        JavaFXUtils.clearSelection(carSelection);
                    } else {
                        int removedIndex = change.getFrom();
                        carSelection.setValue(removedIndex == 0 ? plates.getLast() : plates.get(removedIndex - 1));
                    }
                }
            }
        });
    }

    private void updateAllFields() {
        var car = carManager.selected();

        if (car == null) return;

        carModelName.setText(car.getModelName());
        carTotalWeight.setText(car.getTotalWeightDisplay());
        carTotalPrice.setText(car.getTotalPriceDisplay());
        carEngineState.setText(car.getEngineStateDisplay());
        carSpeed.setText(car.getSpeedDisplay());

        clutchName.setText(car.getClutch().getName());
        clutchWeight.setText(car.getClutch().getWeightDisplay());
        clutchPrice.setText(car.getClutch().getPriceDisplay());
        clutchState.setText(car.getClutchStateDisplay());

        transmissionName.setText(car.getGearbox().getName());
        transmissionWeight.setText(car.getGearbox().getWeightDisplay());
        transmissionPrice.setText(car.getGearbox().getPriceDisplay());
        transmissionGear.setText(car.getGearDisplay());

        engineName.setText(car.getEngine().getName());
        engineWeight.setText(car.getEngine().getWeightDisplay());
        enginePrice.setText(car.getEngine().getPriceDisplay());
        engineRpm.setText(car.getRpmDisplay());
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
        allSections = new TitledPane[]{carSection, clutchSection, transmissionSection, engineSection};
        allFields = new TextField[]{
                carModelName, carTotalWeight, carTotalPrice, carEngineState, carSpeed,
                clutchName, clutchWeight, clutchPrice, clutchState,
                transmissionName, transmissionWeight, transmissionPrice, transmissionGear,
                engineName, engineWeight, enginePrice, engineRpm};

        Platform.runLater(primaryRoot::requestFocus);

        JavaFXUtils.applyStyleTheme(primaryRoot, false);
        JavaFXUtils.forceCollapse(allSections);

        initializeDrivingAreaClip();
        initializeCarSelection();

        carIcon.setVisible(false);
        deleteCar.setDisable(true);

        drivingArea.getChildren().add(carIcon);
        drivingArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.TAB) event.consume();
        });

        useDarkTheme.selectedProperty().addListener((_, _, selected) -> {
            JavaFXUtils.applyStyleTheme(primaryRoot, selected);

            // Update options context-menu CSS style.
            options.hide();
            options.show();
        });
    }

    @FXML
    private void carSelectionOnAction() {
        if (JavaFXUtils.isEmpty(carSelection)) {
            JavaFXUtils.hide(carIcon);
            clearEverySection();
            return;
        }

        var car = carManager.findByPlateNumber(carSelection.getValue());

        if (car == null) return;

        carIcon.updateAllVisuals(car);

        updateCarSubscription(car);
        populateEverySection();
        JavaFXUtils.show(carIcon);
    }

    @FXML
    private void addCarOnAction() throws IOException {
        primaryRoot.getStyleClass().add("dimmed");

        var formStage = new Stage();
        var fxmlLoader = new FXMLLoader(SimulatorApp.class.getResource("Form.fxml"));
        var formRoot = (Parent) fxmlLoader.load();
        var formScene = new Scene(formRoot);

        JavaFXUtils.applyStyleTheme(formRoot, primaryRoot);

        formStage.initOwner(getPrimaryStage());
        formStage.initModality(Modality.WINDOW_MODAL);
        formStage.initStyle(StageStyle.UNDECORATED);
        formStage.setScene(formScene);

        FormController formController = fxmlLoader.getController();
        formController.importUsedPlateNumbers(carManager.getUsedPlateNumbers());
        formController.importInitialPosition(new Point(
                drivingArea.getLayoutBounds().getCenterX(),
                drivingArea.getLayoutBounds().getCenterY()
        ));

        formStage.setOnHidden(_ -> {
            var createdCar = formController.exportResult();

            if (createdCar != null) carManager.addEntry(createdCar);

            primaryRoot.getStyleClass().remove("dimmed");
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
        primaryRoot.requestFocus();
        mouseCoords.setText("");

        performCarAction(Car::pause);
    }

    @FXML
    private void drivingAreaOnMouseMoved(@NotNull MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        mouseCoords.setText("x: %.0f, y: %.0f".formatted(mouseX, mouseY));

        performCarAction(car -> car.updateDestination(mouseX, mouseY, 8.0));
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
