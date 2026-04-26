package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents.TaskbarController;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Smoke test for Taskbar UI. Ensures FXML loads and controller initializes without errors. */
class TaskbarControllerSmokeTest {

    private static boolean started = false;

    @BeforeAll
    static void initJavaFX() throws Exception {
        if (started) {
            return;
        }

        CountDownLatch latch = new CountDownLatch(1);

        Thread t =
                new Thread(
                        () -> {
                            try {
                                Platform.startup(
                                        () -> {
                                            Platform.setImplicitExit(false);
                                            latch.countDown();
                                        });
                            } catch (IllegalStateException ignored) {
                                latch.countDown();
                            }
                        });

        t.setDaemon(true);
        t.start();

        latch.await();
        started = true;
    }

    @Test
    void taskbarControllerIsWiredCorrectly() throws Exception {
        URL url = getClass().getResource("/ui-structure/gameuicomponents/Taskbar.fxml");
        assertNotNull(url);

        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        TaskbarController controller = loader.getController();

        assertNotNull(root);
        assertNotNull(controller);
    }
}
