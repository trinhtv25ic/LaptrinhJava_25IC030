package hki2;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class RankManager {
    private static final String FILE_NAME = "rank.txt";
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public static class RankEntry {
        String name;
        int score;
        Date date;

        public RankEntry(String name, int score, Date date) {
            this.name = name;
            this.score = score;
            this.date = date;
        }
    }

    public static void saveScore(String name, int score) {
        List<RankEntry> allEntries = loadAllEntries();
        // Lưu lượt chơi mới với ngày hiện tại
        allEntries.add(new RankEntry(name, score, new Date()));

        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_NAME))) {
            for (RankEntry entry : allEntries) {
                pw.println(entry.name + ":" + entry.score + ":" + sdf.format(entry.date));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<RankEntry> loadAllEntries() {
        List<RankEntry> entries = new ArrayList<>();
        File file = new File(FILE_NAME);
        if (!file.exists()) return entries;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 3) {
                    String name = parts[0];
                    int score = Integer.parseInt(parts[1]);
                    Date date = sdf.parse(parts[2]);
                    entries.add(new RankEntry(name, score, date));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entries;
    }

    // Hàm lấy Top 5 theo loại: "all", "week", "day"
    public static List<RankEntry> getTop5(String type) {
        List<RankEntry> filtered = new ArrayList<>();
        List<RankEntry> all = loadAllEntries();
        
        Calendar calNow = Calendar.getInstance();
        Date now = new Date();
        calNow.setTime(now);

        for (RankEntry entry : all) {
            Calendar calEntry = Calendar.getInstance();
            calEntry.setTime(entry.date);

            if (type.equalsIgnoreCase("day")) {
                // Cùng ngày, cùng tháng, cùng năm
                if (calNow.get(Calendar.YEAR) == calEntry.get(Calendar.YEAR) &&
                    calNow.get(Calendar.DAY_OF_YEAR) == calEntry.get(Calendar.DAY_OF_YEAR)) {
                    filtered.add(entry);
                }
            } else if (type.equalsIgnoreCase("week")) {
                // Cùng tuần và cùng năm
                if (calNow.get(Calendar.YEAR) == calEntry.get(Calendar.YEAR) &&
                    calNow.get(Calendar.WEEK_OF_YEAR) == calEntry.get(Calendar.WEEK_OF_YEAR)) {
                    filtered.add(entry);
                }
            } else {
                // All-time: lấy hết
                filtered.add(entry);
            }
        }

        // Sắp xếp điểm số giảm dần
        filtered.sort((a, b) -> Integer.compare(b.score, a.score));

        // Lấy tối đa 5 người đầu bảng
        return filtered.subList(0, Math.min(filtered.size(), 5));
    }
}