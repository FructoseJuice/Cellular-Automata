package CAClassPackage;

import java.util.ArrayList;

/**
 * Generic Class to hold an array of cells for a Cellular Automata
 * @param <T> A type of cell
 */
public class CellArray<T> {
    private final ArrayList<ArrayList<T>> grid;
    public final int numRows;
    public final int numCols;

    public CellArray(int numRows, int numCols) {
        this.numRows = numRows;
        this.numCols = numCols;
        grid = new ArrayList<>();

        //Initialize grid
        for (int i = 0; i < numRows; i++) {
            ArrayList<T> newRow = new ArrayList<>();

            for (int j = 0; j < numCols; j++) {
                newRow.add(null);
            }

            grid.add(newRow);
        }
    }

    /**
     * .get() method that automatically wraps a node across the screen if it goes beyond
     * the arrays indexes
     *
     * @return returns T which will be a type of cell
     */
    public T get(int row, int col) {
        try {
            return grid.get(row).get(col);
        } catch (Exception e) {
            //Check if we need to wrap col
            if (col >= numCols) {
                col = 0;
            } else if (col < 0) {
                col = numCols - 1;
            }

            //Check if we need to wrap row
            if (row >= numRows) {
                row = 0;
            } else if (row < 0) {
                row = numRows - 1;
            }

            //Return wrapped values
            return grid.get(row).get(col);
        }
    }

    public int getWidth() {
        return grid.get(0).size();
    }

    public int getHeight() {
        return grid.size();
    }

    public void set(int row, int col, T value) {
        grid.get(row).set(col, value);
    }

    /**
     * @return a for each targetable list of all cells this List contains
     */
    public Iterable<T> cellIterator() {
        return () -> {
            ArrayList<T> cells = new ArrayList<>();
            for (int row = 0; row < numRows; row++) {
                cells.addAll(grid.get(row));
            }

            return cells.iterator();
        };
    }
}
