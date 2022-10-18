import CAClassPackage.GUIMethodPackage;
import Automatas.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This class is the main launcher for all Cellular Automatas.
 * First lets user choose which cellular automata they want,
 * then uses the related CA's launcher
 */
public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        VBox vBox = new VBox();
        vBox.setPadding(new Insets(0, 5, 5, 5));
        Label label = new Label("Choose Cellular Automata:");

        //Buttons
        Button elementary = new Button("Elementary");
        Button life = new Button("Game Of Life");
        Button langton = new Button("Langtons Loop");
        Button generic = new Button("Generic CA");

        //Event handlers
        elementary.setOnMousePressed(event -> Elementary.inputFormatPrompt(primaryStage));
        life.setOnMousePressed(event -> GameOfLife.inputFormatPrompt(primaryStage));
        langton.setOnMousePressed(event -> {
            LangtonsLoop.launcher();
            primaryStage.close();
        });
        generic.setOnMousePressed(event -> GenericCellularAutomata.promptUser(primaryStage));

        //Add nodes
        vBox.getChildren().add(label);
        vBox.getChildren().add(elementary);
        vBox.getChildren().add(life);
        vBox.getChildren().add(langton);
        vBox.getChildren().add(generic);

        //Show prompt
        GUIMethodPackage.renderVBoxPrompt(primaryStage, vBox);
    }
}
