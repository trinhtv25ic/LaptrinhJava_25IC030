package hki2;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class LobbyPanel extends JPanel {
    private GameFrame frame;
    private Image bgImage;
    private JTable roomTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;
    private JButton btnCreate, btnRefresh, btnBack;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isRunning = false;
    private String localPlayerName = "Player";

    public LobbyPanel(GameFrame frame) {
        this.frame = frame;
        this.setLayout(new BorderLayout());

        bgImage = new ImageIcon("C:\\Users\\LENOVO\\eclipse-workspace\\oop\\src\\hki2\\9b4b1017e868dee524496f52a4066ef4.jpg").getImage();

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel lblTitle = new JLabel("SẢNH CHỜ ONLINE");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(new Color(222, 255, 154));
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        topContainer.add(lblTitle);
        topContainer.add(Box.createVerticalStrut(15));

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        controlsPanel.setOpaque(false);

        txtSearch = new JTextField(10);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtSearch.setBackground(new Color(20, 30, 40));
        txtSearch.setForeground(Color.WHITE);
        txtSearch.setCaretColor(Color.WHITE);
        txtSearch.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 216), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        txtSearch.setText("Tìm ID...");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void filter() {
                String keyword = txtSearch.getText().trim();
                if (keyword.isEmpty() || keyword.equals("Tìm ID...")) {
                    if (out != null) out.println("GET_ROOMS"); 
                } else {
                    javax.swing.table.TableRowSorter<DefaultTableModel> sorter = new javax.swing.table.TableRowSorter<>(tableModel);
                    roomTable.setRowSorter(sorter);
                    sorter.setRowFilter(javax.swing.RowFilter.regexFilter("^" + keyword + "$", 0)); 
                }
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().equals("Tìm ID...")) {
                    txtSearch.setText("");
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtSearch.getText().isEmpty()) {
                    txtSearch.setText("Tìm ID...");
                    roomTable.setRowSorter(null);
                }
            }
        });

        btnCreate = createNeonButton("TẠO PHÒNG", new Color(46, 196, 182));
        btnRefresh = createNeonButton("LÀM MỚI", new Color(255, 159, 67));

        controlsPanel.add(txtSearch);
        controlsPanel.add(btnCreate);
        controlsPanel.add(btnRefresh);
        
        topContainer.add(controlsPanel);
        add(topContainer, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblListTitle = new JLabel("DANH SÁCH PHÒNG ĐANG TRỐNG", JLabel.CENTER);
        lblListTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblListTitle.setForeground(Color.WHITE);
        lblListTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        centerPanel.add(lblListTitle, BorderLayout.NORTH);

        String[] columns = {"ID PHÒNG", "CHỦ PHÒNG", "TRẠNG THÁI", "THỜI GIAN","HÀNH ĐỘNG",};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roomTable.setRowHeight(38);
        roomTable.setBackground(new Color(15, 23, 42, 220)); 
        roomTable.setForeground(Color.WHITE);
        roomTable.setGridColor(new Color(50, 70, 90));
        roomTable.setSelectionBackground(new Color(222, 255, 154, 60));
        roomTable.setSelectionForeground(Color.WHITE);

        JTableHeader tableHeader = roomTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(30, 41, 59));
        tableHeader.setForeground(new Color(222, 255, 154));
        tableHeader.setPreferredSize(new Dimension(100, 35));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < roomTable.getColumnCount(); i++) {
            roomTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.getViewport().setBackground(new Color(10, 15, 30, 150));
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(50, 70, 90), 1));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 15));
        footerPanel.setOpaque(false);
        btnBack = createNeonButton("TRỜI VỀ MENU", new Color(231, 76, 60));
        btnBack.setPreferredSize(new Dimension(160, 38));
        footerPanel.add(btnBack);
        add(footerPanel, BorderLayout.SOUTH);

        btnCreate.addActionListener(e -> {
            String[] timeOptions = {"1 phút (60s)", "3 phút (180s)", "5 phút (300s)"};   
            String selectedTime = (String) JOptionPane.showInputDialog(
                    this,
                    "Vui lòng chọn thời gian thi đấu cho phòng:",
                    "Cài Đặt Phòng Chơi",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    timeOptions,
                    timeOptions[0]
            );
            if (selectedTime != null) {
                int minutes = 1;
                if (selectedTime.contains("3 phút")) minutes = 3;
                if (selectedTime.contains("5 phút")) minutes = 5;
                if (out != null) {
                    out.println("CREATE_ROOM:" + minutes);
                    out.flush(); 
                    System.out.println("-> CLIENT: Đã gửi lệnh [CREATE_ROOM:" + minutes + "] lên Server.");
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể kết nối đến Server!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnRefresh.addActionListener(e -> {
            if (out != null) out.println("GET_ROOMS");
        });
        
        btnBack.addActionListener(e -> {
        	if (out != null) {
        	    out.println("LEAVE_ROOM");
        	}
            disconnectLobby();
            frame.showMenu();
        });

        roomTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = roomTable.getSelectedRow();
                    if (row != -1) {
                        String status = roomTable.getValueAt(row, 2).toString();
                        if (status.contains("Trong trận") || status.contains("🔴")) {
                            JOptionPane.showMessageDialog(LobbyPanel.this, 
                                "Trận đấu đã bắt đầu, bạn không thể vào!", 
                                "Thông báo", 
                                JOptionPane.WARNING_MESSAGE);
                            return; 
                        }
                        
                        int roomId = Integer.parseInt(roomTable.getValueAt(row, 0).toString()); 
                        if (out != null) {
                            out.println("JOIN_ROOM:" + roomId);
                            out.flush();
                        }
                    }
                }
            }
        });
    }

    public void connectToLobby(String realName) {
        this.localPlayerName = realName;
        if (isRunning) return;
        isRunning = true;
        new Thread(() -> {
            try {
                if (socket == null || socket.isClosed()) {
                    socket = new Socket("localhost", 12345);
                    out = new PrintWriter(socket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    out.println("NAME:" + localPlayerName);
                }
                out.println("GET_ROOMS");

                String response;
                while (isRunning && (response = in.readLine()) != null) {
                    if (response.startsWith("ROOM_LIST:")) {
                        updateRoomTable(response.substring(10));
                    } 
                    else if (response.startsWith("ROOM_CREATED:")) {
                        System.out.println("-> Client đã nhận được lệnh tạo phòng thành công từ Server: " + response);
                        int createdId = Integer.parseInt(response.substring(13).trim());
                        
                        isRunning = false; 
                        
                        SwingUtilities.invokeLater(() -> {
                            frame.showRoom(createdId, "HOST", socket, in, out, localPlayerName); 
                        });
                    }
                    else if (response.startsWith("JOIN_SUCCESS:")) {
                        int joinedId = Integer.parseInt(response.substring(13).trim());
                        
                        isRunning = false; 	
                        
                        SwingUtilities.invokeLater(() -> {
                            frame.showRoom(joinedId, "GUEST", socket, in, out, localPlayerName); 
                        });
       
                    }
                    else if (response.equals("JOIN_FAILED")) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Phòng này đã đầy hoặc đang trong trận đấu!", "Không thể tham gia", JOptionPane.WARNING_MESSAGE);
                        });
                    }
                }
            } catch (Exception e) {
            }
        }).start();
    }

    private void updateRoomTable(String data) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);
            if (data.trim().isEmpty()) return;
            
            String[] rooms = data.split("\\|"); 
            
            for (String roomStr : rooms) {
                if (roomStr.trim().isEmpty()) continue;
                
                String[] info = roomStr.split(",");
                
                if (info.length == 4) { 
                    int id = Integer.parseInt(info[0]);
                    String hostName = info[1];
                    String status = info[2];
                    String duration = info[3]; 
                    String displayStatus;
                    String actionText;
                    if ("Playing".equalsIgnoreCase(status)) {
                        displayStatus = "🔴 Trong trận";
                        actionText = "Đầy"; 
                    } else {
                        displayStatus = "🟢 Trống";
                        actionText = "Vào";
                    }
                    
                    tableModel.addRow(new Object[]{id, hostName, displayStatus, duration, actionText});
                }
            }
        });
    }

    private void disconnectLobby() {
        isRunning = false;
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
            socket = null;
        } catch (IOException e) {}
    }

    private JButton createNeonButton(String text, Color baseColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(baseColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(baseColor.brighter());
                } else {
                    g2.setColor(baseColor);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(110, 35));
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null) {
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}