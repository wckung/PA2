package views.panes;

import controllers.AudioManager;
import controllers.SceneManager;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import models.Config;
import models.FXGame;
import models.FlowTimer;
import org.jetbrains.annotations.NotNull;
import views.BigButton;
import views.BigVBox;
import views.NumberTextField;
import views.SideMenuVBox;

import java.util.Optional;

public class SettingsPane extends GamePane {

    @NotNull
    private static final String MSG_BAD_ROW_NUM = "Row number should be at least 2!";
    @NotNull
    private static final String MSG_BAD_COL_NUM = "Column number should be at least 2!";
    @NotNull
    private static final String MSG_BAD_DELAY_NUM = "Delay value should be a positive integer!";
    @NotNull
    private static final String MSG_BAD_FLOW_NUM = "Flow rate should be a positive integer!";

    @NotNull
    private final VBox leftContainer = new SideMenuVBox();
    @NotNull
    private final Button saveButton = new BigButton("Save");
    @NotNull
    private final Button returnButton = new BigButton("Return");
    @NotNull
    private final Button toggleSoundButton = new BigButton("Sound FX: Enabled");
    @NotNull
    private final Button toggleGameModeButton = new BigButton("Counting Down: Disabled");
    
    /**
     * Text field for modifying the number of rows for generated maps.
     *
     * @see FXGame#getDefaultRows()
     * @see FXGame#setDefaultRows(int)
     */
    @NotNull
    private final NumberTextField rowsField = new NumberTextField(String.valueOf(FXGame.getDefaultRows()));
    @NotNull
    private final BorderPane rowBox = new BorderPane(null, null, rowsField, null, new Label("Default Rows"));
    /**
     * Text field for modifying the number of columns for generated maps.
     *
     * @see FXGame#getDefaultCols()
     * @see FXGame#setDefaultCols(int)
     */
    @NotNull
    private final NumberTextField colsField = new NumberTextField(String.valueOf(FXGame.getDefaultCols()));
    @NotNull
    private final BorderPane colBox = new BorderPane(null, null, colsField, null, new Label("Default Columns"));
    /**
     * Text field for modifying the default flow delay for generated maps.
     *
     * @see FlowTimer#getDefaultDelay()
     * @see FlowTimer#setDefaultDelay(int)
     */
    @NotNull
    private final NumberTextField delayField = new NumberTextField(String.valueOf(FlowTimer.getDefaultDelay()));
    @NotNull
    private final BorderPane delayBox = new BorderPane(null, null, delayField, null, new Label("Default Delay"));
    /**
     * Text field for modifying the default flow rate.
     *
     * @see FlowTimer#getDefaultFlowDuration()
     * @see FlowTimer#setDefaultFlowDuration(int)
     */
    @NotNull
    private final NumberTextField flowField = new NumberTextField(String.valueOf(FlowTimer.getDefaultFlowDuration()));
    @NotNull
    private final BorderPane flowBox = new BorderPane(null, null, flowField, null, new Label("Flow Rate (s)"));
    @NotNull
    private final VBox centerContainer = new BigVBox();
    @NotNull
    private final TextArea infoText = new TextArea(Config.getAboutText());

    public SettingsPane() {
        connectComponents();
        styleComponents();
        setCallbacks();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connectComponents() {
        // TODO
    	
    	this.setLeft(leftContainer);
    	
    	leftContainer.getChildren().add(returnButton);
    	leftContainer.getChildren().add(saveButton);
    	leftContainer.getChildren().add(rowBox);
    	leftContainer.getChildren().add(colBox);
    	leftContainer.getChildren().add(delayBox);
    	leftContainer.getChildren().add(flowBox);
    	leftContainer.getChildren().add(toggleSoundButton);
    	leftContainer.getChildren().add(toggleGameModeButton);
    	
    	this.setCenter(infoText);
    }

    /**
     * {@inheritDoc}
     *
     * In particular, the text box should be not editable, text should be wrapped around, "text-area" style should
     * be applied, and preferred height should be {@link Config#HEIGHT}.
     */
    @Override
    void styleComponents() {
        // TODO
    	infoText.setWrapText(true);
    	toggleSoundButton.setText(AudioManager.getInstance().isEnabled() ? "Sound FX : Enabled" : "Sound FX : Disabled");
    	toggleGameModeButton.setText(AudioManager.getInstance().isCountingDown() ? "Counting Down: Enabled" : "Counting Down: Disabled");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setCallbacks() {
        // TODO
    	
    	saveButton.setOnAction(e -> {
    		Optional<String> validated = validate();
    		
    		if (validated == null) {
    			returnToMainMenu(true);
    			return;
    		}
    		
    		Alert alert = new Alert(AlertType.WARNING);
    		alert.setTitle("Error");
    		alert.setHeaderText("Validation Failed");
    		
    		System.out.println(validated.get());
    		
    		if (validated.get() == "rowError") {    			
    			alert.setContentText("Row number should be at least 2!");
    		} else if (validated.get() == "colError") {
    			alert.setContentText("Column number should be at least 2!");
    		} else if (validated.get() == "delayError") {
    			alert.setContentText("Delay should be a positive integer!");
    		} else if (validated.get() == "flowError") {
    			alert.setContentText("Flow rate should be a positive integer!");
    		}
    		
    		Optional<ButtonType> result = alert.showAndWait();
    		if (result.get() == ButtonType.OK) {
    			returnToMainMenu(true);
    			return;
    		}
    	});
    	
    	returnButton.setOnAction(e -> {
    		returnToMainMenu(false);
    	});
    	
    	toggleSoundButton.setOnAction(e -> {
    		AudioManager.getInstance().setEnabled(!AudioManager.getInstance().isEnabled());
    		toggleSoundButton.setText(AudioManager.getInstance().isEnabled() ? "Sound FX : Enabled" : "Sound FX : Disabled");
    	});
    	
    	toggleGameModeButton.setOnAction(e -> {
    		AudioManager.getInstance().setCountingDown(!AudioManager.getInstance().isCountingDown());
    		toggleGameModeButton.setText(AudioManager.getInstance().isCountingDown() ? "Counting Down: Enabled" : "Counting Down: Disabled");
    	});
    }

    /**
     * Fill in the default values for all editable fields.
     */
    private void fillValues() {
        // TODO
    	rowsField.setText("" + FXGame.getDefaultRows());
    	colsField.setText("" + FXGame.getDefaultCols());
    	delayField.setText("" + FlowTimer.getDefaultDelay());
    	flowField.setText("" + FlowTimer.getDefaultFlowDuration());
    }

    /**
     * Switches back to the {@link MainMenuPane}.
     *
     * @param writeback Whether to save the values present in the text fields to their respective classes.
     */
    private void returnToMainMenu(final boolean writeback) {
        // TODO
    	if (writeback) {
    		FlowTimer.setDefaultDelay(Integer.parseInt(delayField.getText()));
    		FlowTimer.setDefaultFlowDuration(Integer.parseInt(flowField.getText()));
    		FXGame.setDefaultRows(Integer.parseInt(rowsField.getText()));
    		FXGame.setDefaultCols(Integer.parseInt(colsField.getText()));
    	} else {
    		fillValues();
    	}
    	
    	SceneManager.getInstance().showPane(MainMenuPane.class);
    }

    /**
     * Validates on all the input {@link javafx.scene.control.TextField}.
     *
     * <p>
     * There are three things to check in this method:
     * <ul>
     * <li>Whether the value of {@link SettingsPane#delayField} is a positive integer</li>
     * <li>Whether the value of {@link SettingsPane#rowsField} is bigger than 1</li>
     * <li>Whether the value of {@link SettingsPane#colsField} is bigger than 1</li>
     * </ul>
     * </p>
     *
     * @return If validation failed, {@link Optional} containing the reason message; An empty {@link Optional}
     * otherwise.
     */
    @NotNull
    private Optional<String> validate() {
    	if (Integer.parseInt(rowsField.getText()) < 2) {
    		return Optional.of("rowError");
    	} else if (Integer.parseInt(colsField.getText()) < 2) {
    		return Optional.of("colError");
    	} else if (Integer.parseInt(delayField.getText()) < 1) {
    		return Optional.of("delayError");
    	} else if (Integer.parseInt(flowField.getText()) < 1) {
    		return Optional.of("flowError");
    	}
    	
        return null;
    }
}
