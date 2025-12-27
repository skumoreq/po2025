package com.github.skumoreq.simulator.gui;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class SimulatorApplication extends javafx.application.Application {
    @Override public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SimulatorApplication.class.getResource("PrimaryView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.setTitle("Symulator");
        primaryStage.setOnCloseRequest(_ -> {
            Platform.exit(); // shut down internal thread and clean up
            System.exit(0); // ensures JVM terminates
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
