package com.github.skumoreq.simulator.gui;

import com.github.skumoreq.simulator.*;
import com.github.skumoreq.simulator.exception.CarException;
import com.github.skumoreq.simulator.exception.ClutchEngagedException;
import com.github.skumoreq.simulator.exception.EngineStalledException;
import com.github.skumoreq.simulator.exception.GearboxNotInNeutralException;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import static com.github.skumoreq.simulator.gui.JavaFxUtils.*;

public class PrimaryController implements Listener {

    CarManager carManager = new CarManager();
    Point cursorPoint = new Point();

    // region > Listener Interface Implementation

    private void subscribeToSelectedCar() {
        carManager.getSelectedCar().addListener(this);
    }
    private void unsubscribeFromSelectedCar() {
        carManager.getSelectedCar().removeListener(this);
    }

    @Override
    public void update() {
        Platform.runLater(() -> {
            updateDynamicTextFields();
            updateCarIconTranslation();
            updateCarIconMotionBlur();
        });
    }
    // endregion

    // region > Constants

    private static final double MIN_BLUR_RADIUS = 10.0;
    private static final double MAX_BLUR_RADIUS = 63.0;
    private static final double MIN_SPEED_FOR_BLUR = 50.0;
    // endregion

    // region > FXML Injected Fields

    @FXML
    private BorderPane root;

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
    private Pane drivingArea;

    @FXML
    private Group carIconNode;
    @FXML
    private ImageView carIcon;
    private Bounds carIconBounds;
    private ImageView carIconMotionBlur;

    @FXML
    private Label cursorPointDisplay;

    private TitledPane[] allSections;
    private TextField[] allTextFields;
    // endregion
    
    // region > Helper Methods

    private Stage getPrimaryStage() {
        return (Stage) root.getScene().getWindow();
    }

    private void initializeDrivingAreaClip() {
        Rectangle clip = new Rectangle();
        drivingArea.setClip(clip);

        drivingArea.layoutBoundsProperty().addListener((_, _, newBounds) -> {
            clip.setX(newBounds.getMinX());
            clip.setY(newBounds.getMinY());
            clip.setWidth(newBounds.getWidth());
            clip.setHeight(newBounds.getHeight());
        });
    }
    private void initializeCarIconMotionBlur() {
        carIconMotionBlur = new ImageView(carIcon.getImage());

        carIconMotionBlur.setFitWidth(carIcon.getFitWidth());
        carIconMotionBlur.setPreserveRatio(true);
        carIconMotionBlur.setEffect(new MotionBlur(0.0, 0.0));

        carIconNode.getChildren().add(carIconMotionBlur);
        carIconMotionBlur.toBack();

        carIconBounds = carIconNode.getBoundsInParent();
    }

    private void updateCarIconTranslation() {
        Point position = carManager.getSelectedCar().getPosition();

        carIconNode.setLayoutX(position.getX() - carIconBounds.getWidth() / 2.0);
        carIconNode.setLayoutY(position.getY() - carIconBounds.getHeight() / 2.0);
    }
    private void updateCarIconRotation() {
        Point position = carManager.getSelectedCar().getPosition();
        Point destination = carManager.getSelectedCar().getDestination();

        carIconNode.setRotate(position.angleTo(destination));
    }
    private void updateCarIconMotionBlur() {
        Car car = carManager.getSelectedCar();
        Point position = car.getPosition();
        Point destination = car.getDestination();

        double speed = car.getSpeed();
        double topSpeed = car.calculateTopSpeed();

        if (speed < MIN_SPEED_FOR_BLUR || position.equals(destination)) {
            carIconMotionBlur.setOpacity(0.0);
            return;
        }

        // Linearly interpolate radius based on speed.
        double radius = (MIN_BLUR_RADIUS * (topSpeed - speed) + MAX_BLUR_RADIUS * (speed - MIN_SPEED_FOR_BLUR))
                / (topSpeed - MIN_SPEED_FOR_BLUR);

        MotionBlur motionBlur = (MotionBlur) carIconMotionBlur.getEffect();
        motionBlur.setRadius(radius);

        carIconMotionBlur.setOpacity(1.0);
    }

    private void updateStaticTextFields() {
        Car car = carManager.getSelectedCar();
        Clutch clutch = carManager.getSelectedCar().getGearbox().getClutch();
        Gearbox gearbox = carManager.getSelectedCar().getGearbox();
        Engine engine = carManager.getSelectedCar().getEngine();

        carModelName.setText(car.getModelName());
        carTotalWeight.setText(car.getTotalWeightDisplay());
        carTotalPrice.setText(car.getTotalPriceDisplay());

        clutchName.setText(clutch.getName());
        clutchWeight.setText(clutch.getWeightDisplay());
        clutchPrice.setText(clutch.getPriceDisplay());

        gearboxName.setText(gearbox.getName());
        gearboxWeight.setText(gearbox.getWeightDisplay());
        gearboxPrice.setText(gearbox.getPriceDisplay());

        engineName.setText(engine.getName());
        engineWeight.setText(engine.getWeightDisplay());
        enginePrice.setText(engine.getPriceDisplay());
    }
    private void updateDynamicTextFields() {
        Car car = carManager.getSelectedCar();
        Clutch clutch = carManager.getSelectedCar().getGearbox().getClutch();
        Gearbox gearbox = carManager.getSelectedCar().getGearbox();
        Engine engine = carManager.getSelectedCar().getEngine();

        carIsEngineOn.setText(car.getEngineStatusDisplay());
        carSpeed.setText(car.getSpeedDisplay());

        clutchIsEngaged.setText(clutch.getEngagementStatusDisplay());
        gearboxGear.setText(gearbox.getGearDisplay());
        engineRpm.setText(engine.getRpmDisplay());
    }

    private void populateEverySection() {
        carManager.setSelectedCarByPlateNumber(carSelection.getValue());

        subscribeToSelectedCar();

        deleteCar.setDisable(false);
        updateStaticTextFields();
        updateDynamicTextFields();
        expand(allSections);
    }
    private void clearEverySection() {
        unsubscribeFromSelectedCar();

        deleteCar.setDisable(true);
        collapse(allSections);
        clear(allTextFields);
    }

    private void handleCarException(@NotNull CarException exception) {
        Alert.AlertType alertType;
        String header;
        String content;

        switch (exception) {
            case GearboxNotInNeutralException _ -> {
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
            default -> throw exception;
        }

        showAlertAndWait(alertType, root, header, content);
    }
    // endregion

    // region > FXML Event Handlers
    
    @FXML
    private void initialize() {
        allSections = new TitledPane[] {
                carSection, clutchSection,
                gearboxSection, engineSection
        };
        allTextFields = new TextField[] {
                carModelName, carTotalWeight, carTotalPrice, carIsEngineOn, carSpeed,
                clutchName, clutchWeight, clutchPrice, clutchIsEngaged,
                gearboxName, gearboxWeight, gearboxPrice, gearboxGear,
                engineName, engineWeight, enginePrice, engineRpm
        };

        initializeDrivingAreaClip();
        initializeCarIconMotionBlur();

        carIconNode.setVisible(false);
        deleteCar.setDisable(true);
        collapse(allSections);

        carManager.getCars().addListener((ListChangeListener<Car>) _ -> {
            ObservableList<String> items = FXCollections.observableArrayList();
            for (Car car : carManager.getCars()) items.add(car.getPlateNumber());
            carSelection.setItems(items);
        });
    }
    
    @FXML
    private void carSelectionOnAction() {
        if (isEmpty(carSelection)) {
            carIconNode.setVisible(false);
            clearEverySection();
            return;
        }

        populateEverySection();
        updateCarIconTranslation();
        updateCarIconRotation();
        updateCarIconMotionBlur();
        carIconNode.setVisible(true);
    }
    
    @FXML
    private void addCarOnAction() throws IOException {
        root.setOpacity(0.5);

        Stage formStage = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(SimulatorApp.class.getResource("Form.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        FormController formController = fxmlLoader.getController();
        formController.setUsedPlateNumbers(carManager.getAllPlateNumbers());

        formStage.initStyle(StageStyle.UNDECORATED);
        formStage.initModality(Modality.WINDOW_MODAL);
        formStage.initOwner(getPrimaryStage());

        formStage.setOnHidden(_ -> {
            Car createdCar = formController.getCreatedCar();
            carManager.addCar(createdCar);
            root.setOpacity(1.0);
        });

        formStage.setScene(scene);
        formStage.show();
    }
    @FXML
    private void deleteCarOnAction() {
        // ComboBox onAction will fire when items change, calling handleCarComboBox()
        // which will unsubscribe via unsubscribeFromSelectedCar()
        carManager.removeSelectedCar();
    }

    @FXML
    private void startEngineOnAction() {
        try {
            carManager.getSelectedCar().startEngine();
        } catch (CarException exception) {
            handleCarException(exception);
        }
    }
    @FXML
    private void stopEngineOnAction() {
        try {
            carManager.getSelectedCar().stopEngine();
        } catch (CarException exception) {
            handleCarException(exception);
        }
    }

    @FXML
    private void releaseClutchOnAction() {
        try {
            carManager.getSelectedCar().releaseClutch();
        } catch (CarException exception) {
            handleCarException(exception);
        }
    }
    @FXML
    private void pressClutchOnAction() {
        try {
            carManager.getSelectedCar().pressClutch();
        } catch (CarException exception) {
            handleCarException(exception);
        }
    }

    @FXML
    private void shiftUpOnAction() {
        try {
            carManager.getSelectedCar().shiftUp();
        } catch (CarException exception) {
            handleCarException(exception);
        }
    }
    @FXML
    private void shiftDownOnAction() {
        try {
            carManager.getSelectedCar().shiftDown();
        } catch (CarException exception) {
            handleCarException(exception);
        }
    }

    @FXML
    private void revUpOnAction() {
        try {
            carManager.getSelectedCar().revUp();
        } catch (CarException exception) {
            handleCarException(exception);
        }
    }
    @FXML
    private void revDownOnAction() {
        try {
            carManager.getSelectedCar().revDown();
        } catch (CarException exception) {
            handleCarException(exception);
        }
    }

    @FXML
    private void drivingAreaOnMouseMoved(@NotNull MouseEvent mouseEvent) {
        cursorPoint.setX(mouseEvent.getX());
        cursorPoint.setY(mouseEvent.getY());

        cursorPointDisplay.setText(cursorPoint.getPointDisplay());
    }
    @FXML
    private void drivingAreaOnMouseExited() {
        cursorPointDisplay.setText("");
    }
    @FXML
    private void drivingAreaOnMouseClicked() {
        Car car = carManager.getSelectedCar();

        if (!car.isEngineOn()) return;

        Point destination = car.getDestination();

        destination.setX(cursorPoint.getX());
        destination.setY(cursorPoint.getY());

        updateCarIconRotation();
    }
    // endregion
}
