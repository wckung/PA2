package models.map.cells;

import models.MapElement;
import models.map.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import util.Coordinate;
import util.Direction;

/**
 * Representation of a cell in the {@link Map}.
 */
public abstract class Cell implements MapElement {

    @NotNull
    public final Coordinate coord;

    Cell(@NotNull Coordinate coord) {
        this.coord = coord;
    }

    /**
     * Parses a {@link Cell} from a character.
     *
     * <p>
     * Refer to README for the list of characters to their corresponding map element. If the character does not
     * represent a {@link TerminationCell}, the {@code terminationType} parameter can be ignored.
     * </p>
     *
     * @param c               Character to parse.
     * @param coord           Coordinate of the newly created cell.
     * @param terminationType If the character is a termination cell, its type. Otherwise, this argument is ignored and
     *                        can be null.
     * @return A cell based on the given creation parameters, or null if the parameters cannot form a valid Cell.
     */
    @Nullable
    public static Cell fromChar(char c, @NotNull Coordinate coord, @Nullable TerminationCell.Type terminationType) {
        switch (c) {
            case 'W':
                return new Wall(coord);
            case '.':
                return new FillableCell(coord);
            case '^':
                if (terminationType != null) {
                    return new TerminationCell(coord, Direction.UP, terminationType);
                }
                break;
            case '>':
                if (terminationType != null) {
                    return new TerminationCell(coord, Direction.RIGHT, terminationType);
                }
                break;
            case '<':
                if (terminationType != null) {
                    return new TerminationCell(coord, Direction.LEFT, terminationType);
                }
                break;
            case 'v':
                if (terminationType != null) {
                    return new TerminationCell(coord, Direction.DOWN, terminationType);
                }
                break;
        }

        return null;
    }
}
