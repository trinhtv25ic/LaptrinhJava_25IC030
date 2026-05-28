package hki2;

import javax.swing.*;
import java.awt.*;

public class GameOverPanel extends JPanel {

    private ImageIcon gif = new ImageIcon(
        "C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\Block Puzzle5.png"
    );

    private int score;
    private GameFrame frame; 
    private JButton btnHome; 

    public GameOverPanel(GameFrame frame, int score) { 
        this.frame = frame;
        this.score = score;
        this.setLayout(null); 

        btnHome = new JButton("HOME");
        btnHome.setFont(new Font("Arial", Font.BOLD, 18));
        btnHome.setForeground(Color.WHITE);
        btnHome.setBackground(new Color(50, 50, 50));
        btnHome.setFocusPainted(false);
        btnHome.setOpaque(false);
		btnHome.setContentAreaFilled(false);
		btnHome.setBorderPainted(false);
		btnHome.setText("");
        btnHome.setBounds(200, 400, 200, 50); 
        btnHome.addActionListener(e -> {
            frame.showMenu();
        });
        
        this.add(btnHome);

        Timer timer = new Timer(30, e -> repaint());
        timer.start();
    }

    public void setScore(int score) {
        this.score = score;
        repaint(); 
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        g2.drawImage(
                gif.getImage(),
                0,
                0,
                getWidth(),
                getHeight(),
                this
        );
        g2.setFont(new Font("Arial", Font.BOLD, 42));
        g2.setColor(Color.BLACK);
        g2.drawString("SCORE: " + score, 180, 550);
        g2.setColor(Color.WHITE);
        g2.drawString("SCORE: " + score, 180, 550);
    }
}