package views;

import controllers.Renderer;
import io.Deserializer;
import io.GameProperties;
import io.Serializer;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import models.exceptions.InvalidMapException;
import models.map.Map;
import models.map.cells.Cell;
import models.map.cells.FillableCell;
import models.map.cells.TerminationCell;
import models.map.cells.Wall;
import models.map.cells.TerminationCell.Type;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import util.Coordinate;
import util.Direction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

import static models.Config.TILE_SIZE;

public class LevelEditorCanvas extends Canvas {

    private static final String MSG_MISSING_SOURCE = "Source tile is missing!";
    private static final String MSG_MISSING_SINK = "Sink tile is missing!";
    private static final String MSG_BAD_DIMS = "Map size must be at least 2x2!";
    private static final String MSG_BAD_DELAY = "Delay must be a positive value!";
    private static final String MSG_SOURCE_TO_WALL = "Source tile is blocked by a wall!";
    private static final String MSG_SINK_TO_WALL = "Sink tile is blocked by a wall!";

    private GameProperties gameProp;

    @Nullable
    private TerminationCell sourceCell;
    @Nullable
    private TerminationCell sinkCell;

    public LevelEditorCanvas(int rows, int cols, int delay) {
        super();

        resetMap(rows, cols, delay);
    }

    /**
     * Changes the attributes of this canvas.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param delay Amount of delay.
     */
    public void changeAttributes(int rows, int cols, int delay) {
        resetMap(rows, cols, delay);
        sourceCell = null;
        sinkCell = null;
    }

    /**
     * Resets the map with the given attributes.
     *
     * @param rows  Number of rows.
     * @param cols  Number of columns.
     * @param delay Amount of delay.
     */
    private void resetMap(int rows, int cols, int delay) {
        // TODO
    	
    	gameProp = new GameProperties(rows, cols);
    	setAmountOfDelay(delay);
    	this.setWidth(32 * cols);
    	this.setHeight(32 * rows);
    	
    	for (int i = 0; i < rows; ++i) {
    		for (int j = 0; j < cols; ++j) {
    			if (i == 0 || j == 0 || i == rows - 1 || j == cols - 1) {
    				gameProp.cells[i][j] = new Wall(new Coordinate(i, j));
    			} else {
    				gameProp.cells[i][j] = new FillableCell(new Coordinate(i, j));
    			}
    		}
    	}
    	renderCanvas();
    }

    /**
     * Renders the canvas.
     */
    private void renderCanvas() {
        Platform.runLater(() -> Renderer.renderMap(this, gameProp.cells));
    }

    /**
     * Sets a tile on the map.
     * <p>
     * Hint:
     * You may need to check/compute some attribute in order to create the new {@link Cell} object.
     *
     * @param sel Selected {@link CellSelection}.
     * @param x   X-coordinate relative to the canvas.
     * @param y   Y-coordinate relative to the canvas.
     */
    public void setTile(@NotNull CellSelection sel, double x, double y) {
        // TODO
    	if (sel == null)
    		return;
    	
    	int sel_col = (int) (x / 32);
    	int sel_row = (int) (y / 32);
    	
    	if ("Wall" == sel.toString()) {
    		gameProp.cells[sel_row][sel_col] = new Wall(new Coordinate(sel_row, sel_col));
    		if (sourceCell != null && sourceCell.coord.row == sel_row && sourceCell.coord.col == sel_col) {
    			sourceCell = null;
    		}
    		if (sinkCell != null && sinkCell.coord.row == sel_row && sinkCell.coord.col == sel_col) {
    			sinkCell = null;
    		}
    	} else if ("Cell" == sel.toString()) {
    		gameProp.cells[sel_row][sel_col] = new FillableCell(new Coordinate(sel_row, sel_col));
    		if (sourceCell != null && sourceCell.coord.row == sel_row && sourceCell.coord.col == sel_col) {
    			sourceCell = null;
    		}
    		if (sinkCell != null && sinkCell.coord.row == sel_row && sinkCell.coord.col == sel_col) {
    			sinkCell = null;
    		}
    	} else if ("Source/Sink" == sel.toString()) {
    		if (sel_row == 0 && sinkCell == null) {
    			sinkCell = new TerminationCell(new Coordinate(sel_row, sel_col),
    					Direction.UP, TerminationCell.Type.SINK);
    			gameProp.cells[sel_row][sel_col] = sinkCell;
    		} else if (sel_row == gameProp.rows - 1 && sinkCell == null) {
    			sinkCell = new TerminationCell(new Coordinate(sel_row, sel_col),
    					Direction.DOWN, TerminationCell.Type.SINK);
    			gameProp.cells[sel_row][sel_col] = sinkCell;
    		} else if (sel_col == 0 && sinkCell == null) {
    			sinkCell = new TerminationCell(new Coordinate(sel_row, sel_col),
    					Direction.LEFT, TerminationCell.Type.SINK);
    			gameProp.cells[sel_row][sel_col] = sinkCell;
    		} else if (sel_col == gameProp.cols - 1 && sinkCell == null) {
    			sinkCell = new TerminationCell(new Coordinate(sel_row, sel_col),
    					Direction.RIGHT, TerminationCell.Type.SINK);
    			gameProp.cells[sel_row][sel_col] = sinkCell;
    		}
    		else if (sourceCell == null) {
    			sourceCell = new TerminationCell(new Coordinate(sel_row, sel_col), 
    					Direction.UP, models.map.cells.TerminationCell.Type.SOURCE);    			
    			gameProp.cells[sel_row][sel_col] = sourceCell;
    		}    		
    	}
    	renderCanvas();
    }

    /**
     * Sets a tile on the map.
     * <p>
     * Hint:
     * You will need to make sure that there is only one source/sink cells in the map.
     *
     * @param cell The {@link Cell} object to set.
     */
    private void setTileByMapCoord(@NotNull Cell cell) {
        // TODO
    }

    /**
     * Toggles the rotation of the source tile clockwise.
     */
    public void toggleSourceTileRotation() {
        // TODO
    	if (sourceCell == null)
    		return;
    	
    	switch (sourceCell.pointingTo) {
		case UP:
			sourceCell = new TerminationCell(new Coordinate(sourceCell.coord.row, sourceCell.coord.col), 
					Direction.RIGHT, TerminationCell.Type.SOURCE);
			break;
		case RIGHT:
			sourceCell = new TerminationCell(new Coordinate(sourceCell.coord.row, sourceCell.coord.col), 
					Direction.DOWN, TerminationCell.Type.SOURCE);
			break;
		case DOWN:
			sourceCell = new TerminationCell(new Coordinate(sourceCell.coord.row, sourceCell.coord.col), 
					Direction.LEFT, TerminationCell.Type.SOURCE);
			break;
		case LEFT:
			sourceCell = new TerminationCell(new Coordinate(sourceCell.coord.row, sourceCell.coord.col), 
					Direction.UP, TerminationCell.Type.SOURCE);
			break;
		default:
			throw new IllegalArgumentException("Unexpected value: " + sourceCell.pointingTo);
		}
    	gameProp.cells[sourceCell.coord.row][sourceCell.coord.col] = sourceCell;
    	renderCanvas();
    }

    /**
     * Loads a map from a file.
     * <p>
     * Prompts the player if they want to discard the changes, displays the file chooser prompt, and loads the file.
     *
     * @return {@code true} if the file is loaded successfully.
     */
    public boolean loadFromFile() {
        // TODO
    	File file = getTargetLoadFile();
    	
    	if (file != null) {
    		return loadFromFile(file.toPath());
    	}
        return false;
    }

    /**
     * Prompts the user for the file to load.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     *
     * @return {@link File} to load, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetLoadFile() {
        // TODO
    	FileChooser fileChooser = new FileChooser();
    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Map files (*.map)", "*.map");
        fileChooser.getExtensionFilters().add(extFilter);
    	
    	File file = fileChooser.showOpenDialog(this.getScene().getWindow());
    	
    	return file;
    }

    /**
     * Loads the file from the given path and replaces the current {@link LevelEditorCanvas#gameProp}.
     * <p>
     * Hint:
     * You should handle any exceptions which arise from loading in this method.
     *
     * @param path Path to load the file from.
     * @return {@code true} if the file is loaded successfully, {@code false} otherwise.
     */
    private boolean loadFromFile(@NotNull Path path) {
        // TODO
    	try {
    		BufferedReader in = new BufferedReader(
    		           new InputStreamReader(new FileInputStream(path.toFile()), "UTF-8"));
    		
    		String str;
    		int rd_rows = 0;
    		int rd_cols = 0;
    		int rd_delay = 0;
    		while ((str = in.readLine()) != null) {
                if (str.equals("# rows")) {
                	rd_rows = Integer.parseInt(in.readLine());
                	System.out.print("row " + rd_rows);
                } else if (str.equals("# cols")) {
                	rd_cols = Integer.parseInt(in.readLine());
                	System.out.print("col " + rd_cols);
                } else if (str.equals("# delay before first flow")) {
                	rd_delay = Integer.parseInt(in.readLine());
                	System.out.print("delay " + rd_delay);
                } else if (str.equals("# map")) {
                	resetMap(rd_rows, rd_cols, rd_delay);
                	
                	for (int i = 0; i < rd_rows; ++i) {
                		str = in.readLine();
                		for (int j = 0; j < rd_cols; ++j) {
                			TerminationCell.Type type = null;
                			if (str.charAt(j) != 'W' && str.charAt(j) != '.') {
                				if (i == 0 || i == rd_rows -1 || j == 0 || j == rd_cols - 1) {
                					type = TerminationCell.Type.SINK;
                				} else {
                					type = TerminationCell.Type.SOURCE;
                				}
                			}
                			gameProp.cells[i][j] = Cell.fromChar(str.charAt(j), new Coordinate(i, j), type);
                			if (str.charAt(j) != 'W' && str.charAt(j) != '.') {
                				if (i == 0 || i == rd_rows -1 || j == 0 || j == rd_cols - 1) {
                					sinkCell = (@Nullable TerminationCell) gameProp.cells[i][j];
                				} else {
                					sourceCell = (@Nullable TerminationCell) gameProp.cells[i][j];
                				}
                			}
                		}
                	}
                }
            }
            in.close();    
            
            renderCanvas();
            
            return true;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return false;
    }

    /**
     * Checks the validity of the map, prompts the player for the target save directory, and saves the file.
     */
    public void saveToFile() {
        // TODO
    	Optional<String> map_valid;
    	if ((map_valid = checkValidity()) != null) {
    		System.out.println(map_valid);
    		return;
    	}
    	File file = getTargetSaveDirectory();
    	
    	if (file != null) {
    		exportToFile(file.toPath());
    	}
    }

    /**
     * Prompts the user for the directory and filename to save as.
     * <p>
     * Hint:
     * Use {@link FileChooser} and {@link FileChooser#setSelectedExtensionFilter(FileChooser.ExtensionFilter)}.
     *
     * @return {@link File} to save to, or {@code null} if the operation is canceled.
     */
    @Nullable
    private File getTargetSaveDirectory() {
        // TODO
    	FileChooser fileChooser = new FileChooser();
    	FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Map files (*.map)", "*.map");
        fileChooser.getExtensionFilters().add(extFilter);
    	
    	File file = fileChooser.showSaveDialog(this.getScene().getWindow());
    	
    	return file;
    }

    /**
     * Exports the current map to a file.
     * <p>
     * Hint:
     * You should handle any exceptions which arise from saving in this method.
     *
     * @param p Path to export to.
     */
    private void exportToFile(@NotNull Path p) {
        // TODO
    	try {
    		PrintWriter writer;
    		writer = new PrintWriter(p.toFile(), StandardCharsets.UTF_8);
    		
    		writer.println("# rows");
    		writer.println(gameProp.rows + "\n");
    		
    		writer.println("# cols");
    		writer.println(gameProp.cols + "\n");
    		
    		writer.println("# delay before first flow");
    		writer.println(gameProp.delay + "\n");
    		
    		writer.println("# map");
    		for (int row = 0; row < gameProp.cells.length; ++row) {
    			for (int col = 0; col < gameProp.cells[row].length; ++col) {
    				writer.print(gameProp.cells[row][col].toSerializedRep());
    			}
    			writer.println();
    		}
    		
    		writer.close();
    	} catch(IOException e) {
    		e.printStackTrace();
    	}
    }

    /**
     * Checks whether the current map and its properties are valid.
     * <p>
     * Hint:
     * You should check for the following conditions:
     * <ul>
     * <li>Source cell is present</li>
     * <li>Sink cell is present</li>
     * <li>Minimum map size is 2x2</li>
     * <li>Flow delay is at least 1</li>
     * <li>Source/Sink tiles are not blocked by walls</li>
     * </ul>
     *
     * @return {@link Optional} containing the error message, or an empty {@link Optional} if the map is valid.
     */
    private Optional<String> checkValidity() {
        // TODO
    	
    	if (sourceCell == null) {
    		return Optional.of("Source does't exist on the map.");
    	}
    	if (sinkCell == null) {
    		return Optional.of("Sink doesn't exist on the map.");    		
    	}
    	if (gameProp.rows < 2) {
    		return Optional.of("Less than 2 rows");
    	}
    	if (gameProp.cols < 2) {
    		return Optional.of("Less than 2 cols");
    	}
    	if (gameProp.delay < 1) {
    		return Optional.of("Flow delay must be at least 1");
    	}
    	
    	Map temp = new Map(gameProp.rows, gameProp.cols, gameProp.cells);
    	if (!temp.checkReachable()) {
    		return Optional.of("Source is blocked by wall");
    	}
		return null;    
    }

    public int getNumOfRows() {
        return gameProp.rows;
    }

    public int getNumOfCols() {
        return gameProp.cols;
    }

    public int getAmountOfDelay() {
        return gameProp.delay;
    }

    public void setAmountOfDelay(int delay) {
        gameProp.delay = delay;
    }

    public enum CellSelection {
        WALL("Wall"),
        CELL("Cell"),
        TERMINATION_CELL("Source/Sink");

        private String text;

        CellSelection(@NotNull String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
