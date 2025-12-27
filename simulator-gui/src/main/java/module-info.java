module com.github.skumoreq.simulator.gui {
    requires com.github.skumoreq.simulator;
    requires javafx.controls;
    requires javafx.fxml;

    opens com.github.skumoreq.simulator.gui to javafx.fxml;
    exports com.github.skumoreq.simulator.gui;
}