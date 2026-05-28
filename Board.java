package hki2;
import java.awt.Color;
public class Board {
    private Color[][] grid;
    public Board() {
        grid = new Color[10][10];
    }
    public Color getCell(int row, int col) {
        return grid[row][col];
    }
    public void setCell(int row, int col, Color color) {
        grid[row][col] = color; 
    }
    public boolean canPlace(Block block, int row, int col) {
        for (int i = 0; i < block.getHeight(); i++) {
            for (int j = 0; j < block.getWidth(); j++) {

                if (block.getCell(i, j) == 1) {
                    int newRow = row + i;
                    int newCol = col + j;
                    if (newRow >= 10 || newCol >= 10 || newRow < 0 || newCol < 0) {
                        return false;
                    }
                    if (grid[newRow][newCol] != null) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    public void placeBlock(Block block, int row, int col) {
        for (int i = 0; i < block.getHeight(); i++) {
            for (int j = 0; j < block.getWidth(); j++) {
                if (block.getCell(i, j) == 1) {
                    grid[row + i][col + j] = block.getColor();
                }
            }
        }
    }
    public boolean isFullRow(int row) {
        for (int j = 0; j < 10; j++) {
            if (grid[row][j] == null) return false;
        }
        return true;
    }
    public void clearRow(int row) {
        for (int j = 0; j < 10; j++) {
            grid[row][j] = null;
        }
    }
    public boolean isFullCol(int col) {
        for (int i = 0; i < 10; i++) {
            if (grid[i][col] == null) return false;
        }
        return true;
    }
    public void clearCol(int col) {
        for (int i = 0; i < 10; i++) {
            grid[i][col] = null;
        }
    }
    public int clearLines() {
        int total = 0;

        for (int i = 0; i < 10; i++) {
            if (isFullRow(i)) {
                clearRow(i);
                total++;
            }
        }

        for (int j = 0; j < 10; j++) {
            if (isFullCol(j)) {
                clearCol(j);
                total++;
            }
        }
        return total;
    }
    public boolean canPlaceAnywhere(Block block) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (canPlace(block, i, j)) {
                    return true;
                }
            }
        }
        return false;
    }
}