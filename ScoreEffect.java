package hki2;

import java.awt.*;

public class ScoreEffect {

    int x, y;

    int value;

    int alpha = 255;

    public ScoreEffect(int x, int y, int value) {

        this.x = x;
        this.y = y;

        this.value = value;
    }

    public void update() {

        y -= 2;

        alpha -= 4;

        if (alpha < 0) {
            alpha = 0;
        }
    }

    public void draw(Graphics2D g2) {

        g2.setFont(new Font("Impact", Font.BOLD, 40));

        g2.setColor(
                new Color(255,215,0,alpha)
        );

        g2.drawString(
                "+" + value,
                x,
                y
        );
    }

    public boolean isDone() {

        return alpha <= 0;
    }
}