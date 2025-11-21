import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class GameServer {
    private Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private Map<String, Player> players = new ConcurrentHashMap<>();
    private Map<Integer, HeadObject> heads = new ConcurrentHashMap<>();
    private java.util.Random random = new java.util.Random();
    private int nextHeadId = 0;
    private int nextPlayerNumber = 1;
    private Timer physicsTimer;
    private Timer gameTimer;
    private long gameStartTime;
    private boolean gameInProgress = false;
    private Consumer<String> logCallback = null;
    
    public void setLogCallback(Consumer<String> callback) {
        this.logCallback = callback;
    }
    
    private void log(String message) {
        System.out.println(message);
        if (logCallback != null) {
            logCallback.accept(message);
        }
    }

    public void start() {
        log("เซิร์ฟเวอร์เริ่มทำงานที่ Port: " + GameConfig.SERVER_PORT);
        gameStartTime = System.currentTimeMillis();
        gameInProgress = false;
        spawnInitialHeads();
        startPhysicsLoop();
        startGameTimer();
        log("สร้าง Heads เริ่มต้นแล้ว");
        log("รอผู้เล่นเชื่อมต่อ...");

        try (ServerSocket serverSocket = new ServerSocket(GameConfig.SERVER_PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                String clientIP = socket.getInetAddress().getHostAddress();
                log("มีผู้เล่นเชื่อมต่อจาก: " + clientIP);
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            log("เกิดข้อผิดพลาด: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void startGameTimer() {
        gameTimer = new Timer();
        gameTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                long elapsed = (System.currentTimeMillis() - gameStartTime) / 1000;
                long remaining = GameConfig.GAME_DURATION - elapsed;
                if (remaining < 0)
                    remaining = 0;
                broadcastToAll("TIME:" + remaining);

                if (remaining == 0) {
                    broadcastToAll("GAMEOVER");
                    resetGame();
                }
            }
        }, 0, GameConfig.GAME_TIMER_INTERVAL);
    }

    private void resetGame() {
        new Thread(() -> {
            try {
                Thread.sleep(GameConfig.GAME_RESET_DELAY);

                for (Player player : players.values()) {
                    player.setScore(0);
                }

                heads.clear();
                nextHeadId = 0;
                spawnInitialHeads();

                gameStartTime = System.currentTimeMillis();
                gameInProgress = false;
                broadcastToAll("NEWGAME");

                log("เกมรีเซ็ตแล้ว - รอผู้เล่นอย่างน้อย " + GameConfig.MIN_PLAYERS + " คนเพื่อเริ่มเกมใหม่");
                
                if (players.size() >= GameConfig.MIN_PLAYERS) {
                    checkAndStartGame();
                } else {
                    log("ผู้เล่นไม่พอ (มี " + players.size() + "/" + GameConfig.MIN_PLAYERS + " คน) - รอผู้เล่นเพิ่มเติม...");
                }
            } catch (Exception e) {
            }
        }).start();
    }

    private void checkAndStartGame() {
        if (!gameInProgress && players.size() >= GameConfig.MIN_PLAYERS) {
            gameInProgress = true;
            broadcastToAll("GAMESTARTING");
            log("เกมเริ่มแล้ว! มีผู้เล่น " + players.size() + " คน");
        }
    }

    private void spawnInitialHeads() {
        for (int i = 0; i < GameConfig.MAX_HEADS; i++) {
            spawnHead();
        }
    }

    private void spawnHead() {
        int id = nextHeadId++;
        double x = random.nextInt(GameConfig.WINDOW_WIDTH - GameConfig.HEAD_SPAWN_MARGIN);
        double y = random.nextInt(GameConfig.WINDOW_HEIGHT - GameConfig.HEAD_SPAWN_MARGIN);
        double vx = (random.nextDouble() - 0.5) * GameConfig.HEAD_VELOCITY_RANGE;
        double vy = (random.nextDouble() - 0.5) * GameConfig.HEAD_VELOCITY_RANGE;
        
        int spriteIndex;
        if (random.nextDouble() < GameConfig.SKULL_SPAWN_CHANCE) {
            spriteIndex = GameConfig.SKULL_INDEX;
        } else {
            spriteIndex = random.nextInt(GameConfig.HEAD_ANIMATIONS.length - 1);
        }
        String imagePath = String.valueOf(spriteIndex);

        HeadObject head = new HeadObject(id, x, y, vx, vy, imagePath);
        heads.put(id, head);
        broadcastToAll("NEWHEAD:" + head.toMessage());
    }

    private void respawnHead(int headId) {
        double x = random.nextInt(GameConfig.WINDOW_WIDTH - GameConfig.HEAD_SPAWN_MARGIN);
        double y = random.nextInt(GameConfig.WINDOW_HEIGHT - GameConfig.HEAD_SPAWN_MARGIN);
        double vx = (random.nextDouble() - 0.5) * GameConfig.HEAD_VELOCITY_RANGE;
        double vy = (random.nextDouble() - 0.5) * GameConfig.HEAD_VELOCITY_RANGE;
        
        int spriteIndex;
        if (random.nextDouble() < GameConfig.SKULL_SPAWN_CHANCE) {
            spriteIndex = GameConfig.SKULL_INDEX;
        } else {
            spriteIndex = random.nextInt(GameConfig.HEAD_ANIMATIONS.length - 1);
        }
        String imagePath = String.valueOf(spriteIndex);

        HeadObject head = new HeadObject(headId, x, y, vx, vy, imagePath);
        heads.put(headId, head);
    }

    private void startPhysicsLoop() {
        physicsTimer = new Timer();
        physicsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updatePhysics();
                broadcastToAll("HEADSYNC:" + serializeHeads());
            }
        }, 0, GameConfig.PHYSICS_UPDATE_INTERVAL);
    }

    private void updatePhysics() {
        java.util.List<Integer> expiredSkulls = new java.util.ArrayList<>();
        
        for (HeadObject head : heads.values()) {
            head.update();
            
            if (head.isSkullExpired()) {
                expiredSkulls.add(head.getId());
            }
        }
        
        for (Integer skullId : expiredSkulls) {
            log("Skull " + skullId + " หมดอายุ - กำลัง respawn");
            respawnHead(skullId);
            broadcastToAll("REMOVEHEAD:" + skullId);
            broadcastToAll("NEWHEAD:" + heads.get(skullId).toMessage());
        }
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
    
    private void recalculateNextPlayerNumber() {
        if (players.isEmpty()) {
            nextPlayerNumber = 1;
            return;
        }
        
        int maxPlayerNumber = 0;
        for (String playerId : players.keySet()) {
            int playerNum = Integer.parseInt(playerId.substring(1));
            if (playerNum > maxPlayerNumber) {
                maxPlayerNumber = playerNum;
            }
        }
        nextPlayerNumber = maxPlayerNumber + 1;
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

                log("ผู้เล่นเข้าร่วม: " + playerId + " (ทั้งหมด: " + players.size() + "/" + GameConfig.MAX_PLAYERS + ")");

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
                
                broadcastToAll("PLAYERCOUNT:" + players.size());
                
                if (gameInProgress) {
                    out.println("GAMEINPROGRESS");
                    log("ผู้เล่น " + playerId + " พยายามเข้าในระหว่างเกม");
                } else {
                    checkAndStartGame();
                }

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
                            
                            int x = (int)head.getX();
                            int y = (int)head.getY();
                            boolean isSkull = head.isSkull();
                            
                            if (isSkull) {
                                player.addScore(-GameConfig.SKULL_PENALTY);
                                log("ผู้เล่น " + playerId + " ถูก Skull! -" + GameConfig.SKULL_PENALTY + " แต้ม");
                            } else {
                                player.addScore(GameConfig.SCORE_PER_HIT);
                            }

                            respawnHead(headId);

                            broadcastToAll("HEADHIT:" + headId + "," + x + "," + y + "," + isSkull);
                            broadcastToAll("SCORE:" + playerId + "," + player.getScore());
                        }
                    }
                }
            } catch (IOException e) {
                log("ผู้เล่นตัดการเชื่อมต่อ: " + playerId);
            } finally {
                cleanup();
            }
        }

        private void cleanup() {
            clients.remove(this);
            if (playerId != null) {
                players.remove(playerId);
                broadcast("REMOVE:" + playerId, this);
                broadcastToAll("PLAYERCOUNT:" + players.size());
                
                log("ผู้เล่นออก: " + playerId + " (เหลือ: " + players.size() + " คน)");
                
                if (gameInProgress && players.size() < GameConfig.MIN_PLAYERS) {
                    gameInProgress = false;
                    broadcastToAll("GAMEINPROGRESS");
                    log("ผู้เล่นไม่พอ (มี " + players.size() + "/" + GameConfig.MIN_PLAYERS + " คน) - เกมหยุดชั่วคราว");
                }
                
                recalculateNextPlayerNumber();
            }
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }


}
