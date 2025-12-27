package com.github.skumoreq.simulator.gui;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.StageStyle;

import java.net.URL;


public final class JavaFxUtils {
    private JavaFxUtils() {} // prevents instantiation

    public static void showWarningAlert(Parent root, String title, String message) {
        URL stylesheetUrl = SimulatorApplication.class.getResource("stylesheets/styles.css");
        if (stylesheetUrl == null) throw new IllegalStateException("Could not find 'styles.css' in SimulatorAplication path");

        root.setOpacity(0.5);

        Alert warning = new Alert(Alert.AlertType.WARNING);

        warning.initStyle(StageStyle.UNDECORATED);
        warning.getDialogPane().getStylesheets().add(stylesheetUrl.toExternalForm());

        warning.setHeaderText(title);
        warning.setContentText(message);

        warning.showAndWait();

        root.setOpacity(1.0);
    }

    // «««TitledPane Utility»»»
    public static void expand(TitledPane... titledPanes) {
        for (TitledPane titledPane: titledPanes) {
            titledPane.setCollapsible(true);
            titledPane.setExpanded(true);
            titledPane.setCollapsible(false);
        }
    }
    public static void collapse(TitledPane... titledPanes) {
        for (TitledPane titledPane: titledPanes) {
            titledPane.setCollapsible(true);
            titledPane.setExpanded(false);
            titledPane.setCollapsible(false);
        }
    }

    // «««ComboBox Utility»»»
    public static <T> int getSelectedIndex(ComboBox<T> comboBox) {
        return comboBox.getSelectionModel().getSelectedIndex();
    }
    public static <T> boolean isEmpty(ComboBox<T> comboBox) {
        return comboBox.getSelectionModel().isEmpty();
    }
    public static <T> void select(ComboBox<T> comboBox, int index) {
        comboBox.getSelectionModel().select(index);
    }
    public static <T> void clearSelection(ComboBox<T> comboBox) {
        comboBox.getSelectionModel().clearSelection();
    }

    // «««Button Utility»»»
    public static void show(Button button) {
        button.setVisible(true);
        button.setManaged(true);
    }
    public static void hide(Button button) {
        button.setVisible(false);
        button.setManaged(false);
    }

    // «««TextField Utility»»»
    public static String getTrimmedText(TextField textField) {
        if (textField.getText() == null) return "";
        return textField.getText().trim();
    }
    public static void clear(TextField... textFields) {
        for (TextField textField: textFields) {
            textField.clear();
        }
    }
}
