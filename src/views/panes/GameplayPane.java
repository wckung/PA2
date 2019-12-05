package views.panes;

import controllers.AudioManager;
import controllers.AudioManager.SoundRes;
import controllers.LevelManager;
import controllers.Renderer;
import controllers.ResourceLoader;
import controllers.SceneManager;
import io.Deserializer;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import models.FXGame;
import models.map.cells.Cell;
import models.map.cells.TerminationCell;
import models.pipes.Pipe;
import util.Coordinate;

import org.jetbrains.annotations.NotNull;

import views.BigButton;
import views.BigVBox;
import views.GameplayInfoPane;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static models.Config.TILE_SIZE;

/**
 * Pane for displaying the actual gameplay.
 */
public class GameplayPane extends GamePane {

    private HBox topBar = new HBox(20);
    private VBox canvasContainer = new BigVBox();
    private Canvas gameplayCanvas = new Canvas();
    private HBox bottomBar = new HBox(20);
    private Canvas queueCanvas = new Canvas();
    private Button quitToMenuButton = new BigButton("Quit to menu");
    private Button pauseButton = new BigButton("Pause");

    private FXGame game;

    private final IntegerProperty ticksElapsed = new SimpleIntegerProperty();
    private GameplayInfoPane infoPane = null;
    
    public GameplayPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    void connectComponents() {
        // TODO
//    	this.setTop(topBar);
    	    	
		game = new FXGame();    	
    	infoPane = new GameplayInfoPane(LevelManager.getInstance().getCurrentLevelProperty(), ticksElapsed, game.getNumOfSteps(), game.getNumOfUndo());
    	this.setTop(infoPane);
    	
    	this.setCenter(canvasContainer);    	
    	canvasContainer.getChildren().add(gameplayCanvas);
    	
    	this.setBottom(bottomBar);
    	queueCanvas.setWidth(256);
    	queueCanvas.setHeight(64);
    	bottomBar.getChildren().add(queueCanvas);
    	bottomBar.getChildren().add(quitToMenuButton);
    	bottomBar.getChildren().add(pauseButton);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // TODO
    	bottomBar.setAlignment(Pos.CENTER_LEFT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
    	
    	gameplayCanvas.setOnMouseClicked(e -> {
    		onCanvasClicked(e);
    	});
    	
    	quitToMenuButton.setOnAction(e -> {
    		doQuitToMenuAction();
    	});
    	
    	this.setOnKeyPressed(e -> {
    		onKeyPressed(e);
    	});
    	
    	pauseButton.setOnAction(e -> {
    		if (pauseButton.getText() == "Pause") {
    			pauseButton.setText("Resume");
    			game.pauseGame();
    		} else {
    			pauseButton.setText("Pause");
    			game.resumeGame();
    		}
    	});
    }

    /**
     * Handles events when somewhere on the {@link GameplayPane#gameplayCanvas} is clicked.
     *
     * @param event Event to handle.
     */
    private void onCanvasClicked(MouseEvent event) {
        // TODO
    	if (game.isPaused()) {
    		return;
    	}
    	
    	int sel_col = (int) (event.getX() / 32);
    	int sel_row = (int) (event.getY() / 32);
    	game.placePipe(sel_row, sel_col);
    	game.renderMap(gameplayCanvas);
    	game.renderQueue(queueCanvas);
    	AudioManager.getInstance().playSound(SoundRes.MOVE);
    	
    	if (game.hasWon()) {
    		game.fillAllPipes();
    		game.stopCountdown();
    		createWinPopup();
    		AudioManager.getInstance().playSound(SoundRes.WIN);
    	}
    }

    /**
     * Handles events when a key is pressed.
     *
     * @param event Event to handle.
     */
    private void onKeyPressed(KeyEvent event) {
        // TODO
    	
    	if (game.isPaused()) {
    		return;
    	}
    	
    	switch (event.getCode()) {
		case U:
			game.undoStep();
			game.renderMap(gameplayCanvas);
			game.renderQueue(queueCanvas);
			break;
		case S:
			game.skipPipe();
			game.renderQueue(queueCanvas);
			break;
		}
    }

    /**
     * Creates a popup which tells the player they have completed the map.
     */
    private void createWinPopup() {
        // TODO
    	ButtonType next_map = new ButtonType("Next Map", ButtonData.OK_DONE);
    	ButtonType ret = new ButtonType("Return", ButtonData.CANCEL_CLOSE);
    	
    	Alert alert = new Alert(AlertType.CONFIRMATION, "", next_map, ret);
    	alert.setTitle("Confirm");
    	alert.setHeaderText("Level Cleared");
    	

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get().getText() == "Next Map"){
    	    loadNextMap();
    	} else if (result.get().getText() == "Return"){
    	    doQuitToMenu();
    	}
    }

    /**
     * Loads the next map in the series, or generate a new map if one is not available.
     */
    private void loadNextMap() {
        // TODO
    	
    	if (LevelManager.getInstance().getCurrentLevelProperty().get() == "") {
    		FXGame game = new FXGame();
    		startGame(game);
    		return;
    	}
    	
    	String newLevel = LevelManager.getInstance().getAndSetNextLevel();
    	System.out.println(newLevel);
    	
    	int rd_rows = 0;
		int rd_cols = 0;
		int rd_delay = 0;
		Cell[][] cells = null;
		List<Pipe> pipes = null;
    	try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(LevelManager.getInstance().getCurrentLevelPath() + 
					"/" + newLevel + ".map"), "UTF-8"));
			
			String str;
			
			while ((str = in.readLine()) != null) {
	            if (str.equals("# rows")) {
	            	rd_rows = Integer.parseInt(in.readLine());
	            } else if (str.equals("# cols")) {
	            	rd_cols = Integer.parseInt(in.readLine());
	            } else if (str.equals("# delay before first flow")) {
	            	rd_delay = Integer.parseInt(in.readLine());                	
	            } else if (str.equals("# map")) {   
	            	cells = new Cell[rd_rows][rd_cols];
	            	
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
	            			cells[i][j] = Cell.fromChar(str.charAt(j), new Coordinate(i, j), type);
	            		}
	            	}
	            } else if (str.contains(",") && str.charAt(0) != '#') {
	            	String[] pipes_str = str.split(",");
	            	pipes = new ArrayList<Pipe>();
	            	for (String pipe_str : pipes_str) {
	            		pipes.add(Pipe.fromString(pipe_str.trim()));
	            	}
	            }
	        }
	        in.close();    
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	FXGame game = new FXGame(rd_rows, rd_cols, rd_delay, cells, pipes);
    	
    	startGame(game);
    }

    /**
     * Creates a popup which tells the player they have lost the map.
     */
    private void createLosePopup() {
        // TODO
    	
    	ButtonType ret = new ButtonType("Return", ButtonData.OK_DONE);
    	Alert alert = new Alert(AlertType.INFORMATION, "", ret);
    	alert.setTitle("Confir");
    	alert.setHeaderText("You Lose!");
    	
    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get().getText() == "Return") {
    		doQuitToMenu();
    	}
    }

    /**
     * Creates a popup which prompts the player whether they want to quit.
     */
    private void doQuitToMenuAction() {
        // TODO
    	Alert alert = new Alert(AlertType.CONFIRMATION);
    	alert.setTitle("Confirm");
    	alert.setHeaderText("Return to menu?");
    	alert.setContentText("Game progress will be lost.");
    	

    	Optional<ButtonType> result = alert.showAndWait();
    	if (result.get() == ButtonType.OK){
    		doQuitToMenu();
    	} 
    }

    /**
     * Go back to the Level Select scene.
     */
    private void doQuitToMenu() {
        // TODO
    	game.stopCountdown();
    	SceneManager.getInstance().showPane(LevelSelectPane.class);
    	
    	LevelSelectPane levelSelect = SceneManager.getInstance().getPane(LevelSelectPane.class);
    	levelSelect.resetLevel();
    }

    /**
     * Starts a new game with the given name.
     *
     * @param game New game to start.
     */
    private int b = 3;
    void startGame(@NotNull FXGame game) {
        // TODO
    	if (AudioManager.getInstance().isCountingDown()) {
    		ticksElapsed.set(30);
    	} else {
    		ticksElapsed.set(0);
    	}
    	
    	this.game = game;
    	infoPane.bindTo(LevelManager.getInstance().getCurrentLevelProperty(), ticksElapsed, game.getNumOfSteps(), game.getNumOfUndo());
    	
    	game.renderMap(gameplayCanvas);
    	game.renderQueue(queueCanvas);
    	
    	game.startCountdown();
    	
    	game.addOnTickHandler(new Runnable()  {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (AudioManager.getInstance().isCountingDown()) {
					if (ticksElapsed.get() <= 1) {
						game.stopCountdown();
	    				Platform.runLater(() -> createLosePopup());
	    				AudioManager.getInstance().playSound(SoundRes.LOSE);
					}
					Platform.runLater(() -> ticksElapsed.set(ticksElapsed.get() - 1));
				} else {
					Platform.runLater(() -> ticksElapsed.set(ticksElapsed.get() + 1));
				}
			}
		});
    	
    	game.addOnFlowHandler(new Runnable() {
    		
    		@Override
			public void run() {
    			game.renderMap(gameplayCanvas);
    			if (game.hasLost()) {
    				game.stopCountdown();
    				Platform.runLater(() -> createLosePopup());
    				AudioManager.getInstance().playSound(SoundRes.LOSE);
    			}
    		}
    	});
    }

    /**
     * Cleans up the currently bound game.
     */
    private void endGame() {
        // TODO
    	game.stopCountdown();   	
    	
    }
}
