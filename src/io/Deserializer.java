package io;

import models.FXGame;
import models.exceptions.InvalidMapException;
import models.map.Map;
import models.map.cells.Cell;
import models.map.cells.TerminationCell;
import models.pipes.Pipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import textgame.game.Game;
import util.Coordinate;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A deserializer for converting a map file into a {@link Game}.
 */
public class Deserializer {

    /**
     * Path to the map to deserialize.
     */
    @NotNull
    private Path path;

    public Deserializer(@NotNull final String path) throws FileNotFoundException {
        this(Paths.get(path));
    }

    public Deserializer(@NotNull final Path path) throws FileNotFoundException {
        if (!path.toFile().exists()) {
            throw new FileNotFoundException("Cannot find file to load!");
        }

        this.path = path;
    }

    /**
     * Parses the text file and returns an instance of {@link Game}.
     *
     * @return An instance of {@link Game}.
     * @throws InvalidMapException if the map cannot be parsed.
     */
    @Nullable
    public Game parseGame() {
        final var properties = parseGameFile();
        return new Game(properties.rows, properties.cols, properties.delay, properties.cells, properties.pipes);
    }

    /**
     * Parses the text file and returns an instance of {@link FXGame}.
     *
     * @return An instance of {@link FXGame}.
     * @throws InvalidMapException if the map cannot be parsed.
     */
    @Nullable
    public FXGame parseFXGame() {
        final var properties = parseGameFile();
        return new FXGame(properties.rows, properties.cols, properties.delay, properties.cells, properties.pipes);
    }

    /**
     * Parses a game file into a {@link GameProperties} object.
     *
     * @return An instance of {@link GameProperties}.
     * @throws InvalidMapException if the map cannot be parsed.
     */
    @NotNull
    public GameProperties parseGameFile() {
        try (var reader = new BufferedReader(new FileReader(path.toFile()))) {
            String line;

            var rows = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                rows = Integer.parseInt(line);
            } else {
                throw new InvalidMapException("Unexpected EOF when parsing number of rows");
            }

            var cols = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                cols = Integer.parseInt(line);
            } else {
                throw new InvalidMapException("Unexpected EOF when parsing number of columns");
            }

            var delay = 0;
            if ((line = getFirstNonEmptyLine(reader)) != null) {
                delay = Integer.parseInt(line);
            } else {
                throw new InvalidMapException("Unexpected EOF when parsing amount of delay");
            }

            final var mapRep = new ArrayList<String>();
            for (int r = 0; r < rows; ++r) {
                line = getFirstNonEmptyLine(reader);
                if (line == null) {
                    throw new InvalidMapException("Unexpected EOF when parsing row " + r + " of map");
                }

                mapRep.add(line);
            }
            final var cells = parseString(rows, cols, String.join("\n", mapRep));

            List<Pipe> defaultPipes = null;
            String s = getFirstNonEmptyLine(reader);
            if (s != null) {
                defaultPipes = Arrays.stream(s.split(","))
                        .map(Pipe::fromString)
                        .collect(Collectors.toList());
            }

            return new GameProperties(rows, cols, cells, delay, defaultPipes);
        } catch (IOException ioe) {
            throw new InvalidMapException(ioe);
        }
    }

    /**
     * Deserializes a map from a {@link String}.
     *
     * @param rows     Rows of the given map.
     * @param cols     Columns of the given map.
     * @param cellsRep String representation of the map, with rows delimited by {@code '\n'}.
     * @return A 2D cell array from the string. Note that this cell array may not fully conform to the requirements of
     * an actual game map; The "map conformance" checks are performed in the {@link Map} constructor.
     */
    @NotNull
    public static Cell[][] parseString(final int rows, final int cols, @NotNull final String cellsRep) {
        var cells = new Cell[rows][cols];
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                var coord = new Coordinate(r, c);
                var ch = cellsRep.lines().skip(r).findFirst().orElseThrow().charAt(c);

                Cell cell;
                if (r == 0 || r == rows - 1 || c == 0 || c == cols - 1) {
                    cell = Cell.fromChar(ch, coord, TerminationCell.Type.SINK);
                } else {
                    cell = Cell.fromChar(ch, coord, TerminationCell.Type.SOURCE);
                }

                cells[r][c] = cell;
            }
        }

        return cells;
    }

    /**
     * Returns the first non-empty and non-comment line from the reader.
     *
     * @param br {@link BufferedReader} to read from.
     * @return First line that is a parseable line, or {@code null} there are no lines to read.
     * @throws IOException if the reader fails to read a line.
     */
    @Nullable
    private String getFirstNonEmptyLine(@NotNull final BufferedReader br) throws IOException {
        do {

            String s = br.readLine();

            if (s == null) {
                return null;
            }
            if (s.isBlank() || s.startsWith("#")) {
                continue;
            }

            return s;
        } while (true);
    }
}
