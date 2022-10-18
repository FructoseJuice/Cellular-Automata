package CellTypes;

import Automatas.GenericCellularAutomata;
import javafx.scene.shape.Rectangle;

/**
 * A generic cell for use in CA's which takes in a dynamic amount of states
 */
public class GenericCell extends Cell {
    private int status;
    private final Rectangle node;

    public GenericCell(double size) {
        node = new Rectangle(size, size);
    }

    public GenericCell(int status) {
        node = new Rectangle();
        node.setFill(GenericCellularAutomata.stateColors.get(status));
        this.status = status;
    }

    //Setters
    public void setStatus(int status) {
        this.status = status;
        node.setFill(GenericCellularAutomata.stateColors.get(status));
    }

    //Getters
    public Rectangle getNode() {
        return node;
    }

    public int getState() {
        return status;
    }

    @Override
    public int getStatusBit() {
        return status;
    }
}
