module simulator_gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires simulator;
    requires java.desktop;
    requires javafx.graphics;

    opens simulator_gui to javafx.fxml;
    exports simulator_gui;
}