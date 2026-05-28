package hki2;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private CardLayout card;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;       
    private RankPanel rankPanel;       
    private GameOverPanel gameOverPanel;
    private String currentFramePlayerName = "Player";
    private JPanel lobbyPanel;    
    private JPanel roomPanel;      
    private JPanel onlinePanel;
    public GameFrame() {
        setTitle("BLOCK PUZZLE");
        setSize(600, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        card = new CardLayout();
        getContentPane().setLayout(card);

        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel(this);
        rankPanel = new RankPanel(this);

        getContentPane().add(menuPanel, "menu");
        getContentPane().add(gamePanel, "game");
        getContentPane().add(rankPanel, "rank");
    }

    public void showMenu() {
        card.show(getContentPane(), "menu");
    }

    public void showGame() {
        card.show(getContentPane(), "game");
    }

    public void showRank() {
        card.show(getContentPane(), "rank");
    }

    public void showGameOver(int score) {
        if (gameOverPanel != null) {
            getContentPane().remove(gameOverPanel);
        }

        gameOverPanel = new GameOverPanel(this, score);

        getContentPane().add(gameOverPanel, "gameover");

        getContentPane().revalidate();
        getContentPane().repaint();

        card.show(getContentPane(), "gameover"); 
    }

    public void showGameOver() {
        showGameOver(0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameFrame frame = new GameFrame();
            frame.setVisible(true);
            frame.showMenu();
        });
    }
    public void showLobby(String name) {
        this.currentFramePlayerName = name;
        
        try {
            lobbyPanel = new LobbyPanel(this);
            getContentPane().add(lobbyPanel, "lobby");
            
            ((LobbyPanel) lobbyPanel).connectToLobby(name);
            
            card.show(getContentPane(), "lobby");
            getContentPane().revalidate();
            getContentPane().repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showRoom(int roomId, String role, java.net.Socket sharedSocket, java.io.BufferedReader sharedIn, java.io.PrintWriter sharedOut, String myRealName) {
        try {
            roomPanel = new RoomPanel(this);
            getContentPane().add(roomPanel, "room");
            
            ((RoomPanel) roomPanel).setupRoom(roomId, role, sharedSocket, sharedIn, sharedOut, myRealName);
            
            card.show(getContentPane(), "room");
            getContentPane().revalidate();
            getContentPane().repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showOnlineMatch(java.net.Socket existingSocket, java.io.BufferedReader existingIn, java.io.PrintWriter existingOut, String oppName) {
        try {
            onlinePanel = new OnlinePanel(this);
            getContentPane().add(onlinePanel, "onlineMatch");
            
            ((OnlinePanel) onlinePanel).startMatchWithSocket(existingSocket, existingIn, existingOut, oppName);
            
            card.show(getContentPane(), "onlineMatch");
            getContentPane().revalidate();
            getContentPane().repaint();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getPlayerNameFieldText() {
        return this.currentFramePlayerName;
    }
}