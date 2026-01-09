package com.github.skumoreq.simulator.gui;

import com.github.skumoreq.simulator.Car;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

import static com.github.skumoreq.simulator.gui.JavaFXUtils.EasingMode.*;

public class CarIcon extends Group {

    // region ⮞ Static Data Initialization

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static final String IMAGES_FILENAME_FORMAT = "car_%02d.png";
    private static final List<Image> IMAGES;

    static {
        var pathFormat = "images/" + IMAGES_FILENAME_FORMAT;
        var loadedImages = new ArrayList<Image>();

        int index = 1;
        while (true) {
            var path = pathFormat.formatted(index);
            var url = SimulatorApp.class.getResource(path);

            if (url == null) break;

            loadedImages.add(new Image(url.toExternalForm()));
            index++;
        }

        if (loadedImages.isEmpty())
            throw new RuntimeException("""
                   Resource files not found at: %s.
                   Ensure that files exist and are numbered sequentially starting from 01 (%s, %s, ...).
                   """.formatted(pathFormat, IMAGES_FILENAME_FORMAT.formatted(1), IMAGES_FILENAME_FORMAT.formatted(2))
            );

        IMAGES = List.copyOf(loadedImages);
    }

    private static final String COLORS_DATA_FILENAME = "vehicle-colors.json";
    private static final List<Color> COLORS;

    static {
        var path = "data/" + COLORS_DATA_FILENAME;

        try (var inputStream = SimulatorApp.class.getResourceAsStream(path)) {
            if (inputStream == null)
                throw new RuntimeException("Resource file not found: " + path);

            var loadedColors = new ArrayList<Color>();
            var colorsData = MAPPER.readTree(inputStream);

            for (var colorData : colorsData)
                loadedColors.add(Color.web(colorData.get("Hex (Web RGB)").asString()));

            if (loadedColors.isEmpty())
                throw new RuntimeException("The list contains no entries.");

            COLORS = List.copyOf(loadedColors);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize COLORS data.", e);
        }
    }
    // endregion

    // region ⮞ Constants

    private static final double FIT_WIDTH = 120.0;
    private static final double FIT_HEIGHT = FIT_WIDTH * 0.5;

    private static final @NotNull Map<Long, Image> CACHED_IMAGES = new HashMap<>(50);
    // endregion

    // region ⮞ Instance Fields

    private final @NotNull ImageView body = new ImageView();
    private final @NotNull ImageView shadow = new ImageView();
    private final @NotNull ImageView trails = new ImageView();

    private final @NotNull List<ImageView> layers = List.of(shadow, trails, body);

    private final @NotNull GaussianBlur shadowBlur = new GaussianBlur();
    private final @NotNull MotionBlur trailsBlur = new MotionBlur();
    // endregion

    // region ⮞ Properties & Bindings

    private final @NotNull DoubleProperty shadowOffsetFactor = new SimpleDoubleProperty();
    private final @NotNull DoubleBinding shadowOffsetX = shadowOffsetFactor.multiply(FIT_WIDTH);
    private final @NotNull DoubleBinding shadowOffsetY = shadowOffsetFactor.multiply(FIT_HEIGHT);

    private final @NotNull DoubleProperty trailsOffsetFactor = new SimpleDoubleProperty();
    private final @NotNull DoubleBinding trailsOffset = trailsOffsetFactor.multiply(FIT_WIDTH);

    private final @NotNull DoubleBinding bodyRotateRadians = Bindings.createDoubleBinding(
            () -> Math.toRadians(body.getRotate()), body.rotateProperty()
    );
    // endregion

    // region ⮞ Initialization

    public CarIcon() {
        getChildren().addAll(layers);
        setupBindings();
        setupEffects();
    }

    private void setupBindings() {
        for (var layer : layers) {
            layer.setFitWidth(FIT_WIDTH);
            layer.setFitHeight(FIT_HEIGHT);

            if (layer != body)
                layer.rotateProperty().bind(body.rotateProperty());
        }

        shadow.translateXProperty().bind(body.translateXProperty().add(shadowOffsetX));
        shadow.translateYProperty().bind(body.translateYProperty().add(shadowOffsetY));

        trails.translateXProperty().bind(Bindings.createDoubleBinding(
                () -> body.getTranslateX() - Math.cos(bodyRotateRadians.get()) * trailsOffset.get(),
                body.translateXProperty(), bodyRotateRadians, trailsOffset
        ));
        trails.translateYProperty().bind(Bindings.createDoubleBinding(
                () -> body.getTranslateY() - Math.sin(bodyRotateRadians.get()) * trailsOffset.get(),
                body.translateYProperty(), bodyRotateRadians, trailsOffset
        ));
    }

    private void setupEffects() {
        var blackColorAdjust = new ColorAdjust();
        blackColorAdjust.setBrightness(-1.0);

        shadowBlur.setInput(blackColorAdjust);

        shadow.setEffect(shadowBlur);
        trails.setEffect(trailsBlur);
    }
    // endregion

    // region ⮞ Helper Methods

    private void applyImageToAllLayers(@NotNull Image image) {
        for (var layer : layers) layer.setImage(image);
    }

    private @NotNull Image generateTintedImage(@NotNull Image baseTemplate, @NotNull Color tint) {
        int width = (int) baseTemplate.getWidth();
        int height = (int) baseTemplate.getHeight();

        var tintedImage = new WritableImage(width, height);

        var input = baseTemplate.getPixelReader();
        var output = tintedImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                var pixel = input.getColor(x, y);

                // Filters pixels within the green hue range [90°, 150°] (Center 120° ± 30°).
                // This acts as a chroma-key mask for tinting designated car body areas.
                if (Math.abs(pixel.getHue() - 120.0) <= 30.0)
                    output.setColor(x, y, Color.hsb(
                            tint.getHue(),
                            tint.getSaturation() * pixel.getSaturation(),
                            tint.getBrightness() * pixel.getBrightness(),
                            pixel.getOpacity()
                    ));
                else output.setColor(x, y, pixel);
            }
        }

        return tintedImage;
    }
    // endregion

    // region ⮞ Rendering Methods

    public void updateImage(@NotNull Car car) {
        // Generate a stable seed from the plate number.
        long seed = car.getPlateNumber().hashCode();

        // Check if tinted image was already cached.
        var cachedImage = CACHED_IMAGES.get(seed);

        if (cachedImage != null) {
            applyImageToAllLayers(cachedImage);
            return;
        }

        var deterministicRandom = new Random(seed);

        var baseTemplate = IMAGES.get(deterministicRandom.nextInt(IMAGES.size()));
        var tint = COLORS.get(deterministicRandom.nextInt(COLORS.size()));

        var tintedImage = generateTintedImage(baseTemplate, tint);

        // Cache the tinted image for later use.
        CACHED_IMAGES.put(seed, tintedImage);
        applyImageToAllLayers(tintedImage);
    }

    public void updateEffects(@NotNull Car car) {
        double speed = car.getSpeed();
        double topSpeed = car.calculateTopSpeed();

        // Shadow Logic (Reacts early to movement)

        var shadowAnim = JavaFXUtils.EasedValue.from(speed, 0.0, topSpeed, EASE_IN);

        double shadowScale = shadowAnim.map(1.0, 0.85);
        shadow.setScaleX(shadowScale);
        shadow.setScaleY(shadowScale);

        shadowOffsetFactor.set(shadowAnim.map(0.05, 0.10));
        shadow.setOpacity(shadowAnim.map(0.4, 0.2));
        shadowBlur.setRadius(shadowAnim.map(4.0, 16.0));

        // Trails Logic (Aggressive at high speed)

        double trailsStartSpeed = topSpeed * 0.25;
        double trailsFullOpacitySpeed = topSpeed * 0.75;

        var trailsAnim = JavaFXUtils.EasedValue.from(speed, trailsStartSpeed, topSpeed, EASE_OUT);
        var trailsOpacityAnim = JavaFXUtils.EasedValue.from(speed, trailsStartSpeed, trailsFullOpacitySpeed, EASE_IN_OUT);

        double trailsScale = trailsAnim.map(1.0, 1.15);
        trails.setScaleX(trailsScale);

        // Counter-shifts the trail to maintain front-edge alignment, as JavaFX scales from the center.
        trailsOffsetFactor.set((trailsScale - 1.0) * 0.5);

        trails.setOpacity(trailsOpacityAnim.map(0.0, 0.7));
        trailsBlur.setRadius(trailsAnim.map(0.0, 32.0));
    }

    public void updateTranslation(@NotNull Car car) {
        body.setTranslateX(car.getPositionX() - FIT_WIDTH * 0.5);
        body.setTranslateY(car.getPositionY() - FIT_HEIGHT * 0.5);
    }

    public void updateRotation(@NotNull Car car) {
        body.setRotate(car.getAngle());
    }

    public void updateAllVisuals(@NotNull Car car) {
        updateImage(car);
        updateEffects(car);
        updateTranslation(car);
        updateRotation(car);
    }
    // endregion
}
