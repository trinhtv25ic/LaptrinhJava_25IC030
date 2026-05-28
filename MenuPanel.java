package hki2;

import java.awt.*;
import java.util.ArrayList;
import java.awt.event.*;
import javax.swing.*;

public class MenuPanel extends JPanel {
    private GameFrame frame;
    private JButton onlbtn;
    private JButton offbtn;
    private JButton rankbtn;
    private BorderLayout menu;
    private JButton soundbtn;
    private ArrayList<Ripple> ripples = new ArrayList<>();

    public MenuPanel(GameFrame frame) {
        this.frame = frame;
        Timer rippleTimer = new Timer(16, e -> {
            for (int i = 0; i < ripples.size(); i++) {
                ripples.get(i).update();
                if (ripples.get(i).isDone()) {
                    ripples.remove(i);
                    i--;
                }
            }
            repaint();
        });
        rippleTimer.start();
        
        Sound.playMusic("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\ILLIT-_아일릿_-‘Magnetic’-Instrumental.wav");
        
        JPanel bgPanel = new JPanel() {
            Image bg = new ImageIcon(
                "C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\Block Puzzle.png"
            ).getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
                for (Ripple r : ripples) {
                    r.draw(g2);
                }
            }
        };
        
        soundbtn = new JButton("Sound");
        soundbtn.setBounds(150, 640, 80, 80);
        bgPanel.add(soundbtn);
        
        setLayout(new BorderLayout());
        bgPanel.setLayout(null);
        
        bgPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                ripples.add(new Ripple(e.getX(), e.getY()));
                repaint();
            }
        });
        
        onlbtn = new JButton(" Online");
        onlbtn.setBounds(100, 420, 120, 120);
        offbtn = new JButton(" Offline");
        offbtn.setBounds(350, 420, 120, 120);
        rankbtn = new JButton(" Rank");
        rankbtn.setBounds(255, 640, 80, 80);
        
        rankbtn.addActionListener(e -> {
            frame.showRank(); 
        });
        
        JButton btnFeedback = new JButton("FEEDBACK");
        btnFeedback.setFont(new Font("Arial", Font.BOLD, 16));
        btnFeedback.setBounds(370, 650, 80, 80); 
        btnFeedback.setOpaque(false);
        btnFeedback.setContentAreaFilled(false);
        btnFeedback.setBorderPainted(false);
        btnFeedback.setText("");
        btnFeedback.addActionListener(e -> handleFeedbackClick());
        this.add(btnFeedback);
        
        bgPanel.add(onlbtn);
        bgPanel.add(offbtn);
        bgPanel.add(rankbtn);
        
        offbtn.setOpaque(false);
        offbtn.setContentAreaFilled(false);
        offbtn.setBorderPainted(false);
        offbtn.setText("");
        onlbtn.setOpaque(false);
        onlbtn.setContentAreaFilled(false);
        onlbtn.setBorderPainted(false);
        onlbtn.setText("");
        rankbtn.setOpaque(false);
        rankbtn.setContentAreaFilled(false);
        rankbtn.setBorderPainted(false);
        rankbtn.setText("");
        soundbtn.setOpaque(false);
        soundbtn.setContentAreaFilled(false);
        soundbtn.setBorderPainted(false);
        soundbtn.setText("");
        
        onlbtn.addActionListener(e -> {
            String currentName = PlayerData.getPlayerName();
            if (currentName == null || currentName.trim().isEmpty()) {
                currentName = "Player_" + (int)(Math.random() * 1000);
            }
            frame.showLobby(currentName);
            frame.revalidate();
        });
        
        offbtn.addActionListener(e -> {
            Sound.stopMusic();
            frame.showGame();
        });
        
        soundbtn.addActionListener(e -> {
            Sound.toggleMusic();
        });
        
        add(bgPanel, BorderLayout.CENTER);
        
        String playerName = PlayerData.getPlayerName();
        String name = "";
        while (name == null || name.trim().isEmpty()) {
            name = JOptionPane.showInputDialog(
                    this,
                    "Enter your name to start the game:",
                    "Player Name Required",
                    JOptionPane.QUESTION_MESSAGE
            );
            
            if (name == null) {
                System.exit(0); 
            }
        }

        PlayerData.savePlayerName(name.trim());
    }

    private void handleFeedbackClick() {
        Integer[] stars = {5, 4, 3, 2, 1};
        JComboBox<Integer> starCombo = new JComboBox<>(stars);
        
        JTextArea commentArea = new JTextArea(5, 20);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentArea);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.add(new JLabel("Đánh giá số sao:"));
        panel.add(starCombo);
        panel.add(new JLabel("Bình luận của bạn:"));
        panel.add(scrollPane);

        int result = JOptionPane.showConfirmDialog(
                this, 
                panel, 
                "Gửi phản hồi về Game", 
                JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            int selectedStars = (int) starCombo.getSelectedItem();
            String comment = commentArea.getText().trim();
            
            if (comment.isEmpty()) {
                comment = "Không có bình luận.";
            }

            saveFeedbackToFile(selectedStars, comment);
        }
    }

    private void saveFeedbackToFile(int stars, String comment) {
        String filePath = "feedback.txt";
        String playerName = PlayerData.getPlayerName();
        if (playerName == null || playerName.isEmpty()) {
            playerName = "Unknown";
        }

        try (java.io.FileWriter fw = new java.io.FileWriter(filePath, true);
             java.io.BufferedWriter bw = new java.io.BufferedWriter(fw)) {
             
            bw.write("Người chơi: " + playerName);
            bw.newLine();
            bw.write("Đánh giá: " + stars + " sao");
            bw.newLine();
            bw.write("Nội dung: " + comment);
            bw.newLine();
            bw.write("------------------------------------");
            bw.newLine();
            
            JOptionPane.showMessageDialog(this, "Cảm ơn bạn đã gửi phản hồi!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (java.io.IOException e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi lưu phản hồi!", "Thất bại", JOptionPane.ERROR_MESSAGE);
        }
    }
}