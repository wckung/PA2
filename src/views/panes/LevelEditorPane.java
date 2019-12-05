package views.panes;

import controllers.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import models.Config;
import models.FXGame;
import models.FlowTimer;
import views.*;
import views.LevelEditorCanvas.CellSelection;

import java.util.Arrays;

/**
 * Pane for the Level Editor.
 */
public class LevelEditorPane extends GamePane {

    private final LevelEditorCanvas levelEditor = new LevelEditorCanvas(FXGame.getDefaultRows(), FXGame.getDefaultCols(), FlowTimer.getDefaultDelay());
    private final VBox leftContainer = new SideMenuVBox();

    private final Button returnButton = new BigButton("Return");

    private Label rowText = new Label("Rows");
    private NumberTextField rowField = new NumberTextField(String.valueOf(levelEditor.getNumOfRows()));
    private BorderPane rowBox = new BorderPane(null, null, rowField, null, rowText);

    private Label colText = new Label("Columns");
    private NumberTextField colField = new NumberTextField(String.valueOf(levelEditor.getNumOfCols()));
    private BorderPane colBox = new BorderPane(null, null, colField, null, colText);

    private Button newGridButton = new BigButton("New Grid");

    private Label delayText = new Label("Delay");
    private NumberTextField delayField = new NumberTextField(String.valueOf(levelEditor.getAmountOfDelay()));
    private BorderPane delayBox = new BorderPane(null, null, delayField, null, delayText);

    private ObservableList<LevelEditorCanvas.CellSelection> cellList = FXCollections.observableList(Arrays.asList(LevelEditorCanvas.CellSelection.values()));
    private ListView<LevelEditorCanvas.CellSelection> selectedCell = new ListView<>();

    private Button toggleRotationButton = new BigButton("Toggle Source Rotation");
    private Button loadButton = new BigButton("Load");
    private Button saveButton = new BigButton("Save As");

    private VBox centerContainer = new BigVBox();

    public LevelEditorPane() {
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
    	leftContainer.getChildren().add(rowBox);
    	leftContainer.getChildren().add(colBox);
    	leftContainer.getChildren().add(newGridButton);
    	leftContainer.getChildren().add(delayBox);
    	leftContainer.getChildren().add(selectedCell);
    	selectedCell.getItems().addAll(cellList);
    	leftContainer.getChildren().add(toggleRotationButton);
    	leftContainer.getChildren().add(loadButton);
    	leftContainer.getChildren().add(saveButton);
    	
        this.setCenter(levelEditor);
    }

    /**
     * {@inheritDoc}
     *
     * {@link LevelEditorPane#selectedCell} should have cell heights of {@link Config#LIST_CELL_HEIGHT}.
     */
    @Override
    void styleComponents() {
        // TODO
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
    	
    	delayField.textProperty().addListener((observable, oldValue, newValue) -> {
    		levelEditor.setAmountOfDelay(Integer.parseInt(newValue));
    	});
    	
    	loadButton.setOnAction(event -> {
    		if (levelEditor.loadFromFile()) {
    			rowField.setText("" + levelEditor.getNumOfRows());
    			colField.setText("" + levelEditor.getNumOfCols());
    			delayField.setText("" + levelEditor.getAmountOfDelay());
    		}
    	});
    	
    	saveButton.setOnAction(event -> {
    		levelEditor.saveToFile();
    	});    	
    	
    	toggleRotationButton.setOnAction(event -> {
    		levelEditor.toggleSourceTileRotation();
    	});
    	
    	levelEditor.setOnMouseClicked(event -> {    		
    		levelEditor.setTile(selectedCell.getSelectionModel().getSelectedItem(), event.getX(), event.getY());
    	});
    	
    	newGridButton.setOnAction(new EventHandler<ActionEvent>() {
    		@Override public void handle(ActionEvent e) {
    			int rows_new = rowField.getValue();
    			int cols_new = colField.getValue();
    			levelEditor.changeAttributes(rows_new, cols_new, levelEditor.getAmountOfDelay());    			
    		}
    	});
    	
    	returnButton.setOnAction(new EventHandler<ActionEvent>() {
    		@Override public void handle(ActionEvent e) {
    			SceneManager.getInstance().showPane(MainMenuPane.class);
    		}
    	});
    }
}
