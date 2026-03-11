package ch.unibas.dmi.dbis.cs108.casono.client.ui.lobby;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Casinomainui extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Casinomainui.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
        stage.setTitle("Casono");
        
        stage.getIcons().add(new javafx.scene.image.Image(getClass().getResource("/ch/unibas/dmi/dbis/cs108/casono/client/ui/resources/logoinverted.png").toExternalForm()));
        stage.setScene(scene);
        stage.setFullScreen(true);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
