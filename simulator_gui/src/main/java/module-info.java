module simulator_gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires simulator;

    opens simulator_gui to javafx.fxml;
    exports simulator_gui;
}