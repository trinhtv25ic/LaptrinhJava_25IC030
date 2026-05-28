package hki2;

import java.awt.Color;

public class Block {
    private int[][] shape;
    private Color color;

    public Block(int[][] shape, Color color) {
        this.shape = shape;
        this.color = color;
    }

    public int[][] getShape() {
        return shape;
    }

    public Color getColor() {
        return color;
    }

    public int getHeight() {
        return shape.length;
    }

    public int getWidth() {
        return shape[0].length;
    }

    public int getCell(int i, int j) {
        return shape[i][j];
    }
}