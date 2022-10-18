package CellTypes;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * A type of cell for use in CA's that has 8 set states with respective colors
 */
public class LangtonCell extends Cell {
    private int status;
    private final Rectangle node;

    public LangtonCell(double size) {
        node = new Rectangle(size, size);
    }

    public LangtonCell(int status) {
        node = new Rectangle();
        node.setFill(inferStatusColor(status));
        this.status = status;
    }

    //Setters
    public void setStatus(int status) {
        this.status = status;
        node.setFill(inferStatusColor(status));
    }

    public void setSize(double size) {
        node.setHeight(size);
        node.setWidth(size);
    }

    //Getters
    public Rectangle getNode() {
        return node;
    }

    public int getState() {
        return status;
    }

    //Helpers
    public Color inferStatusColor(int status) {
        switch (status) {
            case 0: {
                return Color.BLACK;
            }
            case 1: {
                return Color.YELLOW;
            }
            case 2: {
                return Color.PURPLE;
            }
            case 3: {
                return Color.BLUE;
            }
            case 4: {
                return Color.RED;
            }
            case 5: {
                return Color.GREEN;
            }
            case 6: {
                return Color.ORANGE;
            }
            case 7: {
                return Color.BROWN;
            }
            default: {
                return null;
            }
        }
    }
}
