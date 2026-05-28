package hki2;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RankPanel extends JPanel {
    private GameFrame frame;
    private JTextArea rankArea;

    public RankPanel(GameFrame frame) {
        this.frame = frame;
        setLayout(null);
        
        // ĐẶT NỀN TOÀN BỘ PANEL MÀU ĐEN
        setBackground(Color.BLACK);

        // Tiêu đề Panel (Chữ Vàng nổi trên nền đen)
        JLabel titleLabel = new JLabel("RANKING", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setForeground(new Color(255, 215, 0)); // Màu vàng Gold
        titleLabel.setBounds(150, 40, 300, 50);
        add(titleLabel);

        // Khu vực hiển thị bảng điểm
        rankArea = new JTextArea();
        rankArea.setFont(new Font("Monospaced", Font.BOLD, 22));
        rankArea.setForeground(Color.WHITE); // Chữ trắng nổi bần bật
        rankArea.setBackground(new Color(30, 30, 30)); // Nền xám đậm bên trong khung chữ
        rankArea.setEditable(false);
        rankArea.setMargin(new Insets(15, 15, 15, 15)); // Tạo khoảng cách viền cho chữ dễ nhìn
        
        JScrollPane scrollPane = new JScrollPane(rankArea);
        scrollPane.setBounds(80, 190, 440, 320);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2)); // Viền vàng hộp điểm
        add(scrollPane);

        // 3 Nút chuyển đổi chế độ Xem Rank (Thiết kế lại cho hợp nền đen)
        JButton btnAll = new JButton("All-Time");
        JButton btnWeek = new JButton("Week");
        JButton btnDay = new JButton("Day");

        btnAll.setBounds(80, 130, 130, 40);
        btnWeek.setBounds(235, 130, 130, 40);
        btnDay.setBounds(390, 130, 130, 40);
        
        // Custom nhẹ giao diện nút cho ngầu
        styleButton(btnAll);
        styleButton(btnWeek);
        styleButton(btnDay);

        add(btnAll);
        add(btnWeek);
        add(btnDay);

        // Nút BACK về Menu
        JButton btnBack = new JButton("BACK TO MENU");
        btnBack.setFont(new Font("Arial", Font.BOLD, 18));
        btnBack.setBounds(200, 560, 200, 50);
        styleButton(btnBack);
        btnBack.setBackground(new Color(200, 0, 0)); // Riêng nút Back cho màu đỏ cho nổi
        add(btnBack);

        // Sự kiện cho các nút lọc điểm
        btnAll.addActionListener(e -> displayRank("all"));
        btnWeek.addActionListener(e -> displayRank("week"));
        btnDay.addActionListener(e -> displayRank("day"));

        // Sự kiện nút Back
        btnBack.addActionListener(e -> frame.showMenu());

        // Mặc định vừa mở lên sẽ hiện Rank All-Time
        displayRank("all");
    }

    // Hàm phụ trợ giúp nút bấm nhìn đẹp hơn trên nền đen
    private void styleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(50, 50, 50));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    }

    // Hàm cập nhật chữ hiển thị lên bảng điểm
    public void displayRank(String type) {
        List<RankManager.RankEntry> top5 = RankManager.getTop5(type);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(" %-4s | %-14s | %-6s\n", "TOP", "NAME", "SCORE"));
        sb.append("-----------------------------------\n");
        
        int rank = 1;
        for (RankManager.RankEntry entry : top5) {
            sb.append(String.format("  #%d  | %-14s | %-6d\n", rank++, entry.name, entry.score));
        }
        
        if (top5.isEmpty()) {
            sb.append("\n\n     No data available yet!");
        }
        
        rankArea.setText(sb.toString());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Không vẽ lại hình nền cũ nữa, để mặc định tô màu đen từ setBackground(Color.BLACK)
    }
}