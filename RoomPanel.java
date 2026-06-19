package hki2;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class RoomPanel extends JPanel {
    private GameFrame frame;
    private Image bgImage;
    private JLabel lblRoomID;
    private JLabel lblHostStatus, lblGuestStatus;
    private JLabel lblCountdown;
    private JButton btnLeave;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private int roomId;
    private String myRole;
    private boolean isListening = false;
    private String myPlayerName = "Player";

    public RoomPanel(GameFrame frame) {
        this.frame = frame;
        this.setLayout(new BorderLayout());

        bgImage = new ImageIcon("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\9b4b1017e868dee524496f52a4066ef4.jpg").getImage();

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        lblRoomID = new JLabel("PHÒNG CHỜ ID: --", JLabel.CENTER);
        lblRoomID.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblRoomID.setForeground(new Color(222, 255, 154));
        topPanel.add(lblRoomID);
        add(topPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 0, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JPanel hostBox = createPlayerBox("CHỦ PHÒNG");
        lblHostStatus = new JLabel("Đang chờ chủ phòng...", JLabel.CENTER);
        lblHostStatus.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHostStatus.setForeground(Color.WHITE);
        hostBox.add(lblHostStatus, BorderLayout.CENTER);

        JPanel middleBox = new JPanel(new GridBagLayout());
        middleBox.setOpaque(false);
        lblCountdown = new JLabel("ĐANG CHỜ ĐỐI THỦ...", JLabel.CENTER);
        lblCountdown.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblCountdown.setForeground(new Color(255, 159, 67));
        middleBox.add(lblCountdown);

        JPanel guestBox = createPlayerBox("ĐỐI THỦ");
        lblGuestStatus = new JLabel("ĐANG TÌM ĐỐI THỦ...", JLabel.CENTER);
        lblGuestStatus.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblGuestStatus.setForeground(new Color(150, 150, 150));
        guestBox.add(lblGuestStatus, BorderLayout.CENTER);

        centerPanel.add(hostBox);
        centerPanel.add(middleBox);
        centerPanel.add(guestBox);
        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        btnLeave = new JButton("RỜI PHÒNG");
        btnLeave.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLeave.setForeground(Color.WHITE);
        btnLeave.setBackground(new Color(231, 76, 60));
        btnLeave.setFocusPainted(false);
        btnLeave.setPreferredSize(new Dimension(140, 38));
        
        btnLeave.addActionListener(e -> {
            if (out != null) {
                out.println("LEAVE_ROOM");
            }
            isListening = false; 
            frame.showLobby(myPlayerName);
        });
        bottomPanel.add(btnLeave);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void setupRoom(int roomId, String role, Socket sharedSocket, BufferedReader sharedIn, PrintWriter sharedOut, String myRealName) {
        this.roomId = roomId;
        this.myRole = role;
        this.socket = sharedSocket;
        this.in = sharedIn;
        this.out = sharedOut;
        this.myPlayerName = myRealName;

        lblRoomID.setText("PHÒNG CHỜ ID: #" + roomId);
        lblCountdown.setText("ĐANG CHỜ ĐỐI THỦ...");
        lblCountdown.setForeground(new Color(255, 159, 67));

        if (role.equals("HOST")) {
            lblHostStatus.setText(myPlayerName + " (Bạn)");
            lblGuestStatus.setText("ĐANG TÌM ĐỐI THỦ...");
            lblGuestStatus.setForeground(new Color(150, 150, 150));
        } else {
            lblGuestStatus.setText(myPlayerName + " (Bạn)");
            lblHostStatus.setText("Đang kết nối...");
        }
        
        startListening();
    }

    private void startListening() {
        isListening = true;
        new Thread(() -> {
            try {
                String response;
                while (isListening && (response = in.readLine()) != null) {
                    if (response.startsWith("OPPONENT_NAME:")) {
                        String oppName = response.substring(14).trim();
                        SwingUtilities.invokeLater(() -> {
                            if (myRole.equals("HOST")) {
                                lblGuestStatus.setText(oppName);
                                lblGuestStatus.setForeground(Color.WHITE);
                            } else {
                                lblHostStatus.setText(oppName);
                                lblHostStatus.setForeground(Color.WHITE);
                            }
                        });
                    } else if (response.startsWith("COUNTDOWN:")) {
                        String seconds = response.substring(10).trim();
                        SwingUtilities.invokeLater(() -> {
                            lblCountdown.setText("BẮT ĐẦU SAU: " + seconds + "s");
                            lblCountdown.setFont(new Font("Segoe UI", Font.BOLD, 20));
                            lblCountdown.setForeground(new Color(222, 255, 154));
                        });
                    } else if (response.equals("START")) {
                        isListening = false;
                        
                        String finalOppName = myRole.equals("HOST") ? lblGuestStatus.getText().trim() : lblHostStatus.getText().trim();
                        
                        finalOppName = finalOppName.replace(" (Bạn)", "")
                                                   .replace(" (Chủ phòng)", "")
                                                   .replace("Đang kết nối đến Chủ phòng...", "")
                                                   .trim();
                        
                        if (finalOppName.isEmpty() || finalOppName.equals("Đang kết nối...")) {
                            if (myRole.equals("HOST")) {
                                finalOppName = "Player_B"; 
                            } else {
                                finalOppName = "Player_A";
                            }
                        }
                        
                        final String passOppName = finalOppName;
                        
                        SwingUtilities.invokeLater(() -> {
                            frame.showOnlineMatch(socket, in, out, passOppName);
                        });
                        break;
                    } else if (response.equals("OPPONENT_LEFT")) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, 
                                "Đối thủ đã rời khỏi phòng chờ!", 
                                "Thông báo", 
                                JOptionPane.WARNING_MESSAGE);
                            frame.showLobby(myPlayerName); 
                        });
                    } else if (response.equals("JOIN_FAILED")) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Vào phòng thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                            isListening = false;
                            frame.showLobby(frame.getPlayerNameFieldText());
                        });
                        break;
                    }
                }
            } catch (Exception e) {
            }
        }).start();
    }

    private void disconnectRoom() {
        isListening = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            socket = null;
        } catch (IOException e) {}
    }

    private JPanel createPlayerBox(String title) {
        JPanel box = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 30, 45, 180));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2.setColor(new Color(0, 180, 216));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
                g2.dispose();
            }
        };
        box.setOpaque(false);
        JLabel lblTitle = new JLabel(title, JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTitle.setForeground(new Color(0, 180, 216));
        lblTitle.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        box.add(lblTitle, BorderLayout.NORTH);
        return box;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}