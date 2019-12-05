package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import models.map.cells.FillableCell;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

/**
 * Class encapsulating an undo stack.
 */
public class CellStack {

    @NotNull
    private final Stack<@NotNull FillableCell> cellStack = new Stack<>();
    private IntegerProperty undoCountProperty = new SimpleIntegerProperty(0);

    /**
     * Pushes a cell into the stack.
     *
     * @param cell Cell to push into the stack.
     */
    public void push(@NotNull final FillableCell cell) {
        cellStack.push(cell);
    }

    /**
     * Pops a cell from the stack.
     *
     * @return The last-pushed cell, or {@code null} if the stack is empty.
     */
    @Nullable
    public FillableCell pop() {
        if (cellStack.empty()) {
            return null;
        }

        undoCountProperty.set(undoCountProperty.get() + 1);
        return cellStack.pop();
    }

    @NotNull
    public IntegerProperty getUndoCountProperty() {
        return undoCountProperty;
    }

    /**
     * Displays the current undo count to {@link System#out}.
     */
    public void display() {
        System.out.println("Undo Count: " + undoCountProperty.get());
    }
}
