package ch.unibas.dmi.dbis.cs108.casono.client.ui;

import javafx.application.Application;

/**
 * Launcher for the Casono main UI.
 * <p>
 * Standardkonstruktor für die Anwendung.
 */
public class Launcher {

    /**
     * Standardkonstruktor.
     */
    public Launcher() {
        // Standardkonstruktor
    }
    /**
     * Main entry point for launching the UI.
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        Application.launch(ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui.Casinomainui.class, args);
    }
}
    