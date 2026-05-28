package hki2;
import javax.swing.*;
public class Main {
    public static void main(String[] args) {
    	JFrame frame = new JFrame("Block Puzzle");
        frame.setSize(500, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.add(new GamePanel());
        frame.setVisible(true);
    }
 
}