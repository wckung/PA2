package models.map.cells;

import controllers.Renderer;
import controllers.ResourceLoader;
import javafx.scene.image.Image;
import models.map.Map;
import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.PipePatterns;

/**
 * Represents a wall in {@link Map}.
 */
public class Wall extends Cell {

    /**
     * Image representing a wall.
     */
    private static final Image IMAGE = new Image(ResourceLoader.getResource("assets/images/wall.png"));

    public Wall(@NotNull Coordinate coord) {
        super(coord);
    }

    @Override
    public char toSingleChar() {
        return PipePatterns.WALL;
    }

    @NotNull
    @Override
    public Renderer.CellImage getImageRep() {
        // TODO
        return new Renderer.CellImage(IMAGE, 0);
    }

    @NotNull
    @Override
    public String toSerializedRep() {
        return "W";
    }
}
