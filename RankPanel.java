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
        
        setBackground(Color.BLACK);

        JLabel titleLabel = new JLabel("RANKING", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 42));
        titleLabel.setForeground(new Color(255, 215, 0)); 
        titleLabel.setBounds(150, 40, 300, 50);
        add(titleLabel);

        rankArea = new JTextArea();
        rankArea.setFont(new Font("Monospaced", Font.BOLD, 22));
        rankArea.setForeground(Color.WHITE); 
        rankArea.setBackground(new Color(30, 30, 30)); 
        rankArea.setEditable(false);
        rankArea.setMargin(new Insets(15, 15, 15, 15)); 
        
        JScrollPane scrollPane = new JScrollPane(rankArea);
        scrollPane.setBounds(80, 190, 440, 320);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
        add(scrollPane);

        JButton btnAll = new JButton("All-Time");
        JButton btnWeek = new JButton("Week");
        JButton btnDay = new JButton("Day");

        btnAll.setBounds(80, 130, 130, 40);
        btnWeek.setBounds(235, 130, 130, 40);
        btnDay.setBounds(390, 130, 130, 40);
        
        styleButton(btnAll);
        styleButton(btnWeek);
        styleButton(btnDay);

        add(btnAll);
        add(btnWeek);
        add(btnDay);

        JButton btnBack = new JButton("BACK TO MENU");
        btnBack.setFont(new Font("Arial", Font.BOLD, 18));
        btnBack.setBounds(200, 560, 200, 50);
        styleButton(btnBack);
        btnBack.setBackground(new Color(200, 0, 0)); 
        add(btnBack);

        btnAll.addActionListener(e -> displayRank("all"));
        btnWeek.addActionListener(e -> displayRank("week"));
        btnDay.addActionListener(e -> displayRank("day"));

        btnBack.addActionListener(e -> frame.showMenu());

        displayRank("all");
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(50, 50, 50));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    }

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
    }
}