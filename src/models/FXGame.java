package models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.ChoiceDialog;
import models.map.Map;
import models.map.cells.Cell;
import models.map.cells.FillableCell;
import models.map.cells.TerminationCell;
import models.map.cells.Wall;
import models.map.cells.TerminationCell.Type;
import models.pipes.Pipe;
import models.pipes.Pipe.Shape;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import textgame.game.Game;
import util.Coordinate;
import util.Direction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * JavaFX version of {@link textgame.game.Game}.
 */
public class FXGame {

    /**
     * Default number of rows.
     */
    private static int defaultRows = 8;
    /**
     * Default number of columns.
     */
    private static int defaultCols = 8;

    @NotNull
    private final Map map;
    @NotNull
    private final PipeQueue pipeQueue;
    @NotNull
    private final FlowTimer flowTimer;
    @NotNull
    private final CellStack cellStack = new CellStack();

    private IntegerProperty numOfSteps = new SimpleIntegerProperty(0);
    
    private int distance;
    
    private boolean isReplaced = false;

    /**
     * Sets the default number of rows for generated maps.
     *
     * @param rows New default number of rows.
     */
    public static void setDefaultRows(int rows) {
        defaultRows = rows;
    }

    /**
     * Sets the default number of column for generated maps.
     *
     * @param cols New default number of columns.
     */
    public static void setDefaultCols(int cols) {
        defaultCols = cols;
    }

    /**
     * @return Current default number of rows for generated maps.
     */
    public static int getDefaultRows() {
        return defaultRows;
    }

    /**
     * @return Current default number of columns for generated maps.
     */
    public static int getDefaultCols() {
        return defaultCols;
    }

    /**
     * Constructs an instance with default number of rows and columns.
     */
    public FXGame() {
        this(defaultRows, defaultCols);
    }

    /**
     * Constructs an instance with given number of rows and columns.
     *
     * @param rows Number of rows (excluding side walls)
     * @param cols Number of columns (excluding side walls)
     */
    private FXGame(int rows, int cols) {
        // TODO
    	rows += 2;
    	cols += 2;
    	Cell[][] cells = new Cell[rows][cols];
    	for (int i = 0; i < rows; ++i) {
    		for (int j = 0; j < cols; ++j) {
    			if (i == 0 || j == 0 || i == rows - 1 || j == cols - 1) {
    				cells[i][j] = new Wall(new Coordinate(i, j));
    			} else {
    				cells[i][j] = new FillableCell(new Coordinate(i, j));
    			}
    		}
    	}
    	
    	Map map_temp;
    	int source_row = -1;
    	int source_col = -1;
    	int sink_row = -1;
    	int sink_col = -1;
    	
    	do {
    		if (source_row != -1 && source_col != -1) {
    			cells[source_row][source_col] = new FillableCell(new Coordinate(source_row, source_col));
    		}
    		source_row = (int) (Math.random() * (rows - 2) + 1);
        	source_col = (int) (Math.random() * (cols - 2) + 1);
        	int source_direction = (int) (Math.random() * 4);
        	Direction source_dir = Direction.UP;
        	
        	switch (source_direction) {
        	case 0:
        		source_dir = Direction.UP;
        		break;
        	case 1:
        		source_dir = Direction.DOWN;
        		break;
        	case 2:
        		source_dir = Direction.LEFT;
        		break;
        	case 3:
        		source_dir = Direction.RIGHT;
        		break;
        	}
        	
        	cells[source_row][source_col] = new TerminationCell(new Coordinate(source_row, source_col), source_dir, Type.SOURCE);
        	
    		if (sink_row != -1 && sink_col != -1) {
    			cells[sink_row][sink_col] = new FillableCell(new Coordinate(sink_row, sink_col));
    		}
    		
        	int sink_direction = (int) (Math.random() * 4);
        	Direction sink_dir = Direction.UP;
        	
        	switch (sink_direction) {
        	case 0:
        		sink_dir = Direction.DOWN;
        		sink_row = rows - 1;
        		sink_col = (int) (Math.random() * (cols - 2) + 1);
        		break;
        	case 1:
        		sink_dir = Direction.UP;
        		sink_row = 0;
        		sink_col = (int) (Math.random() * (cols - 2) + 1);
        		break;
        	case 2:
        		sink_dir = Direction.LEFT;
        		sink_col = 0;
        		sink_row = (int) (Math.random() * (rows - 2) + 1);
        		break;
        	case 3:
        		sink_dir = Direction.RIGHT;
        		sink_col = cols - 1;
        		sink_row = (int) (Math.random() * (rows - 2) + 1);
        		break;
        	}
        	
        	cells[sink_row][sink_col] = new TerminationCell(new Coordinate(sink_row, sink_col), sink_dir, Type.SINK);
        	
    		map_temp = new Map(rows, cols, cells);
    	} while (!map_temp.checkReachable());
    	
    	map = map_temp;
    	   	    	
    	pipeQueue = new PipeQueue(null);
        flowTimer = new FlowTimer(FlowTimer.getDefaultDelay());
    }

    /**
     * Constructs an instance with all given parameters.
     *
     * @param rows  Number of rows including side walls
     * @param cols  Number of columns including side walls
     * @param delay Delay in seconds before water starts flowing.
     * @param cells Initial map.
     * @param pipes Initial pipes, if provided.
     */
    public FXGame(int rows, int cols, int delay, @NotNull Cell[][] cells, @Nullable List<Pipe> pipes) {
        // TODO
        map = (cells != null ? new Map(rows, cols, cells) : null);
        pipeQueue = new PipeQueue(pipes);
        flowTimer = new FlowTimer(delay);
    }

    /**
     * Adds a handler to be run when the water flows into an additional tile.
     *
     * @param handler {@link Runnable} to execute.
     */
    public void addOnFlowHandler(@NotNull Runnable handler) {
        flowTimer.registerFlowCallback(handler);
    }

    /**
     * Adds a handler to be run when a tick elapses.
     *
     * @param handler {@link Runnable} to execute.
     */
    public void addOnTickHandler(@NotNull Runnable handler) {
        flowTimer.registerTickCallback(handler);
    }

    /**
     * Starts the flow of water.
     */
    public void startCountdown() {
        flowTimer.start();
        distance = 0;
        
    	addOnFlowHandler(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				map.fillTiles(distance);
				distance = distance + 1;
			}
		});
    }
    
    public boolean isPaused() {
    	return flowTimer.isPaused();
    }
    
    public void pauseGame() {
		flowTimer.pauseTimer();
    }
    
    public void resumeGame() {
    	flowTimer.resumeTimer();
    }

    /**
     * Stops the flow of water.
     */
    public void stopCountdown() {
        flowTimer.stop();
    }

    /**
     * @param row Row index to place pipe
     * @param col Column index to place pipe
     * @see Game#placePipe(int, char)
     */
    public void placePipe(int row, int col) {
        // TODO
    	Pipe first_pipe = pipeQueue.peek();
    	if (!isReplaced) {
    		if (map.canReplacePipe(new Coordinate(row, col))) {
    			// show dialog and get new pipe
    			// need to pause timer

    			boolean isConfirmed = true;
    			Pipe new_pipe = new Pipe(Shape.CROSS);
    			
    			if (isConfirmed) {
    				map.replacePipe(new Coordinate(row, col), new_pipe);
        			
        			isReplaced = true;
    			}
    		}
    	}
    	if (map.tryPlacePipe(new Coordinate(row, col), first_pipe)) {
    		pipeQueue.consume();
    		numOfSteps.set(numOfSteps.get() + 1);
    		cellStack.push(new FillableCell(new Coordinate(row, col), first_pipe));
    	}
    }

    /**
     * @see Game#skipPipe()
     */
    public void skipPipe() {
        // TODO
    	pipeQueue.consume();
    }

    /**
     * @see Game#undoStep()
     */
    public void undoStep() {
        // TODO
    	FillableCell undo_cell = cellStack.pop();
    	
    	if (undo_cell == null || undo_cell.getPipe().get().getFilled()) {
    		return;
    	}
    	
    	map.undo(undo_cell.coord);
    	pipeQueue.undo(undo_cell.getPipe().get());    	
    }

    /**
     * Renders the map onto a {@link Canvas}.
     *
     * @param canvas {@link Canvas} to render to.
     */
    public void renderMap(@NotNull Canvas canvas) {
        map.render(canvas);
    }

    /**
     * Renders the queue onto a {@link Canvas}.
     *
     * @param canvas {@link Canvas} to render to.
     */
    public void renderQueue(@NotNull Canvas canvas) {
        pipeQueue.render(canvas);
    }

    /**
     * @see Game#updateState()
     */
    public void updateState() {
        // TODO
    }

    /**
     * @see Game#updateState()
     */
    public boolean hasWon() {
        // TODO
        return map.checkPath();
    }

    /**
     * @see Game#hasLost()
     */
    public boolean hasLost() {
        // TODO
        return map.hasLost();
    }

    /**
     * Fills all reachable pipes in the map.
     */
    public void fillAllPipes() {
        map.fillAll();
    }

    public IntegerProperty getNumOfSteps() {
        return numOfSteps;
    }

    public IntegerProperty getNumOfUndo() {
        return cellStack.getUndoCountProperty();
    }
    
}
