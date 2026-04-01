package ch.unibas.dmi.dbis.cs108.casono.client.ui;

import ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui.Casinomainui;
import javafx.application.Application;

/**
 * Launcher for the Casono main UI.
 *
 * <p>Default constructor for the application.
 */
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
        Application.launch(Casinomainui.class, args);
    }
}
