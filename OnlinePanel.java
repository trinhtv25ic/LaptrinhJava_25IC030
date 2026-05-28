package hki2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class OnlinePanel extends JPanel {
    private GameFrame frame;
    private Board board = new Board();
    private int opponentScore = 0;
    private JButton btnHome;
    private Block[] blocks = new Block[3];
    private ArrayList<ScoreEffect> effects = new ArrayList<>();
    private int selectedIndex = 0;
    private int score = 0;
    private int combo = 0;
    private int comboX = 500;
    private int comboSize = 20;
    private int comboTime = 0;
    private double comboSpeed = 15;
    private boolean gameOver = false;
    private final int SIZE = 10;
    private final int CELL = 40;
    private final int BOARD_X = 90;
    private final int BOARD_Y = 150;
    private int mouseX, mouseY;
    private boolean dragging = false;
    private Image comboBg = new ImageIcon("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\X-removebg-preview.png").getImage();
    private Image bg = new ImageIcon("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\Block Puzzle3.png").getImage();
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String timeString = "60s";
    private String opponentName = "Đang tìm...";
    private boolean isSearching = true;
    
    public OnlinePanel() {
    }

    public OnlinePanel(GameFrame frame) {
        this.frame = frame;
        this.setLayout(null);
        btnHome = new JButton("HOME");
        btnHome.setFont(new Font("Arial", Font.BOLD, 16));
        btnHome.setBounds(430, 45, 100, 40);
        btnHome.addActionListener(e -> {
            disconnect();
            SwingUtilities.invokeLater(() -> frame.showMenu());
        });
        btnHome.setOpaque(false);
        btnHome.setContentAreaFilled(false);
        btnHome.setBorderPainted(false);
        btnHome.setText("");
        this.add(btnHome);

        for (int i = 0; i < 3; i++) {
            blocks[i] = blockFactory.randomBlock();
        }

        Timer comboTimer = new Timer(10, e -> {
            if (comboX > 130) { comboX -= comboSpeed; }
            if (comboSize > 35) { comboSize--; }
            if (comboSpeed > 0.5) { comboSpeed -= 0.03; }
            if (comboTime > 0) { comboTime--; }
            for (int i = 0; i < effects.size(); i++) {
                effects.get(i).update();
                if (effects.get(i).isDone()) { effects.remove(i); i--; }
            }
            repaint();
        });
        comboTimer.start();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (gameOver || isSearching) return;
                int startX = 65;
                int startY = 630;
                for (int i = 0; i < 3; i++) {
                    Rectangle r = new Rectangle(startX + i * 170, startY, 120, 120);
                    if (r.contains(e.getX(), e.getY())) {
                        selectedIndex = i;
                        dragging = true;
                        mouseX = e.getX();
                        mouseY = e.getY();
                        repaint();
                        return;
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if (gameOver || isSearching) return;
                dragging = false;
                Block currentBlock = blocks[selectedIndex];
                int col = (e.getX() - BOARD_X) / CELL;
                int row = (e.getY() - BOARD_Y) / CELL;
                if (board.canPlace(currentBlock, row, col)) {
                    board.placeBlock(currentBlock, row, col);
                    Sound.playEffect("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\astonishment.wav");
                    int cleared = board.clearLines();
                    if (cleared > 0) {
                        Sound.playEffect("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\a-box-with-a-bonus-appeared-in-the-hand-or-somewhere-in-the-corner.wav");
                        combo++;
                        comboX = 500; comboSpeed = 15; comboSize = 60; comboTime = 90;
                        int addedScore = (int) (cleared * (10 * Math.pow(2, combo - 1)));
                        score += addedScore;
                        sendScoreToServer();
                        effects.add(new ScoreEffect(300, 250, addedScore));
                    } else {
                        combo = 0;
                    }
                    blocks[selectedIndex] = blockFactory.randomBlock();
                }
                repaint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (gameOver || isSearching) return;
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        });
    }

    public void startMatchWithSocket(Socket existingSocket, BufferedReader existingIn, PrintWriter existingOut, String oppName) {
        this.socket = existingSocket;
        this.in = existingIn;
        this.out = existingOut;
        this.opponentName = oppName; 
        this.isSearching = false;
        this.gameOver = false;
        this.score = 0;
        this.opponentScore = 0;
        this.timeString = "60s";
        
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                board.setCell(i, j, null);
            }
        }
        
        for (int i = 0; i < 3; i++) {
            blocks[i] = blockFactory.randomBlock();
        }

        repaint();
        listenToServerGameEvents();
    }

    private void listenToServerGameEvents() {
        new Thread(() -> {
            try {
                String response;
                while (!gameOver && (response = in.readLine()) != null) {
                    if (response.startsWith("TIME:")) {
                        timeString = response.substring(5).trim() + "s";
                        repaint();
                    } else if (response.startsWith("OPPONENT_SCORE:")) {
                        opponentScore = Integer.parseInt(response.substring(15).trim());
                        repaint();
                    } else if (response.startsWith("END_RESULT:")) {
                        gameOver = true;
                        String[] parts = response.split(":");
                        String status = parts[1];
                        String myFinalName = parts[2];
                        int myFinalScore = Integer.parseInt(parts[3]);
                        String oppFinalName = parts[4];
                        int oppFinalScore = Integer.parseInt(parts[5]);
                        handleGameEnd(status, myFinalName, myFinalScore, oppFinalName, oppFinalScore);
                        break;
                    }
                }
            } catch (IOException e) {
            }
        }).start();
    }

    private void handleGameEnd(String status, String myFinalName, int myFinalScore, String oppFinalName, int oppFinalScore) {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Kết Quả Trận Đấu", true);
            dialog.setUndecorated(true); 
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this); 

            JPanel contentPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    GradientPaint gp = new GradientPaint(0, 0, new Color(20, 24, 42), 0, getHeight(), new Color(10, 12, 22));
                    g2.setPaint(gp);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    
                    if (status.equals("WIN")) g2.setColor(new Color(46, 204, 113));      
                    else if (status.equals("LOSE")) g2.setColor(new Color(231, 76, 60)); 
                    else g2.setColor(new Color(241, 196, 15));                           
                    
                    g2.setStroke(new BasicStroke(3f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
                    g2.dispose();
                }
            };
            contentPanel.setLayout(null);
            dialog.setContentPane(contentPanel);

            JLabel lblTitle = new JLabel("", JLabel.CENTER);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
            lblTitle.setBounds(0, 25, 400, 50);
            
            if (status.equals("WIN")) {
                lblTitle.setText("CHIẾN THẮNG!");
                lblTitle.setForeground(new Color(46, 204, 113));
            } else if (status.equals("LOSE")) {
                lblTitle.setText("THẤT BẠI!");
                lblTitle.setForeground(new Color(231, 76, 60));
            } else {
                lblTitle.setText("HÒA CỜ!");
                lblTitle.setForeground(new Color(241, 196, 15));
            }
            contentPanel.add(lblTitle);

            JLabel lblMyScore = new JLabel(myFinalName + " (Bạn): " + myFinalScore + " điểm", JLabel.CENTER);
            lblMyScore.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblMyScore.setForeground(Color.WHITE);
            lblMyScore.setBounds(0, 100, 400, 30);
            contentPanel.add(lblMyScore);

            JLabel lblOppScore = new JLabel("Đối thủ: " + oppFinalScore + " điểm", JLabel.CENTER);
            lblOppScore.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblOppScore.setForeground(new Color(189, 195, 199));
            lblOppScore.setBounds(0, 140, 400, 30);
            contentPanel.add(lblOppScore);

            JButton btnOk = new JButton("XÁC NHẬN") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(new Color(52, 152, 219)); 
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            btnOk.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnOk.setForeground(Color.WHITE);
            btnOk.setBounds(130, 210, 140, 40);
            btnOk.setFocusPainted(false);
            btnOk.setContentAreaFilled(false);
            btnOk.setBorderPainted(false);
            
            btnOk.addActionListener(e -> {
                dialog.dispose();
                disconnect();
                frame.showLobby(frame.getPlayerNameFieldText());
            });
            contentPanel.add(btnOk);

            dialog.setVisible(true);
        });
    }

    private void sendScoreToServer() {
        if (out != null) {
            out.println("SCORE:" + score);
            out.flush(); 
        }
    }

    private void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g.drawImage(bg, 0, 0, getWidth(), getHeight(), null);
        if (isSearching) {
            g2.setColor(new Color(0, 0, 0, 200));
            g2.fillRect(0, 0, getWidth(), getHeight());
            g2.setFont(new Font("Arial", Font.BOLD, 26));
            g2.setColor(Color.WHITE);
            g2.drawString("ĐANG TÌM ĐỐI THỦ...", 160, 380);
            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.drawString("Vui lòng đợi người chơi thứ 2 kết nối", 160, 420);
            return;
        }
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int x = BOARD_X + j * CELL;
                int y = BOARD_Y + i * CELL;
                GradientPaint boardGp = new GradientPaint(x, y, new Color(20, 20, 20, 180), x, y + CELL, new Color(60, 60, 60, 180));
                g2.setPaint(boardGp);
                g2.fillRoundRect(x, y, CELL - 2, CELL - 2, 10, 10);
                g2.setColor(new Color(255, 255, 255, 120));
                g2.drawRoundRect(x, y, CELL - 2, CELL - 2, 10, 10);
                Color c = board.getCell(i, j);
                if (c != null) { drawBlock(g2, x, y, c); }
            }
        }
        g2.setColor(new Color(0, 0, 0, 120));
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                g2.drawRoundRect(BOARD_X + j * CELL, BOARD_Y + i * CELL, CELL, CELL, 8, 8);
            }
        }
        if (dragging) {
            Block currentBlock = blocks[selectedIndex];
            int col = (mouseX - BOARD_X) / CELL;
            int row = (mouseY - BOARD_Y) / CELL;
            if (board.canPlace(currentBlock, row, col)) {
                g.setColor(new Color(0, 255, 0, 100));
                for (int i = 0; i < currentBlock.getHeight(); i++) {
                    for (int j = 0; j < currentBlock.getWidth(); j++) {
                        if (currentBlock.getCell(i, j) == 1) {
                            g.fillRect(BOARD_X + (col + j) * CELL, BOARD_Y + (row + i) * CELL, CELL, CELL);
                        }
                    }
                }
            }
            g.setColor(currentBlock.getColor());
            for (int i = 0; i < currentBlock.getHeight(); i++) {
                for (int j = 0; j < currentBlock.getWidth(); j++) {
                    if (currentBlock.getCell(i, j) == 1) {
                        drawBlock(g2, mouseX + j * CELL, mouseY + i * CELL, currentBlock.getColor());
                        g.setColor(currentBlock.getColor());
                    }
                }
            }
        }
        int startX = 65;
        int startY = 630;
        for (int k = 0; k < 3; k++) {
            Block b = blocks[k];
            for (int i = 0; i < b.getHeight(); i++) {
                for (int j = 0; j < b.getWidth(); j++) {
                    if (b.getCell(i, j) == 1) { drawBlock(g2, startX + k * 170 + j * CELL, startY + i * CELL, b.getColor()); }
                }
            }
            if (k == selectedIndex) {
                g.setColor(Color.YELLOW);
                g.drawRect(startX + k * 170 - 5, startY - 5, 90, 70);
            }
        }
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.BLACK);
        g.drawString("" + score, 318, 73);
        g.drawString("" + score, 322, 73);
        g.drawString("" + score, 318, 77);
        g.drawString("" + score, 322, 77);
        g.setColor(new Color(255, 215, 0));
        g.drawString("" + score, 320, 75);
        
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.BLACK);
        String oppStr = "ĐỐI THỦ: " + opponentScore;
        g.drawString(oppStr, 98, 74);
        g.drawString(oppStr, 102, 74);
        g.drawString(oppStr, 98, 76);
        g.drawString(oppStr, 102, 76);
        g.setColor(Color.ORANGE);
        g.drawString(oppStr, 100, 75);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.setColor(Color.BLACK);
        g.drawString("TIME: " + timeString, 230, 124);
        g.setColor(Color.WHITE);
        g.drawString("TIME: " + timeString, 230, 125);
        if (combo > 0 && comboTime > 0) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.drawImage(comboBg, comboX, 430, 350, 370, null);
            g2.setFont(new Font("Jokerman", Font.BOLD, 100));
            g2.setColor(new Color(255, 0, 255, 80));
            g2.drawString("" + combo, comboX + 305, 530);
            g2.setColor(new Color(0, 255, 255, 80));
            g2.drawString("" + combo, comboX + 305, 530);
            g2.setColor(Color.BLACK);
            g2.drawString("" + combo, comboX + 300, 530);
            g2.setColor(Color.magenta);
            g2.drawString("" + combo, comboX + 300, 530);
        }
        for (ScoreEffect s : effects) { s.draw(g2); }
    }

    private void drawBlock(Graphics2D g2, int x, int y, Color color) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fillRoundRect(x + 3, y + 3, CELL - 2, CELL - 2, 12, 12);
        GradientPaint gp = new GradientPaint(x, y, color.brighter(), x, y + CELL, color.darker());
        g2.setPaint(gp);
        g2.fillRoundRect(x, y, CELL - 2, CELL - 2, 12, 12);
        g2.setColor(new Color(255, 255, 255, 120));
        g2.drawRoundRect(x, y, CELL - 2, CELL - 2, 12, 12);
        g2.setColor(new Color(255, 255, 255, 60));
        g2.fillRoundRect(x + 4, y + 4, CELL - 12, 8, 8, 8);
    }
}