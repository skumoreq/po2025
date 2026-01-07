module com.github.skumoreq.simulator {
    requires static org.jetbrains.annotations;

    requires javafx.base;     // for ObservableList
    requires javafx.graphics; // for Platform.runLater

    requires tools.jackson.databind;

    exports com.github.skumoreq.simulator;
    exports com.github.skumoreq.simulator.exception;
}
