package hki2;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
public class GamePanel extends JPanel {
	private GameFrame frame;
    private Board board =new Board();
    private int highScore=0;
    private JButton btnHome;
    private Block[] blocks =new Block[3];
    private ArrayList<ScoreEffect> effects = new ArrayList<>();
    private ImageIcon gameOverGif =new ImageIcon("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\Block Puzzle.gif");
    private int selectedIndex = 0;
    private int score =0;
    private int combo=0;
    private int comboX = 500;
    private int comboSize = 20;
    private int comboTime = 0;
    private double comboSpeed = 15;
    private boolean gameOver =false;
    private final int SIZE =10;
    private final int CELL =40;
    private final int BOARD_X =90;
    private final int BOARD_Y =150;
    private int mouseX, mouseY;
    private boolean dragging =false;
    private Image comboBg = new ImageIcon("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\X-removebg-preview.png").getImage();
    private Image bg = new ImageIcon("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\Block Puzzle3.png").getImage();
    
    public GamePanel() {
    	
    }
    public GamePanel(GameFrame frame) {
    	this.frame=frame;
    	this.setLayout(null); 
    	java.util.List<RankManager.RankEntry> allTimeRank = RankManager.getTop5("all");
        if (!allTimeRank.isEmpty()) {
            this.highScore = allTimeRank.get(0).score; 
        } else {
            this.highScore = 0; 
        }
        btnHome = new JButton("HOME");
        btnHome.setFont(new Font("Arial", Font.BOLD, 16));
        btnHome.setForeground(Color.WHITE);
        btnHome.setBackground(new Color(50, 50, 50));
        btnHome.setFocusPainted(false);
        btnHome.setBounds(430, 45, 100, 40);
        btnHome.addActionListener(e -> handleHomeClick());
        btnHome.setOpaque(false);
		btnHome.setContentAreaFilled(false);
		btnHome.setBorderPainted(false);
		btnHome.setText("");
        this.add(btnHome);
        
        for (int i=0;i<3;i++) {
            blocks[i] =blockFactory.randomBlock();
        }
        Timer comboTimer = new Timer(10, e -> {
        	if (comboX>130) {
        	    comboX -=comboSpeed;
        	}
            if (comboSize>35) {
                comboSize--;
            }
            if (comboSpeed>0.5) {
                comboSpeed-=0.03;
            }
            if (comboTime>0) {
                comboTime--;
            }
            for (int i = 0; i < effects.size(); i++) {

                effects.get(i).update();

                if (effects.get(i).isDone()) {

                    effects.remove(i);

                    i--;
                }
            }
            repaint();
        });
        comboTimer.start();
        addMouseListener(new MouseAdapter() {
        	@Override
        	public void mousePressed(MouseEvent e) {
        	    int startX =65;
        	    int startY =630;
        	    for (int i=0;i<3;i++) {
        	        Rectangle r=new Rectangle(startX + i * 170,startY,120,120);
        	        if (r.contains(e.getX(),e.getY())) {
        	            selectedIndex =i;
        	            dragging =true;
        	            mouseX =e.getX();
        	            mouseY =e.getY();
        	            repaint();
        	            return;
        	        }
        	    }
        	}
            @Override
            public void mouseReleased(MouseEvent e) {
                dragging =false;
                Block currentBlock=blocks[selectedIndex];
                int col = (e.getX()-BOARD_X)/CELL;
                int row = (e.getY()-BOARD_Y)/CELL;
                if (!gameOver && board.canPlace(currentBlock,row,col)) {
                    board.placeBlock(currentBlock,row,col);
                    Sound.playEffect("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\astonishment.wav");
                    int cleared =board.clearLines();
                    if (cleared > 0) {
                    	Sound.playEffect("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\a-box-with-a-bonus-appeared-in-the-hand-or-somewhere-in-the-corner.wav");
                        combo++;
                        comboX =500;
                        comboSpeed=15;
                        comboSize =60;
                        comboTime=90;
                        int addedScore =(int)(cleared * (10 * Math.pow(2, combo - 1)));
                        score += addedScore;
                        
                        effects.add(new ScoreEffect(300,250,addedScore)
                        );
                    } else {
                        combo = 0;
                    }
                    if (score>highScore) {
                        highScore=score; 
                    }
                    blocks[selectedIndex]=blockFactory.randomBlock();
                    boolean ok=false;
                    for (int i=0;i<3;i++) {
                        if(board.canPlaceAnywhere(blocks[i])) {
                            ok =true;
                            break;
                        }
                    }
                    if (!ok) {
                    	String playerName = PlayerData.getPlayerName();
                    	RankManager.saveScore(playerName, score);
                        gameOver = true;
                        Sound.stopMusic();
                        Sound.playEffect(
                            "C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\sad-music-from-naruto.wav"
                        );
                        frame.showGameOver(score);
                    }
                }
                repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                int startX =45;
                int startY =700;
                for (int i=0; i<3;i++) {
                    Rectangle r=new Rectangle(startX + i * 120,startY,90,70);
                    if (r.contains(e.getX(),e.getY())) {
                        selectedIndex =i;
                        repaint();
                        return;
                    }
                }
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                mouseX =e.getX();
                mouseY =e.getY();
                repaint();
            }
        });
    }
    private void handleHomeClick() {
        String[] options = {"Dừng lại & Lưu điểm", "Chơi tiếp", "Quay về Menu"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Trò chơi đang tạm dừng. Bạn muốn làm gì?",
                "Game Paused",                            
                JOptionPane.YES_NO_CANCEL_OPTION,       
                JOptionPane.QUESTION_MESSAGE,           
                null,                                   
                options,                                 
                options[1]                               
        );

        
        if (choice == 0) { 
            String currentPlayerName = PlayerData.getPlayerName();
            if (currentPlayerName == null || currentPlayerName.isEmpty()) {
                currentPlayerName = "Unknown";
            }
            RankManager.saveScore(currentPlayerName, this.score); 
            frame.showMenu();
            
        } else if (choice == 1 || choice == JOptionPane.CLOSED_OPTION) {

            return; 
            
        } else if (choice == 2) {
            frame.showMenu();
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g.drawImage(bg,0,0,getWidth(),getHeight(),null);
        for (int i = 0; i < SIZE; i++) {

            for (int j = 0; j < SIZE; j++) {

                int x = BOARD_X + j * CELL;
                int y = BOARD_Y + i * CELL;

                GradientPaint boardGp = new GradientPaint(
                        x,
                        y,
                        new Color(20,20,20,180),

                        x,
                        y + CELL,
                        new Color(60,60,60,180)
                );

                g2.setPaint(boardGp);

                g2.fillRoundRect(
                        x,
                        y,
                        CELL - 2,
                        CELL - 2,
                        10,
                        10
                );

                g2.setColor(new Color(255,255,255,120));

                g2.drawRoundRect(
                        x,
                        y,
                        CELL - 2,
                        CELL - 2,
                        10,
                        10
                );

                Color c = board.getCell(i,j);

                if (c != null) {

                    drawBlock(g2, x, y, c);
                }
            }
        }
        g2.setColor(new Color(0,0,0,120));

        for (int i = 0; i < SIZE; i++) {

            for (int j = 0; j < SIZE; j++) {

                g2.drawRoundRect(
                        BOARD_X + j * CELL,
                        BOARD_Y + i * CELL,
                        CELL,
                        CELL,
                        8,
                        8
                );
            }
        }
        if (dragging) {
            Block currentBlock=blocks[selectedIndex];
            int col=(mouseX -BOARD_X)/CELL;
            int row=(mouseY -BOARD_Y)/CELL;
            if (board.canPlace(currentBlock,row,col)) {
                g.setColor(new Color(0,255,0,100));
                for (int i =0;i<currentBlock.getHeight(); i++) {
                    for (int j =0;j<currentBlock.getWidth(); j++) {
                        if (currentBlock.getCell(i,j)==1) {
                            g.fillRect(BOARD_X +(col + j) * CELL,BOARD_Y + (row + i) * CELL,CELL, CELL);
                        }
                    }
                }
            }
            g.setColor(currentBlock.getColor());
            for (int i=0;i<currentBlock.getHeight();i++) {
                for (int j =0;j<currentBlock.getWidth();j++) {
                    if (currentBlock.getCell(i,j)==1) {
                    	drawBlock(
                    	        g2,
                    	        mouseX + j * CELL,
                    	        mouseY + i * CELL,
                    	        currentBlock.getColor()
                    	);
                        g.setColor(currentBlock.getColor());
                    }
                }
            }
        }
        int startX = 65;
        int startY = 630;
        for (int k=0;k<3;k++) {
            Block b = blocks[k];
            for(int i=0;i<b.getHeight();i++) {
                for(int j=0;j<b.getWidth();j++) {
                    if (b.getCell(i,j)==1) {
                    	drawBlock(
                    	        g2,
                    	        startX + k * 170 + j * CELL,
                    	        startY + i * CELL,
                    	        b.getColor()
                    	);
                    }
                }
            }
            if (k==selectedIndex) {
                g.setColor(Color.YELLOW);
                g.drawRect(startX+k*170-5,startY-5, 90,70);
            }
        }
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.setColor(Color.BLACK);
        g.drawString("" + score, 318, 73);
        g.drawString("" + score, 322, 73);
        g.drawString("" + score, 318, 77);
        g.drawString("" + score, 322, 77);
        g.setColor(new Color(255,215,0));
        g.drawString("" + score, 320, 75);
        
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.setColor(Color.BLACK); 
        g.drawString("" + highScore, 118, 74);
        g.drawString("" + highScore, 122, 74);
        g.drawString("" + highScore, 118, 76);
        g.drawString("" + highScore, 122, 76);
        g.setColor(Color.ORANGE); 
        g.drawString("" + highScore, 120, 75);
        if (combo > 0 && comboTime > 0) {
            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );
            g2.drawImage(comboBg, comboX, 430, 350, 370, null);
            g2.setFont(new Font("Jokerman", Font.BOLD, 70));
            g2.setFont(new Font("Jokerman", Font.BOLD, 100));
            g2.setColor(new Color(255,0,255,80));
            g2.drawString("" + combo, comboX + 305, 530);
            g2.setColor(new Color(0,255,255,80));
            g2.drawString("" + combo, comboX + 305, 530);
            g2.setColor(Color.BLACK);
            g2.drawString("" + combo, comboX + 300, 530);
            g2.setColor(Color.magenta);
            g2.drawString("" + combo, comboX + 300, 530);
        }
        for (ScoreEffect s : effects) {
            s.draw(g2);
        }
    }
    private void drawBlock(Graphics2D g2, int x, int y, Color color) {
        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2.setColor(new Color(0,0,0,80));
        g2.fillRoundRect(x + 3,y + 3,CELL - 2,CELL - 2,12,12);
        GradientPaint gp = new GradientPaint(x,y,color.brighter(),x,y + CELL,color.darker());
        g2.setPaint(gp);
        g2.fillRoundRect(x,y,CELL - 2, CELL - 2,12,12);
        g2.setColor(new Color(255,255,255,120));
        g2.drawRoundRect(x,y,CELL - 2,CELL - 2,12,12 );
        g2.setColor(new Color(255,255,255,60));
        g2.fillRoundRect(x + 4,y + 4,CELL - 12,8,8, 8);
    }
}