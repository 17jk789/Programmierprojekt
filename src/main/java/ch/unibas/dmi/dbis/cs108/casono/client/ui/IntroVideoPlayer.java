package ch.unibas.dmi.dbis.cs108.casono.client.ui;

import ch.unibas.dmi.dbis.cs108.casono.client.ui.lobbyui.Casinomainui;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Plays an intro video in fullscreen and then starts the main UI. */
public class IntroVideoPlayer extends Application {

    private static final Logger LOGGER = LogManager.getLogger(IntroVideoPlayer.class);

    private Stage videoStage;

    @Override
    public void start(Stage stage) {
        this.videoStage = stage;

        URL videoUrl = getClass().getResource("/images/placeholder-animation.mp4");

        if (videoUrl == null) {
            LOGGER.error("Video file not found!");
            Platform.exit();
            return;
        }

        Media media = new Media(videoUrl.toExternalForm());
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        MediaView mediaView = new MediaView(mediaPlayer);

        StackPane root = new StackPane(mediaView);

        Rectangle2D screenBounds = Screen.getPrimary().getBounds();

        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());

        // scale video automatically
        mediaView.setPreserveRatio(true);
        mediaView.setFitWidth(screenBounds.getWidth());
        mediaView.setFitHeight(screenBounds.getHeight());

        stage.initStyle(StageStyle.UNDECORATED);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.setScene(scene);
        stage.show();

        mediaPlayer.setOnEndOfMedia(this::onVideoEnd);
        mediaPlayer.play();
    }

    private void onVideoEnd() {
        videoStage.close();
        setSystemProperties();
        startMainUI();
    }

    private void setSystemProperties() {
        var params = getParameters().getRaw();
        if (params == null || params.isEmpty()) {
            return;
        }

        String arg = params.get(0);
        String[] parts = arg.split(":", 2);
        if (parts.length == 2) {
            System.setProperty("casono.server.host", parts[0]);
            System.setProperty("casono.server.port", parts[1]);
        }
    }

    private void startMainUI() {
        Stage mainStage = new Stage();
        try {
            new Casinomainui().start(mainStage);
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
        }
    }
}
