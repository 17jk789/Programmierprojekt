package ch.unibas.dmi.dbis.cs108.casono.client.ui.gameui.gameuicomponents;

import java.util.function.Consumer;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controller for the settings display within the poker UI. Handles theme selection and drag
 * movement.
 */
public class SettingsController {

    private static final Logger LOGGER = LogManager.getLogger(SettingsController.class);

    @FXML private VBox settingsBox;
    @FXML private RadioButton themeStandard;
    @FXML private RadioButton themeBlackWhite;
    @FXML private RadioButton themeGlass;
    private boolean visible = true;

    private double xOffset;
    private double yOffset;

    private static final double SCALE_DRAG = 0.95;
    private static final double SCALE_NORMAL = 1.0;

    private String currentTheme = "standard";
    private Consumer<String> themeChangeListener;

    /** Initialize the settings controller, set up theme toggle group and listeners. */
    @FXML
    private void initialize() {
        setupThemeToggle();
    }

    /** Set up the theme toggle group and listeners for the theme selection radio buttons. */
    private void setupThemeToggle() {
        if (themeStandard == null || themeBlackWhite == null || themeGlass == null) {
            LOGGER.warn("Theme buttons not injected from FXML");
            return;
        }

        ToggleGroup group = new ToggleGroup();

        themeStandard.setToggleGroup(group);
        themeBlackWhite.setToggleGroup(group);
        themeGlass.setToggleGroup(group);

        themeStandard.setSelected(true);

        group.selectedToggleProperty()
                .addListener(
                        (obs, oldVal, newVal) -> {
                            if (newVal == themeStandard) {
                                setTheme("standard");
                            } else if (newVal == themeBlackWhite) {
                                setTheme("blackwhite");
                            } else if (newVal == themeGlass) {
                                setTheme("glass");
                            }
                        });
    }

    /**
     * Set the current theme and notify the listener of the change.
     *
     * @param theme The new theme to set (e.g., "standard", "blackwhite", "glass").
     */
    private void setTheme(String theme) {
        currentTheme = theme;

        if (themeChangeListener != null) {
            themeChangeListener.accept(theme);
        }

        LOGGER.info("Theme changed to: " + theme);
    }

    /**
     * Handle the mouse press event on the settings box to prepare for dragging.
     *
     * @param event The MouseEvent triggered when the user presses the mouse button on the settings
     *     box.
     */
    @FXML
    private void onSettingsPressed(MouseEvent event) {
        if (settingsBox == null) {
            return;
        }

        xOffset = event.getSceneX() - settingsBox.getLayoutX();
        yOffset = event.getSceneY() - settingsBox.getLayoutY();
    }

    /**
     * Handle the mouse drag event on the settings box to allow dragging it around the scene.
     *
     * @param event The MouseEvent triggered when the user drags the mouse while pressing on the
     *     settings box.
     */
    @FXML
    private void onSettingsDragged(MouseEvent event) {
        if (settingsBox == null || settingsBox.getScene() == null) {
            return;
        }

        settingsBox.setScaleX(SCALE_DRAG);
        settingsBox.setScaleY(SCALE_DRAG);

        double x = event.getSceneX() - xOffset;
        double y = event.getSceneY() - yOffset;

        double maxX = settingsBox.getScene().getWidth() - settingsBox.getWidth();
        double maxY = settingsBox.getScene().getHeight() - settingsBox.getHeight();

        settingsBox.setLayoutX(clamp(x, 0, maxX));
        settingsBox.setLayoutY(clamp(y, 0, maxY));
    }

    /** Close the settings box from the header minus button. */
    @FXML
    private void onToggleSettings() {
        if (settingsBox == null) {
            return;
        }

        visible = !visible;

        settingsBox.setVisible(visible);
        settingsBox.setManaged(visible);
    }

    /**
     * Handle the mouse release event on the settings box to reset its scale after dragging.
     *
     * @param event The MouseEvent triggered when the user releases the mouse button after dragging
     *     the settings box.
     */
    @FXML
    private void onSettingsReleased(MouseEvent event) {
        if (settingsBox == null || settingsBox.getScene() == null) {
            return;
        }

        settingsBox.setScaleX(SCALE_NORMAL);
        settingsBox.setScaleY(SCALE_NORMAL);
    }

    /**
     * Set a listener to be notified when the theme changes. The listener will receive the new theme
     * as a string.
     *
     * @param listener A Consumer that accepts a String representing the new theme (e.g.,
     *     "standard", "blackwhite", "glass").
     */
    public void setThemeChangeListener(Consumer<String> listener) {
        this.themeChangeListener = listener;
    }

    /**
     * Show the settings box by setting its visibility and managed properties to true.
     */
    public void show() {
        if (settingsBox == null) {
            return;
        }

        settingsBox.setVisible(true);
        settingsBox.setManaged(true);
    }

    /** Hide the settings box by setting its visibility and managed properties to false. */
    public void hide() {
        if (settingsBox == null) {
            return;
        }

        settingsBox.setVisible(false);
        settingsBox.setManaged(false);
    }

    /**
     * Utility method to clamp a value between a minimum and maximum range.
     *
     * @param value The value to clamp.
     * @param min The minimum allowed value.
     * @param max The maximum allowed value.
     * @return The clamped value, guaranteed to be between min and max.
     */
    private double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }
}
