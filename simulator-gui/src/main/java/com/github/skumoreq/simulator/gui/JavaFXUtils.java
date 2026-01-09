package com.github.skumoreq.simulator.gui;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;

public final class JavaFXUtils {

    private JavaFXUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    // region ⮞ Static Data Initialization

    private static final String STYLESHEET_FILENAME = "styles.css";
    public static final @NotNull String STYLESHEET;

    static {
        var path = "stylesheets/" + STYLESHEET_FILENAME;
        var url = SimulatorApp.class.getResource(path);

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

    public static void applyStyleTheme(@NotNull Node node, boolean useDarkTheme) {
        node.getStyleClass().remove("dark-mode");

        if (useDarkTheme) node.getStyleClass().add("dark-mode");
    }

    public static void applyStyleTheme(@NotNull Node node, @NotNull Node owner) {
        applyStyleTheme(node, owner.getStyleClass().contains("dark-mode"));
    }
    // endregion

    // region ⮞ Dialogs

    public record AlertInfo(@NotNull Alert.AlertType type, @NotNull String header, @NotNull String content) {
        public @NotNull AlertInfo withContent(@NotNull String newContent) {
            return new AlertInfo(type, header, newContent);
        }
    }

    public static void showAlertAndWait(@NotNull Node owner, @NotNull AlertInfo info) {
        owner.getStyleClass().add("dimmed");

        var alert = new Alert(info.type());
        var dialogPane = alert.getDialogPane();

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
        for (var node : nodes) {
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
        for (var titledPane : titledPanes) {
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
        for (var textInputControl : textInputControls) {
            textInputControl.clear();
        }
    }
    // endregion
}
