package Automatas;

import CAClassPackage.GUIMethodPackage;
import CAClassPackage.CellArray;
import CAClassPackage.IllegalInitialStateException;
import CAClassPackage.Status;
import CellTypes.GenericCell;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class GenericCellularAutomata extends Application {
    //Colors that generic cells can inherit
    public static ArrayList<Color> stateColors = new ArrayList<>();
    //Rule table all cells follow
    public static HashMap<String, Integer> ruleTable = new HashMap<>();

    
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        promptUser(primaryStage);
    }

    public static void promptUser(Stage primaryStage) {
        GridPane prompt = new GridPane();
        prompt.setPadding(new Insets(0, 5, 5, 5));
        prompt.setVgap(3);

        //Labels
        Label dimensionLabel = new Label("Enter amount of dimensions:");
        Label statesLabel = new Label("Enter amount of states (1D can only have 2):");
        Label ruleTableLabel = new Label("Enter rule table file (Rule number for 1D):");
        Label initialConfigLabel = new Label("Enter initial configuration.");

        //TextFields
        TextField dimensionField = new TextField();
        TextField statesField = new TextField();
        TextField ruleTableField = new TextField();
        TextArea initialConfigField = new TextArea();

        //Buttons
        Button submit = new Button("Submit");

        //Event Handlers
        submit.setOnMousePressed(event -> {
            /*
                Parse all inputs
            */
            try {
                int dimensions = Integer.parseInt(dimensionField.getText());
                int states = Integer.parseInt(statesField.getText());
                String ruleTableName = ruleTableField.getText();

                if (!(dimensions == 1 || dimensions == 2)) {
                    throw new IllegalInitialStateException("Must be either 1 or 2 dimensions.");
                }

                //Set states to 2 for 1D
                if (dimensions == 1 && states != 2) states = 2;

                if (states < 2) {
                    throw new IllegalInitialStateException("Cells must have at least 2 states.");
                }

                if(dimensions == 1) {
                    //Parse binary rule
                    List<Status> statusList = Elementary.parseBinaryRule(ruleTableName);
                    //Set rules
                    ruleTable = new HashMap<>() {{
                        put("111", statusList.get(0).getBit());
                        put("110", statusList.get(1).getBit());
                        put("101", statusList.get(2).getBit());
                        put("100", statusList.get(3).getBit());
                        put("011", statusList.get(4).getBit());
                        put("010", statusList.get(5).getBit());
                        put("001", statusList.get(6).getBit());
                        put("000", statusList.get(7).getBit());
                    }};

                    //Set state colors
                    stateColors.add(Color.WHITE);
                    stateColors.add(Color.BLACK);


                } else {
                    File ruleTableFile;
                    //Get rule table
                    if(!ruleTableName.contains("resources/")) {
                        ruleTableFile = new File("resources/" + ruleTableName);
                    } else {
                        ruleTableFile = new File(ruleTableName);
                    }

                    //Parse rule table
                    parseRules(ruleTableFile);
                    //Generate state colors
                    generateColors(states);
                }


                //Check if initial state field is not empty
                String unparsedInitialState = initialConfigField.getText();
                if (unparsedInitialState.isEmpty()) {
                    throw new IllegalInitialStateException("Must enter initial state.");
                }

                CellArray<GenericCell> initialState;
                //Parse initial state
                if (dimensions == 1) {
                    initialState = parseInitial1DState(unparsedInitialState);
                    //Proceed with animation
                    generate1DAnimation(initialState, (800.0 / initialState.numCols));
                } else {
                    initialState = parseInitial2DState(unparsedInitialState, states);
                    //Proceed with animation
                    generate2DAnimation(initialState, (800.0 / Math.max(initialState.numRows, initialState.numCols)));
                }


                primaryStage.close();

            } catch (IllegalArgumentException | IllegalInitialStateException e) {
                Alert alert = new Alert(Alert.AlertType.WARNING, e.getMessage());
                alert.showAndWait();
            }
        });

        //Add nodes to GridPane
        prompt.add(dimensionLabel, 0, 0);
        prompt.add(statesLabel, 0, 1);
        prompt.add(ruleTableLabel, 0, 2);
        prompt.add(initialConfigLabel, 0, 3);

        prompt.add(dimensionField, 3, 0);
        prompt.add(statesField, 3, 1);
        prompt.add(ruleTableField, 3, 2);
        prompt.add(initialConfigField, 3, 3);

        prompt.add(submit, 1, 4);


        GUIMethodPackage.renderGridPrompt(primaryStage, prompt, "Generic CA Launcher");
    }

    /**
     * Generates random colors for each cell state
     *
     * @param numStates number of possible states
     */
    public static void generateColors(int numStates) {
        Random rand = new Random();
        int r;
        int g;
        int b;

        for (int i = 0; i < numStates; i++) {
            r = rand.nextInt(256);
            g = rand.nextInt(256);
            b = rand.nextInt(256);

            stateColors.add(Color.rgb(r, g, b));
        }
    }

    public static CellArray<GenericCell> parseInitial1DState(String initialConfig)
            throws IllegalInitialStateException {

        String[] unparsedList = initialConfig.split("\\n");

        if (unparsedList.length != 1) {
            throw new IllegalInitialStateException("Initial state must match entered number of dimensions.");
        }

        //Set up list
        CellArray<GenericCell> initialState = new CellArray<>(1, unparsedList[0].length());

        int colIndex = 0;
        for (char character : unparsedList[0].toCharArray()) {
            int state = Integer.parseInt("" + character);

            if (state < 0 || state > 2) {
                throw new IllegalInitialStateException("Must enter valid number of cell states.");
            }

            //Create each new cell
            initialState.set(0, colIndex, new GenericCell(state));

            ++colIndex;
        }

        return initialState;
    }

    public static CellArray<GenericCell> parseInitial2DState(String initialConfig, int numStates) throws IllegalInitialStateException {
        String[] unparsedList = initialConfig.split("\\n");

        //Check if valid amount of rows
        if (unparsedList.length < 2) {
            throw new IllegalInitialStateException("Initial state must have at least 2 rows.");
        }

        final int numRows = unparsedList.length;
        final int numCols = unparsedList[0].length();

        //Set up list
        CellArray<GenericCell> initialState = new CellArray<>(numRows, numCols);

        int rowIndex = 0;
        for (String col : unparsedList) {
            //Check if valid amount of columns
            if (col.length() != numCols) {
                throw new IllegalInitialStateException("Must enter valid amount of columns in initial state grid.");
            }

            int colIndex = 0;
            byte state;
            for (char character : col.toCharArray()) {
                //Try to parse each Cell's state
                state = (byte) Integer.parseInt("" + character);

                if (state < 0 || state > numStates) {
                    throw new IllegalInitialStateException("Must enter valid cell states.");
                }

                //Create each new cell
                initialState.set(rowIndex, colIndex, new GenericCell(state));

                ++colIndex;
            }
            ++rowIndex;
        }

        return initialState;
    }

    public static void parseRules(File ruleTableFile) {
        try (Scanner fin = new Scanner(new FileInputStream(ruleTableFile))) {
            while (fin.hasNextLine()) {
                String rule = fin.nextLine();

                String key = rule.substring(0, 5);
                int value = Integer.parseInt("" + rule.charAt(5));

                char anchor = key.charAt(0);
                List<String> keys = LangtonsLoop.rotateString(key.substring(1, 5));
                for (String string : keys) {
                    ruleTable.put(anchor + string, value);
                }
            }
        } catch (FileNotFoundException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "FILE NOT FOUND.");
            alert.showAndWait();
        }
    }


    public static void generate1DAnimation(CellArray<GenericCell> initialState, final double cellSize) {
        GridPane gridPane = new GridPane();
        gridPane.setMinHeight(800);
        gridPane.setMinWidth(800);
        //Set cell size
        for(GenericCell cell : initialState.cellIterator()) {
            cell.setSize(cellSize);
        }

        //Render initial state
        for(int col = 0;col < initialState.numCols;col++) {
            gridPane.add(initialState.get(0, col).getNode(), col, 0);
        }

        //Generates each frame
        AnimationTimer gameLoop = new AnimationTimer() {
            private CellArray<GenericCell> currentGen = initialState;
            private int row = 1;

            @Override
            public void handle(long now) {
                currentGen = generateNext1DGeneration(currentGen, cellSize);
                //Draw to gridPane
                for(int col = 0;col < currentGen.numCols;col++) {
                    gridPane.add(currentGen.get(0, col).getNode(), col, row);
                }

                ++row;
            }
        };

        /*
        Boiler plate
         */
        draw(gridPane, gameLoop);
    }

    private static void draw(GridPane gridPane, AnimationTimer gameLoop) {
        Stage stage = new Stage();
        Scene scene = new Scene(gridPane);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Generic CA");
        stage.show();
        stage.centerOnScreen();
        gameLoop.start();
    }

    public static void generate2DAnimation(CellArray<GenericCell> initialState, final double cellSize) {
        GridPane gridPane = new GridPane();
        gridPane.setMinWidth(cellSize * initialState.numCols);
        gridPane.setMinHeight(cellSize * initialState.numRows);
        //Set cell size
        for (GenericCell cell : initialState.cellIterator()) {
            cell.setSize(cellSize);
        }

        //Render the initial state to the screen
        GUIMethodPackage.renderFrame(gridPane, initialState);


        //Generates each frame of game
        AnimationTimer gameLoop = new AnimationTimer() {
            private CellArray<GenericCell> currentGen = initialState;

            @Override
            public void handle(long now) {
                currentGen = generateNext2DGeneration(currentGen, cellSize);
                GUIMethodPackage.renderFrame(gridPane, currentGen);
            }
        };


        /*
        BOILER PLATE GUI
         */
        draw(gridPane, gameLoop);
    }

    private static CellArray<GenericCell> generateNext1DGeneration(CellArray<GenericCell> pastGen, final double cellSize) {
        CellArray<GenericCell> newGen = new CellArray<>(1, pastGen.numCols);

        for(int col = 0;col < pastGen.numCols; col++) {
            newGen.set(0, col, new GenericCell(cellSize));
        }

        for(int col = 0;col < pastGen.numCols;col++) {
            /*
            Build neighborhood
             */
            String neighborhood = "";
            neighborhood += pastGen.get(0, col - 1).getStatusBit();
            neighborhood += pastGen.get(0, col).getStatusBit();
            neighborhood += pastGen.get(0, col + 1).getStatusBit();
            //Update current cells state
            newGen.get(0, col).setStatus(ruleTable.get(neighborhood));
        }

        return newGen;
    }

    private static CellArray<GenericCell> generateNext2DGeneration(CellArray<GenericCell> pastGen, final double cellSize) {
        CellArray<GenericCell> newGen = new CellArray<>(pastGen.numRows, pastGen.numCols);
        //Initialize with cells
        for (int row = 0; row < pastGen.numRows; row++) {
            for (int col = 0; col < pastGen.numCols; col++) {
                newGen.set(row, col, new GenericCell(cellSize));
            }
        }


        //noinspection DuplicatedCode
        for (int row = 0; row < pastGen.numRows; row++) {
            for (int col = 0; col < pastGen.numCols; col++) {

                /*
                Build neighborhood
                 */
                String neighborhood = "";
                //Add current cells status
                neighborhood += pastGen.get(row, col).getState();
                //Add all neighbors
                neighborhood += pastGen.get(row - 1, col).getState(); //N
                neighborhood += pastGen.get(row, col + 1).getState(); //E
                neighborhood += pastGen.get(row + 1, col).getState(); //S
                neighborhood += pastGen.get(row, col - 1).getState(); //W

                //Update current cells state
                try {
                    newGen.get(row, col).setStatus(ruleTable.get(neighborhood));
                } catch (NullPointerException ignored) {
                }
            }
        }

        return newGen;
    }
}
