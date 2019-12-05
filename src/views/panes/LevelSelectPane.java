package views.panes;

import controllers.LevelManager;
import controllers.Renderer;
import controllers.SceneManager;
import controllers.Renderer.CellImage;
import io.Deserializer;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import models.FXGame;
import models.map.cells.Cell;
import models.map.cells.TerminationCell;
import models.pipes.Pipe;
import util.Coordinate;
import views.BigButton;
import views.BigVBox;
import views.SideMenuVBox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import org.jetbrains.annotations.Nullable;

public class LevelSelectPane extends GamePane {

    private SideMenuVBox leftContainer = new SideMenuVBox();
    private BigButton returnButton = new BigButton("Return");
    private BigButton playButton = new BigButton("Play");
    private BigButton playRandom = new BigButton("Generate Map and Play");
    private BigButton chooseMapDirButton = new BigButton("Choose map directory");
    private ListView<String> levelsListView = new ListView<>(LevelManager.getInstance().getLevelNames());
    private BigVBox centerContainer = new BigVBox();
    private Canvas levelPreview = new Canvas();

    public LevelSelectPane() {
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
    	this.setLeft(leftContainer);
    	
    	leftContainer.getChildren().add(returnButton);
    	leftContainer.getChildren().add(chooseMapDirButton);
    	leftContainer.getChildren().add(levelsListView);
    	leftContainer.getChildren().add(playButton);
    	leftContainer.getChildren().add(playRandom);
    	
    	this.setCenter(levelPreview);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void styleComponents() {
        // TODO
    	
    	playButton.setDisable(true);
    	
    }
    
    public void resetLevel() {
    	String curLvName = LevelManager.getInstance().getCurrentLevelProperty().get();
    	
    	ObservableList<String> levelList = levelsListView.getItems();
    	Iterator<String> it = levelList.iterator();
    	while (it.hasNext()) {
    		if (it.next() == curLvName) {
    			// set Item here
    			// levelsListView.getSelectionModel().set
    		}
    	}
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
    	
    	levelsListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
    	    @Override
    	    public void changed(ObservableValue<? extends String> observable,
    	    		String oldValue, String newValue) {
    	    	onMapSelected(observable, oldValue, newValue);
    	    	playButton.setDisable(false);
    	    }
    	});
    	
    	playButton.setOnAction(e -> {
    		startGame(false);
    	});
    	
    	chooseMapDirButton.setOnAction(e -> {
    		promptUserForMapDirectory();
    	});
    	
    	returnButton.setOnAction(e -> {
    		SceneManager.getInstance().showPane(MainMenuPane.class);
    	});
    	
    	playRandom.setOnAction(e -> {
    		startGame(true);
    	});
    	
    }

    /**
     * Starts the game.
     *
     * <p>
     * This method should do everything that is required to initialize and start the game, including loading/generating
     * maps, switching scenes, etc.
     * </p>
     *
     * @param generateRandom Whether to use a generated map.
     */
    private void startGame(final boolean generateRandom) {    	
        // TODO    	
    	GameplayPane play = SceneManager.getInstance().getPane(GameplayPane.class);
    	
    	if (generateRandom) {
    		FXGame game = new FXGame();
    		play.startGame(game);
    		SceneManager.getInstance().showPane(GameplayPane.class);
    		LevelManager.getInstance().setLevel("");
    		return;
    	}
    	
    	LevelManager lvlManager = LevelManager.getInstance();
    	//lvlManager.
    	SceneManager.getInstance().showPane(GameplayPane.class);
    	
    	    
    	
    	int rd_rows = 0;
		int rd_cols = 0;
		int rd_delay = 0;
		GraphicsContext gc = levelPreview.getGraphicsContext2D();
		Cell[][] cells = null;
		List<Pipe> pipes = null;
    	try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(lvlManager.getCurrentLevelPath() + 
					"/" + lvlManager.getCurrentLevelProperty().get() + ".map"), "UTF-8"));
			
			String str;
			
			while ((str = in.readLine()) != null) {
	            if (str.equals("# rows")) {
	            	rd_rows = Integer.parseInt(in.readLine());
	            	levelPreview.setHeight(32 * rd_rows);
	            } else if (str.equals("# cols")) {
	            	rd_cols = Integer.parseInt(in.readLine());
	            	levelPreview.setWidth(32 * rd_cols);
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
    	play.startGame(game);
    }

    /**
     * Listener method that executes when a map on the list is selected.
     *
     * @param observable Observable value.
     * @param oldValue   Original value.
     * @param newValue   New value.
     */
    private void onMapSelected(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        // TODO
    	LevelManager.getInstance().setLevel(newValue);
    	
    	if (newValue == null || newValue == "") {
    		levelPreview.setWidth(0);
    		playButton.setDisable(true);
    		return;
    	}
    	
    	try {
			BufferedReader in = new BufferedReader(
			           new InputStreamReader(new FileInputStream(LevelManager.getInstance().getCurrentLevelPath() + "/" + newValue + ".map"), "UTF-8"));
			
			String str;
			int rd_rows = 0;
			int rd_cols = 0;
			int rd_delay = 0;
			GraphicsContext gc = levelPreview.getGraphicsContext2D();
			Cell[][] cells = null;
			
			while ((str = in.readLine()) != null) {
	            if (str.equals("# rows")) {
	            	rd_rows = Integer.parseInt(in.readLine());
	            	levelPreview.setHeight(32 * rd_rows);
	            } else if (str.equals("# cols")) {
	            	rd_cols = Integer.parseInt(in.readLine());
	            	levelPreview.setWidth(32 * rd_cols);
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
	            }
	        }
	        in.close();    
	        
	        Renderer.renderMap(levelPreview, cells);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * Prompts the user for a map directory.
     *
     * <p>
     * Hint:
     * Use {@link DirectoryChooser} to display a folder selection prompt.
     * </p>
     */
    private void promptUserForMapDirectory() {
        // TODO
    	DirectoryChooser directorChooser = new DirectoryChooser();
    	
    	File selectedDirectory = directorChooser.showDialog(getScene().getWindow());
    	if (selectedDirectory != null) {
    		commitMapDirectoryChange(selectedDirectory);
    	}
    }

    /**
     * Actually changes the current map directory.
     *
     * @param dir New directory to change to.
     */
    private void commitMapDirectoryChange(File dir) {
        // TODO
    	LevelManager.getInstance().setMapDirectory(dir.toPath());
    	levelsListView.setItems(LevelManager.getInstance().getLevelNames());
    }
}
