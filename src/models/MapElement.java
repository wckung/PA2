package models;

import controllers.Renderer;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a map element.
 */
public interface MapElement {

    /**
     * @return A character representing the map element in game.
     */
    char toSingleChar();

    /**
     * @return An {@link Renderer.CellImage} instance representing the image representation of the element.
     */
    @NotNull
    Renderer.CellImage getImageRep();

    /**
     * @return Serialized {@link String} representation of the element.
     */
    @NotNull
    String toSerializedRep();
}
