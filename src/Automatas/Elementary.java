package Automatas;

import CAClassPackage.*;

import CellTypes.Cell;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class holds all methods for rendering an Elementary CA to the screen
 */
public class Elementary extends Application {
    //TODO: rect.widthProperty().bind(primaryStage.widthProperty().divide(cellSize))
    //Hashmap holding neighborhoods as a String, and it's corresponding rule (Status)
    static HashMap<String, Status> ruleTable;

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
            File[] files = new File("resources/elementaryCA").listFiles();

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


        GUIMethodPackage.renderVBoxPrompt(primaryStage, prompt);
    }

    /**
     * Prompt user to manually input initial state and rule number
     */
    private static void promptUser(Stage primaryStage) {
        GridPane prompt = new GridPane();
        prompt.setPadding(new Insets(0, 5, 5, 5));
        prompt.setHgap(2);

        /*
        Initialize all nodes
         */
        //TextFields
        TextField bitTextField = new TextField();
        TextField stateTextField = new TextField();
        //Labels
        Label bitLabel = new Label("Enter CA rule: ");
        Label stateLabel = new Label("Enter initial state: ");
        //Buttons
        Button submit = new Button("Submit");


        //Event handler for when user tries to submit input
        submit.setOnMousePressed(event -> {
            ArrayList<Cell> initialState = null;

            try {
                setRuleTable(parseBinaryRule(bitTextField.getText()));
                initialState = parseInitialState(stateTextField.getText());

            } catch (IllegalArgumentException | IllegalInitialStateException e) {
                //If invalid input, show alert with message
                Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
                alert.showAndWait();
            }

            if (initialState != null) {
                generateAnimation(initialState);
                primaryStage.close();
            }
        });


        //Add all nodes to GUI
        prompt.add(bitLabel, 0, 0);
        prompt.add(bitTextField, 0, 1);
        prompt.add(stateLabel, 1, 0);
        prompt.add(stateTextField, 1, 1);
        prompt.add(submit, 2, 1);


        GUIMethodPackage.renderGridPrompt(primaryStage, prompt, "Elementary Cellular Automata Launcher");
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


        //Create buttons for each file
        for (File file : files) {
            //Create new button for opening current file
            Button fileOpen = new Button(file.getName().split("\\.")[0]);

            //Set Event handler
            fileOpen.setOnMousePressed(event -> {
                try (Scanner fin = new Scanner(new FileInputStream(file))) {
                        /*
                        Parse inputs
                         */
                    //Set up list of status'
                    List<Status> statusList = new ArrayList<>();
                    //Parse each status
                    for (char character : fin.nextLine().toCharArray()) {
                        byte temp = (byte) Integer.parseInt("" + character);
                        statusList.add((temp == 1) ? Status.ALIVE : Status.DEAD);
                    }


                    setRuleTable(statusList);
                    ArrayList<Cell> initialState = parseInitialState(fin.nextLine());


                    //Start animation
                    generateAnimation(initialState);
                    primaryStage.close();

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
     * First parses input into an integer, then checks if in valid range ([0-255]),
     * finally uses {@link #setRuleTable(List)} Rules in order to put rules into hashmap
     *
     * @param input unparsed user input
     */
    public static List<Status> parseBinaryRule(String input) throws IllegalArgumentException {
        List<Status> statusList = Arrays.asList(new Status[8]); //Binary number array, will always be 8-bit

        try {
            //Try parsing input into integer value
            int ruleNum = Integer.parseInt(input);
            //Check if input is in valid range
            if (ruleNum < 0 || ruleNum > 255) {
                throw new IllegalArgumentException();
            }

            /*
            Convert to binary
             */
            int[] binaryChart = new int[]{128, 64, 32, 16, 8, 4, 2, 1};

            for (int i = 0; i < binaryChart.length; i++) {
                if (binaryChart[i] <= ruleNum) {
                    ruleNum -= binaryChart[i];
                    statusList.set(i, Status.ALIVE);
                } else {
                    statusList.set(i, Status.DEAD);
                }
            }
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Must input number in the range [0 - 255]");
        }

        return statusList;
    }

    /**
     * Takes a list of statuses, and sets up rule table
     */
    private static void setRuleTable(List<Status> statusList) {
        //Put rule table into hashmap
        ruleTable = new HashMap<>() {{
            put("111", statusList.get(0));
            put("110", statusList.get(1));
            put("101", statusList.get(2));
            put("100", statusList.get(3));
            put("011", statusList.get(4));
            put("010", statusList.get(5));
            put("001", statusList.get(6));
            put("000", statusList.get(7));
        }};
    }

    /**
     * Takes in input and returns a row of Cells
     *
     * @param input unparsed initial state
     * @return list of cells
     */
    private static ArrayList<Cell> parseInitialState(String input) throws IllegalInitialStateException {
        ArrayList<Cell> initialState = new ArrayList<>();

        char[] chars = input.toCharArray();

        for (int temp, i = 0; i < chars.length; i++) {
            temp = Integer.parseInt("" + chars[i]);

            if (temp == 0 || temp == 1) {
                //If cell is '1' it's Alive, otherwise if it's '0' it's Dead
                initialState.add((temp == 1) ? new Cell(Status.ALIVE) : new Cell(Status.DEAD));

            } else {
                throw new IllegalInitialStateException();
            }
        }

        return initialState;
    }


    /**
     * Generates elementary CA animation
     *
     * @param initialState the state the CA starts in
     */
    private static void generateAnimation(ArrayList<Cell> initialState) {
        GridPane grid = new GridPane();
        grid.setMinSize(800, 800);
        grid.setHgap(1);
        grid.setVgap(1);

        //Set size of each cell and add to gridPane
        final double cellSize = 800.0 / initialState.size();
        for (int i = 0; i < initialState.size(); i++) {
            initialState.get(i).setSize(cellSize);
            grid.add(initialState.get(i).getNode(), i, 0);
        }

        //Initialize Stage
        Stage primaryStage = GUIMethodPackage.render(grid, "Elementary CA");

        AnimationTimer animation = new AnimationTimer() {
            private ArrayList<Cell> currentGen = initialState;
            //private long lastUpdate;

            @Override
            public void handle(long now) {
                //if (now - lastUpdate >= 900_000_000) { //For timing each update
                currentGen = generateNextGeneration(currentGen, cellSize);
                //Set resizing properties to all cells
                for(Cell cell: currentGen) {
                    cell.getNode().widthProperty().bind(primaryStage.widthProperty().divide(currentGen.size()));
                    cell.getNode().heightProperty().bind(primaryStage.heightProperty().divide(currentGen.size()));
                }
                //Update gridPane
                int row = grid.getRowCount();
                int col = 0;
                for (Cell cell : currentGen) {
                    grid.add(cell.getNode(), col++, row);
                }

                //lastUpdate = now;
                //}
            }
        };


        animation.start();
    }

    /**
     * Generates each new generation every frame
     *
     * @param currentGen current gen
     * @param cellSize   size each cell should be
     * @return new generation of cells
     */
    private static ArrayList<Cell> generateNextGeneration(ArrayList<Cell> currentGen, double cellSize) {
        ArrayList<Cell> newGen = new ArrayList<>();


        StringBuilder neighborhood = new StringBuilder();
        //Bytes representing the state of each neighbor
        int rightNeighbor;
        int leftNeighbor;

        for (int i = 0; i < currentGen.size(); i++) {

            //Wrap indexes if they exceed grid size
            if (i == 0) {
                leftNeighbor = currentGen.get(currentGen.size() - 1).getStatusBit();
                rightNeighbor = currentGen.get(i + 1).getStatusBit();
            } else if (i == currentGen.size() - 1) {
                leftNeighbor = currentGen.get(i - 1).getStatusBit();
                rightNeighbor = currentGen.get(0).getStatusBit();
            } else {
                leftNeighbor = currentGen.get(i - 1).getStatusBit();
                rightNeighbor = currentGen.get(i + 1).getStatusBit();
            }


            //Construct local neighborhood
            neighborhood.append(leftNeighbor);
            neighborhood.append(currentGen.get(i).getStatusBit());
            neighborhood.append(rightNeighbor);


            //Determine new cell status from rule table
            newGen.add(new Cell(ruleTable.get(neighborhood.toString()), cellSize));
            neighborhood = new StringBuilder();
        }

        return newGen;
    }
}
