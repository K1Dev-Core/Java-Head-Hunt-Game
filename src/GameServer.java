import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class GameServer {
    private static final int PORT = 8888;
    private static final int MAX_HEADS = 10;
    private static final String[] HEAD_IMAGES = {
            "res/head/Jump (36x36).png",
            "res/head/Hit 2 (36x30)-0.png"
    };

    private Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private Map<String, Player> players = new ConcurrentHashMap<>();
    private Map<Integer, HeadObject> heads = new ConcurrentHashMap<>();
    private java.util.Random random = new java.util.Random();
    private int nextHeadId = 0;
    private int nextPlayerNumber = 1;
    private Timer physicsTimer;

    public void start() {
        System.out.println("Server starting on port " + PORT);
        spawnInitialHeads();
        startPhysicsLoop();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void spawnInitialHeads() {
        for (int i = 0; i < MAX_HEADS; i++) {
            spawnHead();
        }
    }

    private void spawnHead() {
        int id = nextHeadId++;
        double x = random.nextInt(1280 - 50);
        double y = random.nextInt(720 - 50);
        double vx = (random.nextDouble() - 0.5) * 6;
        double vy = (random.nextDouble() - 0.5) * 6;
        String imagePath = HEAD_IMAGES[random.nextInt(HEAD_IMAGES.length)];
        
        HeadObject head = new HeadObject(id, x, y, vx, vy, imagePath);
        heads.put(id, head);
        broadcastToAll("NEWHEAD:" + head.toMessage());
    }
    
    private void respawnHead(int headId) {
        double x = random.nextInt(1280 - 50);
        double y = random.nextInt(720 - 50);
        double vx = (random.nextDouble() - 0.5) * 6;
        double vy = (random.nextDouble() - 0.5) * 6;
        String imagePath = HEAD_IMAGES[random.nextInt(HEAD_IMAGES.length)];
        
        HeadObject head = new HeadObject(headId, x, y, vx, vy, imagePath);
        heads.put(headId, head);
    }

    private void startPhysicsLoop() {
        physicsTimer = new Timer();
        physicsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updatePhysics();
            }
        }, 0, 16);
    }

    private void updatePhysics() {
        for (HeadObject head : heads.values()) {
            head.update();
        }
        broadcastToAll("HEADSYNC:" + serializeHeads());
    }

    private String serializeHeads() {
        StringBuilder sb = new StringBuilder();
        for (HeadObject head : heads.values()) {
            if (sb.length() > 0)
                sb.append("|");
            sb.append(head.toMessage());
        }
        return sb.toString();
    }

    private void broadcastToAll(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    private void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    class ClientHandler implements Runnable {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String playerId;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void sendMessage(String message) {
            if (out != null) {
                out.println(message);
            }
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                playerId = "P" + nextPlayerNumber++;
                java.awt.Color playerColor = new java.awt.Color(
                        random.nextInt(256),
                        random.nextInt(256),
                        random.nextInt(256));
                Player newPlayer = new Player(playerId, playerColor);
                players.put(playerId, newPlayer);

                out.println("ID:" + playerId + "," + playerColor.getRGB());

                for (Player p : players.values()) {
                    if (!p.getId().equals(playerId)) {
                        out.println("NEW:" + p.toMessage());
                    }
                }

                for (HeadObject head : heads.values()) {
                    out.println("NEWHEAD:" + head.toMessage());
                }

                broadcast("NEW:" + newPlayer.toMessage(), this);

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("POS:")) {
                        String[] parts = message.substring(4).split(",");
                        int x = Integer.parseInt(parts[0]);
                        int y = Integer.parseInt(parts[1]);

                        Player player = players.get(playerId);
                        player.setPosition(x, y);

                        broadcast("UPDATE:" + player.toMessage(), this);

                    } else if (message.startsWith("HIT:")) {
                        int headId = Integer.parseInt(message.substring(4));
                        HeadObject head = heads.get(headId);

                        if (head != null) {
                            Player player = players.get(playerId);
                            player.addScore(10);

                            respawnHead(headId);

                            broadcastToAll("SCORE:" + playerId + "," + player.getScore());
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Client disconnected: " + playerId);
            } finally {
                cleanup();
            }
        }

        private void cleanup() {
            clients.remove(this);
            if (playerId != null) {
                players.remove(playerId);
                broadcast("REMOVE:" + playerId, this);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new GameServer().start();
    }
}
