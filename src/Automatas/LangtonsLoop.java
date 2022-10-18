package Automatas;

import CAClassPackage.GUIMethodPackage;
import CAClassPackage.CellArray;
import CellTypes.LangtonCell;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * This class contains all methods for generating Langtons Loop and rendering it in a GUI.
 */
public class LangtonsLoop extends Application {
    //Rules for each cell to follow in choosing it's next state based on its cell neighborhood
    static HashMap<String, Integer> ruleTable = new HashMap<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        launcher();
    }

    /**
     * Launcher method for Langtons Loop
     * First parses all rules from rule_table.txt using {@link #parseRules()},
     * Then parses the initial state of the grid using {@link #parseInitialState()},
     * finally, calculates the size of each cell for rendering to the screen and uses
     * {@link #generateNextGeneration(CellArray, double)} to generate the CA's animation
     */
    public static void launcher() {
        parseRules();
        CellArray<LangtonCell> grid = parseInitialState();
        assert grid != null;
        generateAnimation(grid, (800.0 / Math.max(grid.numCols, grid.numRows)));
    }

    /**
     * Reads in rule table file and puts every single rule into rules HashMap
     * uses {@link #rotateString(String)} to find every possible permutation of each provided rule
     */
    private static void parseRules() {
        try (Scanner fin = new Scanner(new FileInputStream("resources/langtonsLoop/rule_table.txt"))) {
            while (fin.hasNextLine()) {
                String rule = fin.nextLine();

                String key = rule.substring(0, 5);
                int value = Integer.parseInt("" + rule.charAt(5));

                char anchor = key.charAt(0);
                List<String> keys = rotateString(key.substring(1, 5));
                for (String string : keys) {
                    ruleTable.put(anchor + string, value);
                }
            }
        } catch (FileNotFoundException ignored) {
            System.exit(0);
        }
    }

    /**
     * Takes in a Cell neighborhood represented by a string and rotates
     * the string to find each variation of it's Von Neumann neighborhood
     *
     * @param input Cell neighborhood
     * @return List of all possible permutations
     */
    public static List<String> rotateString(String input) {
        List<String> permutations = new ArrayList<>();
        //Add first key
        permutations.add(input);

        //Find all permutations
        StringBuilder stringBuilder;
        for (int i = 0; i < 3; i++) { //3 possible valid rotations
            stringBuilder = new StringBuilder(input);
            char temp = stringBuilder.charAt(3);
            stringBuilder.deleteCharAt(3);
            stringBuilder.insert(0, temp);

            input = stringBuilder.toString();
            permutations.add(input);
        }

        return permutations;
    }

    /**
     * Parses the initial state of the CA
     *
     * @return CellArray of the initial state
     */
    private static CellArray<LangtonCell> parseInitialState() {
        try (Scanner fin = new Scanner(new FileInputStream("resources/langtonsLoop/init_config.txt"))) {
            Scanner ints = new Scanner(fin.nextLine());
            int numRows = ints.nextInt();
            int numCols = ints.nextInt();

            //Set up LangArray
            CellArray<LangtonCell> grid = new CellArray<>(numRows, numCols);

            int rowIndex = 0;
            while (fin.hasNextLine()) {
                int colIndex = 0;

                String row = fin.nextLine();

                //Look at each state
                for (char character : row.toCharArray()) {
                    int state = Integer.parseInt("" + character);
                    grid.set(rowIndex, colIndex, new LangtonCell(state));


                    ++colIndex;
                }
                ++rowIndex;
            }

            return grid;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Draws Cellular Automata onto the screen as it continues to generate each new generation
     * with {@link #generateNextGeneration(CellArray, double)}
     *
     * @param initialState initial state of the grid
     * @param cellSize     size of each cell
     */
    private static void generateAnimation(CellArray<LangtonCell> initialState, final double cellSize) {
        GridPane gridPane = new GridPane();
        gridPane.setMinWidth(cellSize * initialState.numCols);
        gridPane.setMinHeight(cellSize * initialState.numRows);
        //Set cell size
        for (LangtonCell cell : initialState.cellIterator()) {
            cell.setSize(cellSize);
        }
        //Initialize gridPane
        GUIMethodPackage.renderFrame(gridPane, initialState);

        //Initialize and render stage
        Stage primaryStage = GUIMethodPackage.render(gridPane, "Langtons Loop");
        primaryStage.setResizable(false);

        //Generates each frame of game
        AnimationTimer gameLoop = new AnimationTimer() {
            private CellArray<LangtonCell> currentGen = initialState;

            @Override
            public void handle(long now) {
                currentGen = generateNextGeneration(currentGen, cellSize);
                GUIMethodPackage.renderFrame(gridPane, currentGen);
            }
        };


        gameLoop.start();
    }

    /**
     * Used by {@link #generateAnimation(CellArray, double)} to generate each new generation of cells
     *
     * @param pastGen  past generation
     * @param cellSize size of each cell
     * @return new generation
     */
    private static CellArray<LangtonCell> generateNextGeneration(CellArray<LangtonCell> pastGen, final double cellSize) {
        CellArray<LangtonCell> newGen = new CellArray<>(pastGen.numRows, pastGen.numCols);
        //Initialize with cells
        for (int row = 0; row < pastGen.numRows; row++) {
            for (int col = 0; col < pastGen.numCols; col++) {
                newGen.set(row, col, new LangtonCell(cellSize));
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
