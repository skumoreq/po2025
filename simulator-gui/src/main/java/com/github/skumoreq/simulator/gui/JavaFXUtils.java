package com.github.skumoreq.simulator.gui;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;

import java.net.URL;

public final class JavaFXUtils {

    private JavaFXUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // region ⮞ Static Data Initialization

    private static final String STYLESHEET_FILENAME = "styles.css";
    private static final String STYLESHEET;

    static {
        String path = "stylesheets/" + STYLESHEET_FILENAME;
        URL url = SimulatorApp.class.getResource(path);

        if (url == null)
            throw new RuntimeException("Resource file not found: " + path);

        STYLESHEET = url.toExternalForm();
    }
    // endregion

    // region ⮞ Animation Logic

    public enum EasingMode {
        EASE_IN {
            @Override
            public double apply(double t) {
                return t * t;
            }
        },
        EASE_OUT {
            @Override
            public double apply(double t) {
                return 1.0 - (t - 1.0) * (t - 1.0);
            }
        },
        EASE_IN_OUT {
            @Override
            public double apply(double t) {
                return t * t * (3.0 - 2.0 * t);
            }
        };

        public abstract double apply(double t);
    }

    public record EasedValue(double t) {
        public static @NotNull EasedValue from(
                double input,
                double minIn,
                double maxIn,
                @NotNull EasingMode easing
        ) {
            double t = (input - minIn) / (maxIn - minIn);
            return new EasedValue(easing.apply(Math.clamp(t, 0.0, 1.0)));
        }

        public double map(double minOut, double maxOut) {
            return minOut + (maxOut - minOut) * t;
        }
    }

    // endregion

    // region ⮞ Theme Settings

    public static void applyStyleTheme(@NotNull Parent root, boolean useDarkMode) {
        root.getStyleClass().removeAll("light-mode", "dark-mode");
        root.getStyleClass().add(useDarkMode ? "dark-mode" : "light-mode");
    }

    public static void applyStyleTheme(@NotNull Parent root, @NotNull Parent owner) {
        applyStyleTheme(root, owner.getStyleClass().contains("dark-mode"));
    }
    // endregion

    // region ⮞ Dialogs

    public record AlertInfo(Alert.AlertType type, String header, String content) {}

    public static void showAlertAndWait(@NotNull Parent owner, @NotNull AlertInfo info) {
        owner.getStyleClass().add("dimmed");

        Alert alert = new Alert(info.type());
        DialogPane dialogPane = alert.getDialogPane();

        if (owner.getScene() != null && owner.getScene().getWindow() != null)
            alert.initOwner(owner.getScene().getWindow());

        alert.initStyle(StageStyle.UNDECORATED);
        dialogPane.getStylesheets().add(STYLESHEET);
        applyStyleTheme(dialogPane, owner);

        alert.setHeaderText(info.header());
        dialogPane.setContent(new Label(info.content()));

        alert.showAndWait();

        owner.getStyleClass().remove("dimmed");
    }
    // endregion

    // region ⮞ Nodes Visibility

    private static void setRenderedState(boolean rendered, Node @NotNull ... nodes) {
        for (Node node : nodes) {
            node.setVisible(rendered);
            node.setManaged(rendered);
        }
    }

    public static void show(Node @NotNull ... nodes) {
        setRenderedState(true, nodes);
    }

    public static void hide(Node @NotNull ... nodes) {
        setRenderedState(false, nodes);
    }
    // endregion

    // region ⮞ TitledPane

    private static void forceExpandedState(boolean expanded, TitledPane @NotNull ... titledPanes) {
        for (TitledPane titledPane : titledPanes) {
            titledPane.setCollapsible(true);
            titledPane.setExpanded(expanded);
            titledPane.setCollapsible(false);
        }
    }

    public static void forceExpand(TitledPane @NotNull ... titledPanes) {
        forceExpandedState(true, titledPanes);
    }

    public static void forceCollapse(TitledPane @NotNull ... titledPanes) {
        forceExpandedState(false, titledPanes);
    }
    // endregion

    // region ⮞ ComboBox

    public static <T> boolean isEmpty(@NotNull ComboBox<T> comboBox) {
        return comboBox.getSelectionModel().isEmpty();
    }

    public static <T> int getSelectedIndex(@NotNull ComboBox<T> comboBox) {
        return comboBox.getSelectionModel().getSelectedIndex();
    }

    public static <T> void select(@NotNull ComboBox<T> comboBox, @NotNull T obj) {
        comboBox.getSelectionModel().select(obj);
    }

    public static <T> void clearSelection(@NotNull ComboBox<T> comboBox) {
        comboBox.getSelectionModel().clearSelection();
    }
    // endregion

    // region ⮞ TextInput

    public static boolean isBlank(@NotNull TextInputControl textInputControl) {
        return textInputControl.getText().isBlank();
    }

    public static @NotNull String getTrimmedText(@NotNull TextInputControl textInputControl) {
        return textInputControl.getText().trim();
    }

    public static void clear(TextInputControl @NotNull ... textInputControls) {
        for (TextInputControl textInputControl : textInputControls) {
            textInputControl.clear();
        }
    }
    // endregion
}
