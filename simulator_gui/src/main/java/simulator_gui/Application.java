package simulator_gui;

import simulator.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Date;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Utworzenie obiektów
        Tournament tournament = new Tournament("Zawody1", new Date());

        // Załadowanie FXML
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);

        // Przekazanie obiektów do kontrolera
        Controller controller = fxmlLoader.getController();
        controller.setTournament(tournament);

        // Otworzenie okna
        stage.setTitle("Symulator");
        stage.setScene(scene);
        stage.show();
    }
}
