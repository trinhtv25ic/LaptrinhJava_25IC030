package hki2;

import java.io.*;
public class PlayerData {
    private static final String PLAYER_FILE = "playerBP.txt";
    public static void savePlayerName(String name) {
        try {
            FileWriter fw = new FileWriter(PLAYER_FILE);
            fw.write(name);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static String getPlayerName() {
        try {
            File file = new File(PLAYER_FILE);

            if (!file.exists()) {
                return null;
            }
            BufferedReader br = new BufferedReader(new FileReader(file));
            String name = br.readLine();
            br.close();
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
