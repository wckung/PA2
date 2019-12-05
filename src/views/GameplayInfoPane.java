package views;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

/**
 * Displays info about the current level being played by the user.
 */
public class GameplayInfoPane extends BigVBox {
    private final Label levelNameLabel = new Label();
    private final Label timerLabel = new Label();
    private final Label numMovesLabel = new Label();
    private final Label numUndoLabel = new Label();

    public GameplayInfoPane(StringProperty levelNameProperty, IntegerProperty timerProperty, IntegerProperty numMovesProperty, IntegerProperty numUndoProperty) {
        // TODO
    	bindTo(levelNameProperty, timerProperty, numMovesProperty, numUndoProperty);
    	this.getChildren().add(levelNameLabel);
    	this.getChildren().add(timerLabel);
    	this.getChildren().add(numMovesLabel);
    	this.getChildren().add(numUndoLabel);
    }

    /**
     * @param s Seconds duration
     * @return A string that formats the duration stopwatch style
     */
    private static String format(int s) {
        final var d = Duration.of(s, SECONDS);

        int seconds = d.toSecondsPart();
        int minutes = d.toMinutesPart();

        return String.format("%02d:%02d", minutes, seconds);
        // Uncomment next line for JDK 8
//        return String.format("%02d:%02d:%02d", s / 3600, (s % 3600) / 60, (s % 60));
    }

    /**
     * Binds all properties to their respective UI elements.
     *
     * @param levelNameProperty Level Name Property
     * @param timerProperty Timer Property
     * @param numMovesProperty Number of Moves Property
     * @param numUndoProperty Number of Undoes Property
     */
    public void bindTo(StringProperty levelNameProperty, IntegerProperty timerProperty, IntegerProperty numMovesProperty, IntegerProperty numUndoProperty) {
        // TODO
    	if (levelNameProperty.get() == null || levelNameProperty.get() == "") {
    		levelNameLabel.textProperty().bind(Bindings.concat("Level: ").concat(levelNameProperty).concat("<Generated>"));
    	} else {
    		levelNameLabel.textProperty().bind(Bindings.concat("Level: ").concat(levelNameProperty).concat(".map"));
    	}
    	timerLabel.textProperty().bind(timerProperty.asString("Time: %d"));
    	numMovesLabel.textProperty().bind(numMovesProperty.asString("Moves: %d"));
    	numUndoLabel.textProperty().bind(numUndoProperty.asString("Undo Count: %d"));
    }
}
