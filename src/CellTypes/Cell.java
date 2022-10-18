package CellTypes;

import CAClassPackage.Status;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Represents a completely basic cell for use in Cellular Automatas
 */
public class Cell {
    private Status status;
    private final Rectangle node;

    public Cell() {
        node = new Rectangle();
    }

    public Cell(double size) {
        node = new Rectangle(size, size);
    }

    public Cell(Status status) {
        this.status = status;
        node = new Rectangle();
        node.setFill((status == Status.ALIVE) ? Color.BLACK : Color.TRANSPARENT);
    }

    public Cell(Status status, double size) {
        this.status = status;
        node = new Rectangle(size, size);
        node.setFill((status == Status.ALIVE) ? Color.BLACK : Color.TRANSPARENT);
    }

    //Getters
    public Rectangle getNode() {
        return node;
    }

    public Status getStatus() {
        return status;
    }

    public int getStatusBit() {
        return status.getBit();
    }

    //Setters
    public void setStatus(Status status) {
        this.status = status;

        node.setFill((status == Status.ALIVE) ? Color.BLACK : Color.TRANSPARENT);
    }

    public void setSize(double size) {
        node.setHeight(size);
        node.setWidth(size);
    }
}
