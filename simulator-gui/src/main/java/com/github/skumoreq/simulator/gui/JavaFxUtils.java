package com.github.skumoreq.simulator.gui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class JavaFxUtils {

    private JavaFxUtils() {} // prevents instantiation

    // region > Animation & Interpolation

    public enum EasingMode { QUAD_IN, QUAD_OUT, SMOOTHSTEP }

    public static double remapEased(double input, double minIn, double maxIn,
                                    double minOut, double maxOut, EasingMode mode) {
        if (input <= minIn) return minOut;
        if (input >= maxIn) return maxOut;

        double t = (input - minIn) / (maxIn - minIn);

        double easedT = switch (mode) {
            case QUAD_IN -> t * t;
            case QUAD_OUT -> 1.0 - (t - 1.0) * (t - 1.0);
            case SMOOTHSTEP -> t * t * (3.0 - 2.0 * t);
        };

        return minOut + (maxOut - minOut) * easedT;
    }
    // endregion

    // region > Alert

    private static final String STYLESHEET = Objects.requireNonNull(
            SimulatorApp.class.getResource("stylesheets/styles.css")).toExternalForm();

    public static void showAlertAndWait(Alert.AlertType alertType, @NotNull Parent root, String header, String content) {
        root.setOpacity(0.5);

        Alert alert = new Alert(alertType);

        alert.initStyle(StageStyle.UNDECORATED);
        alert.getDialogPane().getStylesheets().add(STYLESHEET);

        alert.setHeaderText(header);
        alert.setContentText(content);

        alert.showAndWait();

        root.setOpacity(1.0);
    }
    // endregion

    // region > TitledPane

    private static void setExpanded(boolean expanded, TitledPane @NotNull ... titledPanes) {
        for (TitledPane titledPane: titledPanes) {
            titledPane.setCollapsible(true);
            titledPane.setExpanded(expanded);
            titledPane.setCollapsible(false);
        }
    }

    public static void expand(TitledPane @NotNull ... titledPanes) {
        setExpanded(true, titledPanes);
    }
    public static void collapse(TitledPane @NotNull ... titledPanes) {
        setExpanded(false, titledPanes);
    }
    // endregion

    // region > ComboBox

    public static <T> int getSelectedIndex(@NotNull ComboBox<T> comboBox) {
        return comboBox.getSelectionModel().getSelectedIndex();
    }
    public static <T> boolean isEmpty(@NotNull ComboBox<T> comboBox) {
        return comboBox.getSelectionModel().isEmpty();
    }

    public static <T> void select(@NotNull ComboBox<T> comboBox, int index) {
        comboBox.getSelectionModel().select(index);
    }
    public static <T> void clearSelection(@NotNull ComboBox<T> comboBox) {
        comboBox.getSelectionModel().clearSelection();
    }
    // endregion

    // region > Node

    private static void setShown(boolean shown, Node @NotNull ... nodes) {
        for (Node node : nodes) {
            node.setVisible(shown);
            node.setManaged(shown);
        }
    }

    public static void show(Node @NotNull ... nodes) {
        setShown(true, nodes);
    }
    public static void hide(Node @NotNull ... nodes) {
        setShown(false, nodes);
    }
    // endregion

    // region > TextInputControl

    public static @NotNull String getTrimmedText(@NotNull TextInputControl textInputControl) {
        return textInputControl.getText().trim();
    }
    public static boolean isEmpty(@NotNull TextInputControl textInputControl) {
        return textInputControl.getText().trim().isEmpty();
    }

    public static void clear(TextInputControl @NotNull ... textInputControls) {
        for (TextInputControl textInputControl : textInputControls) textInputControl.clear();
    }
    // endregion
}
