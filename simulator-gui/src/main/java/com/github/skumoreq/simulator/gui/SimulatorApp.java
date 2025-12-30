package com.github.skumoreq.simulator.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class SimulatorApp extends Application {

    @Override
    public void start(@NotNull Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SimulatorApp.class.getResource("Primary.fxml"));
        Scene primaryScene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Symulator");

        primaryStage.setOnCloseRequest(_ -> {
            Platform.exit(); // shut down internal thread and clean up
            System.exit(0); // ensures JVM terminates
        });

        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }
}
