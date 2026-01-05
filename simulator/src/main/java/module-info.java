module com.github.skumoreq.simulator {
    requires static org.jetbrains.annotations;

    requires javafx.base;     // For ObservableList
    requires javafx.graphics; // For Platform.runLater

    requires com.fasterxml.jackson.databind;

    exports com.github.skumoreq.simulator;
    exports com.github.skumoreq.simulator.exception;
}
