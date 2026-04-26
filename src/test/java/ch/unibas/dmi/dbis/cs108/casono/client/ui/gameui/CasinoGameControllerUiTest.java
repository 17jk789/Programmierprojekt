package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents.TaskbarController;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/** Smoke test for Taskbar UI. Ensures FXML loads and controller initializes without errors. */
class TaskbarControllerSmokeTest {

    private static boolean fxStarted = false;

    /** Initializes JavaFX once before all tests. */
    @BeforeAll
    static void startJavaFX() throws Exception {
        if (!fxStarted) {
            CountDownLatch latch = new CountDownLatch(1);

            try {
                Platform.startup(
                        () -> {
                            Platform.setImplicitExit(false);
                            latch.countDown();
                        });
            } catch (IllegalStateException ignored) {
                latch.countDown();
            }

            assertTrue(latch.await(10, TimeUnit.SECONDS), "JavaFX did not start");
            fxStarted = true;
        }
    }

    /** Ensures FXML loads and controller initializes without errors. */
    @Test
    void taskbarLoadsWithoutErrors() throws Exception {
        URL url = getClass().getResource("/ui-structure/gameuicomponents/Taskbar.fxml");
        assertNotNull(url);

        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        TaskbarController controller = loader.getController();

        assertNotNull(root);
        assertNotNull(controller);
    }
}
