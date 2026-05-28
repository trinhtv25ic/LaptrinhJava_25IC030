package hki2;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.Timer;

public class GameServer {
    private static final int PORT = 12345;
    private static List<ClientHandler> allClients = new ArrayList<>();
    private static Map<Integer, GameRoom> rooms = new HashMap<>();
    private static int roomCounter = 1;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                synchronized (allClients) {
                    allClients.add(handler);
                }
                new Thread(handler).start();
            }
        } catch (IOException e) {
        }
    }

    public static synchronized void createRoom(ClientHandler host, int durationMinutes) {
        int roomId = roomCounter++;
        int durationSeconds = durationMinutes * 60; 
        
        GameRoom room = new GameRoom(roomId, host, durationSeconds);
        rooms.put(roomId, room);
        host.currentRoomId = roomId;
        
        host.sendMessage("ROOM_CREATED:" + roomId); 
        
        broadcastRoomList();
    }

    public static synchronized void joinRoom(int roomId, ClientHandler guest) {
        GameRoom room = rooms.get(roomId);
        if (room != null && room.guest == null) {
            room.guest = guest;
            guest.currentRoomId = roomId;
            
            room.host.sendMessage("OPPONENT_NAME:" + guest.playerName);
            guest.sendMessage("OPPONENT_NAME:" + room.host.playerName);
            
            new Thread(() -> {
                try {
                    Thread.sleep(100); 
                } catch (InterruptedException e) {}
                startRoomCountdown(room);
            }).start();

        } else {
            guest.sendMessage("JOIN_FAILED");
        }
    }

    private static void startRoomCountdown(GameRoom room) {
        Timer countdownTimer = new Timer(1000, null);
        final int[] count = {3};
        countdownTimer.addActionListener(e -> {
            if (room.host == null || room.guest == null) {
                countdownTimer.stop();
                return;
            }
            if (count[0] > 0) {
                room.host.sendMessage("COUNTDOWN:" + count[0]);
                room.guest.sendMessage("COUNTDOWN:" + count[0]);
                count[0]--;
            } else {
                countdownTimer.stop();
                room.host.sendMessage("START");
                room.guest.sendMessage("START");
                startMatchTimer(room);
            }
        });
        countdownTimer.start();
    }

    private static void startMatchTimer(GameRoom room) {
    	final int[] timeLeft = {room.matchDuration};        
    	Timer matchTimer = new Timer(1000, null);
        matchTimer.addActionListener(e -> {
            if (room.host == null || room.guest == null) {
                matchTimer.stop();
                return;
            }
            timeLeft[0]--;
            room.host.sendMessage("TIME:" + timeLeft[0]);
            room.guest.sendMessage("TIME:" + timeLeft[0]);
            if (timeLeft[0] <= 0) {
                matchTimer.stop();
                if (room.host.score > room.guest.score) {
                    room.host.sendMessage("END_RESULT:WIN:" + room.host.playerName + ":" + room.host.score + ":" + room.guest.playerName + ":" + room.guest.score);
                    room.guest.sendMessage("END_RESULT:LOSE:" + room.guest.playerName + ":" + room.guest.score + ":" + room.host.playerName + ":" + room.host.score);
                } else if (room.host.score < room.guest.score) {
                    room.host.sendMessage("END_RESULT:LOSE:" + room.host.playerName + ":" + room.host.score + ":" + room.guest.playerName + ":" + room.guest.score);
                    room.guest.sendMessage("END_RESULT:WIN:" + room.guest.playerName + ":" + room.guest.score + ":" + room.host.playerName + ":" + room.host.score);
                } else {
                    room.host.sendMessage("END_RESULT:DRAW:" + room.host.playerName + ":" + room.host.score + ":" + room.guest.playerName + ":" + room.guest.score);
                    room.guest.sendMessage("END_RESULT:DRAW:" + room.guest.playerName + ":" + room.guest.score + ":" + room.host.playerName + ":" + room.host.score);
                }
                rooms.remove(room.id);
                broadcastRoomList();
            }
        });
        matchTimer.start();
    }

    public static synchronized void broadcastRoomList() {
        StringBuilder sb = new StringBuilder("ROOM_LIST:");
        for (GameRoom room : rooms.values()) {
            String status = (room.guest == null) ? "Waiting" : "Playing";
            int minutes = room.matchDuration / 60; 
            sb.append(room.id).append(",")
              .append(room.host.playerName).append(",")
              .append(status).append(",")
              .append(minutes).append(" phút|");
        }
        
        String roomListStr = sb.toString();
        for (ClientHandler client : allClients) {
            if (client.currentRoomId == -1) { 
                client.sendMessage(roomListStr);
            }
        }
    }

    public static synchronized void removeClient(ClientHandler client) {
        synchronized (allClients) {
            allClients.remove(client);
        }
        if (client.currentRoomId != -1) {
            GameRoom room = rooms.get(client.currentRoomId);
            if (room != null) {
                if (room.host == client) {
                    if (room.guest != null) {
                        room.guest.sendMessage("OPPONENT_LEFT");
                        room.guest.currentRoomId = -1;
                    }
                    rooms.remove(client.currentRoomId);
                } else if (room.guest == client) {
                    if (room.host != null) room.host.sendMessage("OPPONENT_LEFT");
                    room.guest = null;
                }
            }
        }
        broadcastRoomList();
    }

    private static class GameRoom {
        int id;
        ClientHandler host;
        ClientHandler guest;
        int matchDuration; 

        public GameRoom(int id, ClientHandler host, int matchDuration) {
            this.id = id;
            this.host = host;
            this.matchDuration = matchDuration; 
        }
    }
    public static class ClientHandler implements Runnable {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        public String playerName = "Unknown";
        public int score = 0;
        public int currentRoomId = -1;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("NAME:")) {
                        this.playerName = message.substring(5).trim();
                        GameServer.broadcastRoomList();
                    } else if (message.equals("GET_ROOMS")) {
                        GameServer.broadcastRoomList();
                    } 
                    // SỬA CHÍNH XÁC ĐOẠN NÀY:
                    else if (message.startsWith("CREATE_ROOM")) { 
                        int durationMinutes = 1; 
                        
                        if (message.contains(":")) {
                            try {
                                String[] parts = message.split(":");
                                if (parts.length >= 2) {
                                    durationMinutes = Integer.parseInt(parts[1].trim());
                                }
                            } catch (Exception e) {
                                durationMinutes = 1; 
                            }
                        }
                        
                        GameServer.createRoom(this, durationMinutes);
                    } 
                    // -----------------------
                    else if (message.startsWith("JOIN_ROOM:")) {
                        int id = Integer.parseInt(message.substring(10).trim());
                        GameServer.joinRoom(id, this);
                    } else if (message.startsWith("SCORE:")) {
                        this.score = Integer.parseInt(message.substring(6).trim());
                        GameRoom room = rooms.get(currentRoomId);
                        if (room != null) {
                            ClientHandler opp = (room.host == this) ? room.guest : room.host;
                            if (opp != null) opp.sendMessage("OPPONENT_SCORE:" + this.score);
                        }
                    }
                }
            } catch (IOException e) {
            } finally {
                GameServer.removeClient(this);
                try { socket.close(); } catch (IOException e) {}
            }
        }

        public void sendMessage(String message) {
            if (out != null) out.println(message);
        }
    }
    
}