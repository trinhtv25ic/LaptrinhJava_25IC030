package hki2;

import java.awt.*;

public class Ripple {
    int x, y;
    int radius = 0;
    int alpha = 120;
    public Ripple(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public void update() {

        radius += 4;

        alpha -= 3;

        if (alpha < 0) {
            alpha = 0;
        }
    }
    public void draw(Graphics2D g2) {

        g2.setColor(new Color(255,255,255,alpha));

        g2.drawOval(
                x - radius,
                y - radius,
                radius * 2,
                radius * 2
        );
    }

    public boolean isDone() {
        return alpha <= 0;
    }
}