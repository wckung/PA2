package models;

import controllers.ResourceLoader;
import org.jetbrains.annotations.NotNull;

/**
 * Singleton class housing all configuration options.
 */
public class Config {

    private Config() {
    }

    /**
     * Width of a normal pane.
     */
    public static final int WIDTH = 800;
    /**
     * Height of a normal pane.
     */
    public static final int HEIGHT = 600;

    /**
     * Height of a cell in a list.
     */
    public static final int LIST_CELL_HEIGHT = 30;

    /**
     * Width/Height of a tile.
     */
    public static final int TILE_SIZE = 32;

    /**
     * Path to the CSS styling.
     */
    public static final String CSS_STYLES_PATH = ResourceLoader.getResource("assets/styles/styles.css");

    /**
     * @return About text of this game.
     */
    @NotNull
    public static String getAboutText() {
        return "Controls:\n" +
                "LMB: Place Pipe\n" +
                "u: Undo Move\n" +
                "s: Skip Pipe\n" +
                "\n" +
                "Instructions:\n" +
                "The objective of Pipes is to allow the water to flow from the source to the sink. This is done by placing and connecting pipes in the map.";
    }
}
