package hki2;
import java.awt.Color;
import java.util.Random;

public class blockFactory {
    private static Random rand = new Random();

    private static Color randomColor() {
        Color[] colors = {
            Color.RED, Color.BLUE, Color.GREEN,
            Color.ORANGE, Color.MAGENTA, Color.CYAN,Color.yellow,Color.pink
        };
        return colors[rand.nextInt(colors.length)];
    }
    public static Block randomBlock() {
        int r = rand.nextInt(17);
        Color c = randomColor();

        switch (r) {
            case 0:
                return new Block(new int[][]{
                    {1,1},
                    {1,1}
                },c);
            case 1: 
                return new Block(new int[][]{
                    {1,1,1,1}
                },c);
            case 2: 
                return new Block(new int[][]{
                    {1,1,1},
                    {0,1,0}
                },c);
            case 3:
                return new Block(new int[][]{
                    {1,0},
                    {1,0},
                    {1,1}
                },c);
            case 4:
                return new Block(new int[][]{
                    {0,1},
                    {0,1},
                    {1,1}
                },c);
            case 5: 
                return new Block(new int[][]{
                    {0,1,1},
                    {1,1,0}
                },c);
            case 6: 
                return new Block(new int[][]{
                    {0,1,0},
                    {0,1,1},
                    {0,1,0}
                },c);
            case 7: 
                return new Block(new int[][]{
                    {1,1,1},
                    {1,0,0},
                    {1,0,0}
                },c);
            case 8: 
                return new Block(new int[][]{
                    {1,1,1},
                    {1,1,1},
                    {1,1,1}
                },c);
            case 9:
                return new Block(new int[][]{
                	{1,1,0},
                    {1,1,0},
                    {1,1,0}
                },c);
            case 10:
                return new Block(new int[][]{
                	{1,1,0},
                    {1,0,0},
                    {0,0,0}
                },c);
            case 11:
                return new Block(new int[][]{
                	{0,1,1},
                    {0,0,1},
                    {0,0,0}
                },c);
            case 12:
                return new Block(new int[][]{
                	{0,0,1},
                    {0,0,1},
                    {0,0,1},
                    {0,0,1}
                },c);
            case 13:
                return new Block(new int[][]{
                	{0,0,1},
                    {0,0,1},
                    {0,0,1}
                },c);
            case 14:
                return new Block(new int[][]{
                	{0,0,0},
                    {1,1,1},
                    {0,0,0}
                },c);
            case 15:
                return new Block(new int[][]{
                	{1,0,0},
                    {0,0,0},
                    {0,0,0}
                },c);
            default:
                return new Block(new int[][]{
                    {1,1,0},
                    {0,1,1}
                },c);
        }
    }
}