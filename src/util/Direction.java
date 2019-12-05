package util;

import models.map.cells.Cell;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a direction in reference to a {@link Cell}.
 */
public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    /**
     * @return The opposite direction of {@code this}.
     */
    @NotNull
    public Direction getOpposite() {
        switch (this) {
            case UP:
                return DOWN;
            case DOWN:
                return UP;
            case LEFT:
                return RIGHT;
            case RIGHT:
                return LEFT;
            default:
                throw new IllegalStateException("Unknown direction");
        }
    }

    /**
     * @return A unit coordinate offset as expressed by {@code this} coordinate.
     */
    @NotNull
    public Coordinate getOffset() {
        switch (this) {
            case UP:
                return new Coordinate(-1, 0);
            case DOWN:
                return new Coordinate(1, 0);
            case LEFT:
                return new Coordinate(0, -1);
            case RIGHT:
                return new Coordinate(0, 1);
            default:
                throw new IllegalStateException("Unknown direction");
        }
    }

    /**
     * @return {@code this} rotated clockwise by 90 degrees.
     */
    public Direction rotateCW() {
        switch (this) {
            case UP:
                return RIGHT;
            case RIGHT:
                return DOWN;
            case DOWN:
                return LEFT;
            case LEFT:
                return UP;
            default:
                throw new IllegalStateException("Unknown direction");
        }
    }
}
