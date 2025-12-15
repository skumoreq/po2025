package simulator_gui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override public void start(Stage primaryStage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("PrimaryView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        primaryStage.initStyle(StageStyle.UTILITY);
        primaryStage.setMaximized(true);
        primaryStage.setResizable(false);
        primaryStage.setTitle("Symulator");

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
