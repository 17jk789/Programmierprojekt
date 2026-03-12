package ch.unibas.dmi.dbis.cs108.casono.client.ui;

import javafx.application.Application;

/**
 * Launcher-Klasse für die Casino-Anwendung.
 *
 * Startet die JavaFX-Anwendung. Während der Entwicklung kann hier direkt
 * `Casinogameui` für Tests aufgerufen werden. Im fertigen Spiel erfolgt
 * der Aufruf von `Casinogameui` über die `Casinomainui`.
 *
 * Aufgaben:
 * - Dient als Einstiegspunkt für die JVM.
 */
public class Launcher {
    public static void main(String[] args) {
        // Application.launch(ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui.Casinomainui.class, args);
        Application.launch(ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui.Casinogameui.class, args);
    }
}
    