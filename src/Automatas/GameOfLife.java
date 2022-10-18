package Automatas;

import CAClassPackage.*;
import CellTypes.Cell;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class represents all related methods for rendering the Game Of Life on a GUI
 */
public class GameOfLife extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        inputFormatPrompt(primaryStage);
    }

    /**
     * Prompts user to choose either Manual or File input for the grid
     */
    @SuppressWarnings("DuplicatedCode")
    public static void inputFormatPrompt(Stage primaryStage) {
        VBox prompt = new VBox();
        prompt.setPadding(new Insets(0, 53, 5, 5));

        //Initialize gui nodes
        Label label = new Label("Select input method:");
        Button fileButton = new Button("File");
        Button manualButton = new Button("Manual");


        //Set event handlers
        manualButton.setOnMousePressed(event -> promptUser(primaryStage));
        fileButton.setOnMousePressed(event -> {

            File[] files = new File("resources/gameOfLife").listFiles();

            if (files != null && files.length != 0) {
                filePrompt(primaryStage, files);
            } else {
                //If no files are found
                Alert alert = new Alert(Alert.AlertType.WARNING, "No files found.");
                alert.showAndWait();
            }
        });

        //Add nodes to VBox
        prompt.getChildren().add(label);
        prompt.getChildren().add(fileButton);
        prompt.getChildren().add(manualButton);


        //Draw to screen
        GUIMethodPackage.renderVBoxPrompt(primaryStage, prompt);
    }

    /**
     * Prompts user to manually enter amount of rows, cols, and the initial state of the grid
     */
    private static void promptUser(Stage primaryStage) {
        GridPane prompt = new GridPane();
        prompt.setHgap(2);
        prompt.setPadding(new Insets(0, 5, 5, 5));

        //Labels
        Label rowLabel = new Label("Enter amount of rows:");
        Label colLabel = new Label("Enter amount of cols:");
        Label gridLabel = new Label("Enter initial grid:");
        Button submit = new Button("Submit");

        //TextFields
        TextField rowInput = new TextField();
        TextField colInput = new TextField();
        TextArea gridInput = new TextArea();

        //Set all nodes on gridPane
        prompt.add(rowLabel, 0, 0);
        prompt.add(colLabel, 1, 0);
        prompt.add(gridLabel, 2, 0);

        prompt.add(rowInput, 0, 1);
        prompt.add(colInput, 1, 1);
        prompt.add(gridInput, 2, 1);

        prompt.add(submit, 3, 1);


        //Event handler for submission button
        submit.setOnMousePressed(event -> {
            try {
                int numRows = Integer.parseInt(rowInput.getText());
                int numCols = Integer.parseInt(colInput.getText());


                //Check if rows and cols are unsigned integers
                if (numCols < 0) throw new IllegalInitialStateException("Amount of columns must be positive.");
                if (numRows < 0) throw new IllegalInitialStateException("Amount of rows must be positive.");
                //Checks if rows and cols are >= 3
                if (numCols < 3) throw new IllegalInitialStateException("Must have at least 3 columns.");
                if (numRows < 3) throw new IllegalInitialStateException("Must have at least 3 rows.");

                //Parse initial state
                CellArray<Cell> initialState = parseInitialState(gridInput.getText(), numRows, numCols);

                //Start animation
                generateAnimation(numRows, numCols, initialState);
                primaryStage.close();

            } catch (IllegalArgumentException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING, "Must enter a valid number for the row and column amounts.");
                alert.showAndWait();
            } catch (IllegalInitialStateException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
                alert.showAndWait();
            }
        });


        //Draw prompt to screen
        GUIMethodPackage.renderGridPrompt(primaryStage, prompt, "Game Of Life Launcher");
    }

    /**
     * Gives user list of files to launch from
     */
    private static void filePrompt(Stage primaryStage, File[] files) {
        VBox filePrompt = new VBox();
        filePrompt.setPadding(new Insets(0, 5, 5, 5));

        //Add label to top of VBox
        Label label = new Label("Select file: ");
        filePrompt.getChildren().add(label);


        for (File file : files) {
            //Create new button for opening current file
            Button fileOpen = new Button(file.getName().split("\\.")[0]);

            //Set Event handler
            fileOpen.setOnMousePressed(event -> {
                try (Scanner fin = new Scanner(new FileInputStream(file))) {
                        /*
                        Read in file info
                         */
                    //Reading in first two integer values
                    Scanner scan = new Scanner(fin.nextLine());
                    int numRows = scan.nextInt();
                    int numCols = scan.nextInt();

                    //Read in each line of grid
                    StringBuilder grid = new StringBuilder();
                    while (fin.hasNextLine()) {
                        grid.append(fin.nextLine()).append("\n");
                    }

                    //Start animation
                    primaryStage.close();
                    CellArray<Cell> lifeArray = parseInitialState(grid.toString(), numRows, numCols);
                    generateAnimation(numRows, numCols, lifeArray);

                } catch (FileNotFoundException | IllegalInitialStateException e) {
                    e.printStackTrace();
                }
            });

            //Add button to VBox
            filePrompt.getChildren().add(fileOpen);
        }


        Scene scene = new Scene(filePrompt);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Game of Life launcher");
        primaryStage.centerOnScreen();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());
    }

    /**
     * Attempts to parse the initial grid into 1's and 0's
     *
     * @param input    Unparsed input from user
     * @param rowCount Amount of rows the grid should be
     * @param colCount Amount of columns the grid should be
     * @return A LifeArray with the parsed 2D grid
     * @throws IllegalInitialStateException If the grid has an invalid amount of rows/cols, or if it contains
     *                                      anything other than a 1 or a 0.
     */
    private static CellArray<Cell> parseInitialState(String input, int rowCount, int colCount)
            throws IllegalInitialStateException {

        String[] unparsedList = input.split("\\n");


        //Check if valid amount of rows
        if (unparsedList.length != rowCount) {
            throw new IllegalInitialStateException("Must enter valid amount of rows in initial state grid.");
        }

        //Set up List
        CellArray<Cell> initialState = new CellArray<>(rowCount, colCount);

        //Start parsing
        int rowIndex = 0;
        for (String col : unparsedList) {
            //Check if valid amount of columns
            if (col.length() != colCount) {
                throw new IllegalInitialStateException("Must enter valid amount of columns in initial state grid.");
            }

            int colIndex = 0;
            byte temp;
            for (char character : col.toCharArray()) {
                //Try to parse each Cell
                try {
                    temp = (byte) Integer.parseInt("" + character);
                } catch (IllegalArgumentException e) {
                    throw new IllegalInitialStateException("Initial state must only consist of 1's and 0's.");
                }

                //If not 1 or 0, this isn't a valid cell state
                if (!(temp == 1 || temp == 0)) {
                    throw new IllegalInitialStateException("Initial state must only consist of 1's and 0's.");
                }


                initialState.set(rowIndex, colIndex, ((temp == 1) ? new Cell(Status.ALIVE) : new Cell(Status.DEAD)));

                ++colIndex;
            }
            ++rowIndex;
        }

        return initialState;
    }


    /**
     * Generates the actual Game Of Life
     *
     * @param numRows      Number of rows the grid is
     * @param numCols      Number of cols the grid is
     * @param initialState Initial grid to start animation from
     */
    private static void generateAnimation(int numRows, int numCols, CellArray<Cell> initialState) {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(1);
        gridPane.setHgap(1);

        //Calculate grid size
        final double cellSize = 800.0 / Math.max(numCols, numRows); //We want to take the larger value for calculating size
        gridPane.setMinWidth(cellSize * numCols);
        gridPane.setMinHeight(cellSize * numRows);

        //Set cell size
        for (Cell cell : initialState.cellIterator()) {
            cell.setSize(cellSize);
        }

        //Render the initial state to the screen
        GUIMethodPackage.renderFrame(gridPane, initialState);

        //Initialize Stage and render stage
        Stage primaryStage = GUIMethodPackage.render(gridPane, "Game Of Life");

        //Generates each frame of game
        AnimationTimer gameLoop = new AnimationTimer() {
            private CellArray<Cell> currentGen = initialState;
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_999_999) {     //Timing each update
                    currentGen = generateNextGeneration(currentGen, cellSize);

                    //Bind cell size to Stage
                    for(Cell cell: currentGen.cellIterator()) {
                        cell.getNode().widthProperty().bind(primaryStage.widthProperty().divide(currentGen.getWidth()));
                        cell.getNode().heightProperty().bind(primaryStage.heightProperty().divide(currentGen.getHeight()));
                    }

                    GUIMethodPackage.renderFrame(gridPane, currentGen);
                    lastUpdate = now;


                }
            }
        };

        gameLoop.start();
    }

    /**
     * Generates the next frame for the game
     *
     * @param pastGen  Past generation to generate from
     * @param cellSize Size of each cell
     * @return New generation
     */
    private static CellArray<Cell> generateNextGeneration(CellArray<Cell> pastGen, double cellSize) {
        CellArray<Cell> newGen = new CellArray<>(pastGen.numRows, pastGen.numCols);
        //Initialize with cells
        for (int row = 0; row < pastGen.numRows; row++) {
            for (int col = 0; col < pastGen.numCols; col++) {
                newGen.set(row, col, new Cell(cellSize));
            }
        }


        for (int row = 0; row < pastGen.numRows; row++) {
            for (int col = 0; col < pastGen.numCols; col++) {

                /*
                Build neighborhood
                 */
                byte numAlive = 0;
                for (int nestRow = row - 1; nestRow <= row + 1; nestRow++) {
                    for (int nestCol = col - 1; nestCol <= col + 1; nestCol++) {
                        if (!(nestCol == col && nestRow == row)) { //Makes sure we're not looking at the current cell
                            if (pastGen.get(nestRow, nestCol).getStatus() == Status.ALIVE) ++numAlive;
                        }
                    }
                }

                /*
                Update status of current cell
                 */
                if (pastGen.get(row, col).getStatus() == Status.ALIVE) {
                    //Check if cell has less than 2 alive neighbors, or more than 3 alive neighbors
                    if (numAlive < 2 || numAlive > 3) {
                        newGen.get(row, col).setStatus(Status.DEAD);
                    } else {
                        newGen.get(row, col).setStatus(Status.ALIVE);
                    }
                } else {
                    //If Cell is dead, we must check if it has three neighbors to revive it
                    if (numAlive == 3) {
                        newGen.get(row, col).setStatus(Status.ALIVE);
                    } else {
                        newGen.get(row, col).setStatus(Status.DEAD);
                    }
                }
            }
        }

        return newGen;
    }

}
