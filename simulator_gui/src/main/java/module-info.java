module org.example.simulator_gui {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.simulator_gui to javafx.fxml;
    exports org.example.simulator_gui;
}