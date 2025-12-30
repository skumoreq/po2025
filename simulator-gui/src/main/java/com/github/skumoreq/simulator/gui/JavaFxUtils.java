package com.github.skumoreq.simulator.gui;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class JavaFxUtils {

    private JavaFxUtils() {} // prevents instantiation

    private static final String STYLESHEET = Objects.requireNonNull(
            SimulatorApp.class.getResource("stylesheets/styles.css")).toExternalForm();

    // region > Alert Utility

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

    // region > TitledPane Utility

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

    // region > ComboBox Utility

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

    // region > Button Utility

    private static void setShown(boolean shown, @NotNull Button button) {
        button.setVisible(shown);
        button.setManaged(shown);
    }

    public static void show(@NotNull Button button) {
        setShown(true, button);
    }
    public static void hide(@NotNull Button button) {
        setShown(false, button);
    }
    // endregion

    // region > TextField Utility

    public static @NotNull String getTrimmedText(@NotNull TextField textField) {
        return textField.getText().trim();
    }
    public static boolean isEmpty(@NotNull TextField textField) {
        return textField.getText().trim().isEmpty();
    }

    public static void clear(TextField @NotNull ... textFields) {
        for (TextField textField: textFields) textField.clear();
    }
    // endregion
}
