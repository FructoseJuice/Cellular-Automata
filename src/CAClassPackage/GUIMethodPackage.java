package CAClassPackage;

import CellTypes.Cell;

import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * This class holds methods that Cellular Automatas can use for GUI implementation
 */
public class GUIMethodPackage {
    /**
     * Draws prompt to screen for initial CA startups
     */
    public static void renderGridPrompt(Stage stage, GridPane prompt, String title) {
        Scene scene = new Scene(prompt);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
        stage.centerOnScreen();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
    }

    /**
     * Draws vBox to screen
     */
    public static void renderVBoxPrompt(Stage stage, VBox vBox) {
        //Show prompt
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();
        stage.setMinWidth(stage.getWidth());
        stage.setMinHeight(stage.getHeight());
    }

    /**
     * Takes a gridPane and CellArray, and draws them to the screen
     */
    public static void renderFrame(GridPane gridPane, CellArray<? extends Cell> gridArray) {
        //Clear grid
        gridPane.getChildren().clear();

        //Add cells to grid
        for (int row = 0; row < gridArray.numRows; row++) {
            for (int col = 0; col < gridArray.numCols; col++) {
                gridPane.add(gridArray.get(row, col).getNode(), col, row);
            }
        }
    }

    /**
     * Draws a GridPane onto the screen
     *
     * @param title title of the Cellular Automata
     */
    public static Stage render(GridPane gridPane, String title) {
        Scene scene = new Scene(gridPane);
        Stage primaryStage = new Stage();
        primaryStage.setScene(scene);
        primaryStage.setTitle(title);
        primaryStage.show();

        return primaryStage;
    }
}
