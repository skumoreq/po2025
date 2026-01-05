package com.github.skumoreq.simulator.gui;

import javafx.application.Application;

public class Launcher {

    static void main() {

        // Disable dirty region optimizations to prevent "ripping" and visual artifacts.
        // This forces a full-frame repaint, ensuring that overlapping transparent layers
        // (like shadows and motion blurs) stay perfectly synchronized during movement.
        System.setProperty("prism.dirtyopts", "false");

        Application.launch(SimulatorApp.class);
    }
}
