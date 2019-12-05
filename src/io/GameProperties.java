package io;

import models.map.cells.Cell;
import models.pipes.Pipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Data class for the core properties of a game.
 */
public class GameProperties {
    public int rows;
    public int cols;
    public int delay;
    @NotNull
    public Cell[][] cells;
    @Nullable
    public List<Pipe> pipes;

    public GameProperties(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cell[rows][cols];
    }

    public GameProperties(int rows, int cols, @NotNull Cell[][] cells) {
        this.rows = rows;
        this.cols = cols;
        this.cells = cells;
    }

    public GameProperties(int rows, int cols, @NotNull Cell[][] cells, int delay) {
        this(rows, cols, cells);

        this.delay = delay;
    }

    public GameProperties(int rows, int cols, @NotNull Cell[][] cells, int delay, @Nullable List<Pipe> pipes) {
        this(rows, cols, cells, delay);

        this.pipes = pipes;
    }
}
