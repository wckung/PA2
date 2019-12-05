package main;

import controllers.SceneManager;
import javafx.application.Application;
import javafx.stage.Stage;
import textgame.Main;
import views.panes.MainMenuPane;

import java.util.ArrayList;
import java.util.Arrays;

public class PipesMain extends Application {

    @Override
    public void start(final Stage primaryStage) {
        SceneManager.getInstance().setStage(primaryStage);
        SceneManager.getInstance().showPane(MainMenuPane.class);
    }

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--text")) {
            final var txtArgs = new ArrayList<>(Arrays.asList(args));
            txtArgs.remove(0);

            final var txtArrayArgs = new String[]{};
            Main.main(txtArgs.toArray(txtArrayArgs));

            System.exit(0);
        } else {
            PipesMain.launch(args);
        }
    }
}
