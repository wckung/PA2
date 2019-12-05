package models.map.cells;

import controllers.Renderer;
import controllers.ResourceLoader;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import util.Coordinate;
import util.Direction;
import util.PipePatterns;

/**
 * Represents a source or a sink {@link Cell}.
 */
public class TerminationCell extends Cell {

    /**
     * Image representing an unfilled termination cell.
     */
    private static final Image UNFILLED_IMG = new Image(ResourceLoader.getResource("assets/images/dest-unfilled.png"));
    /**
     * Image representing a filled termination cell.
     */
    private static final Image FILLED_IMG = new Image(ResourceLoader.getResource("assets/images/dest-filled.png"));

    private boolean isFilled = false;
    @NotNull
    public final Direction pointingTo;
    @NotNull
    public final Type type;

    public TerminationCell(Coordinate coord, @NotNull Direction d, @NotNull Type type) {
        super(coord);
        this.pointingTo = d;
        this.type = type;
    }

    /**
     * Sets this cell as filled.
     */
    public void setFilled() {
        isFilled = true;
    }

    public boolean isFilled() {
        return isFilled;
    }

    @Override
    public char toSingleChar() {
        if (isFilled) {
            switch (pointingTo) {
                case UP:
                    return PipePatterns.Filled.UP_ARROW;
                case DOWN:
                    return PipePatterns.Filled.DOWN_ARROW;
                case LEFT:
                    return PipePatterns.Filled.LEFT_ARROW;
                case RIGHT:
                    return PipePatterns.Filled.RIGHT_ARROW;
            }
        } else {
            switch (pointingTo) {
                case UP:
                    return PipePatterns.Unfilled.UP_ARROW;
                case DOWN:
                    return PipePatterns.Unfilled.DOWN_ARROW;
                case LEFT:
                    return PipePatterns.Unfilled.LEFT_ARROW;
                case RIGHT:
                    return PipePatterns.Unfilled.RIGHT_ARROW;
            }
        }

        throw new IllegalStateException("Invalid pointingTo value!");
    }

    @NotNull
    @Override
    public Renderer.CellImage getImageRep() {
        // TODO
        return null;
    }

    @NotNull
    @Override
    public String toSerializedRep() {
        switch (pointingTo) {
            case UP:
                return "^";
            case DOWN:
                return "v";
            case LEFT:
                return "<";
            case RIGHT:
                return ">";
            default:
                throw new IllegalStateException("Unknown pointingTo value");
        }
    }

    public enum Type {
        SOURCE, SINK
    }

    /**
     * Data class encapsulating the coordinate and direction of the {@link TerminationCell}.
     */
    public static class CreateInfo {

        public final Coordinate coord;
        public final Direction dir;

        public CreateInfo(@NotNull Coordinate coord, @NotNull Direction dir) {
            this.coord = coord;
            this.dir = dir;
        }
    }
}
