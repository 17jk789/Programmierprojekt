package ch.unibas.dmi.dbis.cs108.casono.client.ui;

import javafx.application.Application;

/** Launcher for the Casono intro animation. */
public class Launcher {

    /** Default constructor. */
    public Launcher() {
        // Default constructor
    }

    /**
     * Main entry point for launching the UI.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Application.launch(IntroVideoPlayer.class, args);
    }
}
