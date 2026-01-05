package com.github.skumoreq.simulator.gui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.skumoreq.simulator.Car;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Group;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.*;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.net.URL;
import java.util.*;

import static com.github.skumoreq.simulator.gui.JavaFxUtils.EasingMode.*;
import static com.github.skumoreq.simulator.gui.JavaFxUtils.remapEased;

public class CarIcon extends Group {

    // region > Constants

    private static final double FIT_WIDTH = 120.0;
    private static final double FIT_HEIGHT = FIT_WIDTH / 2.0;

    private static final List<Image> IMAGES = loadImages();
    private static final List<Color> TINTS = loadColors();

    private static final Map<Long, Image> CACHED_IMAGES = new HashMap<>();
    // endregion

    // region > Static Methods

    private static @NotNull List<Image> loadImages() {
        try {
            List<Image> loadedImages = new ArrayList<>();

            int fileNameIndex = 1;
            while (true) {
                String fileName = String.format("car_%02d.png", fileNameIndex);
                URL url = SimulatorApp.class.getResource("images/" + fileName);

                if (url == null) break;

                loadedImages.add(new Image(url.toExternalForm()));
                fileNameIndex++;
            }

            if (loadedImages.isEmpty())
                throw new RuntimeException(
                        "No car images found in 'images/' folder. " +
                        "Expected files: car_01.png, car_02.png, etc."
                );

            return loadedImages;
        }
        catch (Exception exception) {
            throw new RuntimeException(
                    "Failed to load car images from 'images/' folder. " +
                    "Make sure the the resources directory contains car_XX.png files.",
                    exception
            );
        }
    }
    private static @NotNull List<Color> loadColors() {
        try {
            List<Color> loadedColors = new ArrayList<>();
            JsonNode colorsData = new ObjectMapper()
                    .readTree(SimulatorApp.class.getResourceAsStream("data/vehicle-colors.json"));

            for (JsonNode colorEntry : colorsData) {
                String hexCode = colorEntry.get("Hex (Web RGB)").asText();
                loadedColors.add(Color.web(hexCode));
            }

            return loadedColors;
        }
        catch (Exception exception) {
            throw new RuntimeException(
                    "Failed to load colors from 'data/vehicle-colors.json'. " +
                    "Make sure the file exists in the resources folder and has valid JSON format.",
                    exception
            );
        }
    }
    // endregion

    // region > Instance Fields

    private final ImageView body = new ImageView();
    private final ImageView shadow = new ImageView();
    private final ImageView trails = new ImageView();

    private final List<ImageView> layers = List.of(shadow, trails, body);

    private final GaussianBlur shadowBlur = new GaussianBlur();
    private final MotionBlur trailsBlur = new MotionBlur();
    // endregion

    // region > Properties & Bindings

    private final DoubleProperty shadowOffsetFactor = new SimpleDoubleProperty();
    private final DoubleProperty trailsOffsetFactor = new SimpleDoubleProperty();

    private final DoubleBinding bodyRotateRadians = Bindings.createDoubleBinding(
            () -> Math.toRadians(body.getRotate()), body.rotateProperty());
    private final DoubleBinding shadowOffsetX = shadowOffsetFactor.multiply(FIT_WIDTH);
    private final DoubleBinding shadowOffsetY = shadowOffsetFactor.multiply(FIT_HEIGHT);
    private final DoubleBinding trailsOffset = trailsOffsetFactor.multiply(FIT_WIDTH);
    // endregion

    // region > Initialization

    public CarIcon() {
        setupBindings();
        setupEffects();

        this.getChildren().addAll(layers);
    }

    private void setupBindings() {
        for (ImageView layer : layers) {
            layer.setFitWidth(FIT_WIDTH);
            layer.setFitHeight(FIT_HEIGHT);

            if (layer == body) continue;

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
        ColorAdjust blackColorAdjust = new ColorAdjust();
        blackColorAdjust.setBrightness(-1.0);

        shadowBlur.setInput(blackColorAdjust);

        shadow.setEffect(shadowBlur);
        trails.setEffect(trailsBlur);
    }
    // endregion

    // region > Helper Methods

    private void applyImageToAllLayers(Image image) {
        for (ImageView layer : layers) layer.setImage(image);
    }

    private @NotNull Image generateTintedImage(@NotNull Image baseTemplate, Color tint) {
        int width = (int) baseTemplate.getWidth();
        int height = (int) baseTemplate.getHeight();

        WritableImage tintedImage = new WritableImage(width, height);

        PixelReader input = baseTemplate.getPixelReader();
        PixelWriter output = tintedImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Color pixel = input.getColor(x, y);

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

    // region > Rendering Methods

    public void updateImage(@NotNull Car car) {
        // Generate a stable seed from the plate number.
        long seed = car.getPlateNumber().hashCode();

        // Check if tinted image was already cached.
        if (CACHED_IMAGES.containsKey(seed)) {
            Image cachedImage = CACHED_IMAGES.get(seed);
            applyImageToAllLayers(cachedImage);
            return;
        }

        Random deterministicRandom = new Random(seed);

        Image baseTemplate = IMAGES.get(deterministicRandom.nextInt(IMAGES.size()));
        Color tint = TINTS.get(deterministicRandom.nextInt(TINTS.size()));

        Image tintedImage = generateTintedImage(baseTemplate, tint);

        CACHED_IMAGES.put(seed, tintedImage); // cache for later use
        applyImageToAllLayers(tintedImage);
    }
    public void updateEffects(@NotNull Car car) {
        double speed = car.getSpeed();
        double topSpeed = car.calculateTopSpeed();

        // Shadow Logic (Reacts early to movement)

        double shadowScale = remapEased(speed, 0.0, topSpeed, 1.0, 0.85, QUAD_OUT);
        shadow.setScaleX(shadowScale);
        shadow.setScaleY(shadowScale);

        shadowOffsetFactor.set(remapEased(speed, 0.0, topSpeed, 0.05, 0.10, QUAD_OUT));
        shadow.setOpacity(remapEased(speed, 0.0, topSpeed, 0.4, 0.2, QUAD_OUT));
        shadowBlur.setRadius(remapEased(speed, 0.0, topSpeed, 4.0, 16.0, QUAD_OUT));

        // Trails Logic (Aggressive at high speed)

        double trailsStartSpeed = topSpeed * 0.25;
        double trailsFullOpacitySpeed = topSpeed * 0.75;

        double trailsScale = remapEased(speed, trailsStartSpeed, topSpeed, 1.0, 1.15, QUAD_IN);
        trails.setScaleX(trailsScale);
        trailsOffsetFactor.set((trailsScale - 1.0) * 0.5); // keeps the front of the stretched trail aligned with the body

        trails.setOpacity(remapEased(speed, trailsStartSpeed, trailsFullOpacitySpeed, 0.0, 0.7, SMOOTHSTEP));
        trailsBlur.setRadius(remapEased(speed, trailsStartSpeed, topSpeed, 0.0, 32.0, QUAD_IN));
    }

    public void updateTranslation(@NotNull Car car) {
        body.setTranslateX(car.getPositionX() - FIT_WIDTH / 2.0);
        body.setTranslateY(car.getPositionY() - FIT_HEIGHT / 2.0);
    }
    public void updateRotation(@NotNull Car car) {
        body.setRotate(car.getAngle());
    }

    public void updateVisuals(@NotNull Car car) {
        updateImage(car);
        updateEffects(car);
        updateTranslation(car);
        updateRotation(car);
    }
    // endregion
}
