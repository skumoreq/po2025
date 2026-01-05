module com.github.skumoreq.simulator.gui {
    requires static org.jetbrains.annotations;

    requires com.github.skumoreq.simulator;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires jdk.jfr;
    requires com.fasterxml.jackson.databind;

    opens com.github.skumoreq.simulator.gui to javafx.fxml;
    exports com.github.skumoreq.simulator.gui;
}
