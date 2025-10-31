# 📋 ระบบเกม Monster Pop Arena - สถาปัตยกรรมและการทำงาน

## 📁 โครงสร้างไฟล์และหน้าที่

### 🎮 ไฟล์หลัก (Core Files)

| ไฟล์ | หน้าที่ | ส่วนที่ใช้งาน |
|------|---------|---------------|
| **Main.java** | จุดเริ่มต้นของโปรแกรม | Entry Point |
| **GameConfig.java** | เก็บค่าคงที่และการตั้งค่าต่างๆ ของเกม | Configuration |

### 🖥️ ส่วนติดต่อผู้ใช้ (UI Components)

| ไฟล์ | หน้าที่ | ส่วนที่ใช้งาน |
|------|---------|---------------|
| **MainMenu.java** | หน้าเมนูหลัก แสดงปุ่มเริ่มเกม/ออก | Main Menu |
| **LobbyScreen.java** | หน้ารอผู้เล่น แสดงจำนวนผู้เล่นที่เข้าร่วม | Lobby Screen |
| **GamePanel.java** | หน้าจอเกมหลัก แสดงการเล่นเกม | Game Screen |
| **GameOverScreen.java** | หน้าจอสรุปคะแนนเมื่อเกมจบ | Game Over Screen |
| **MenuElement.java** | คลาสสำหรับสร้างองค์ประกอบ UI (ปุ่ม, ข้อความ, รูปภาพ) | UI Helper |
| **PopupWindow.java** | หน้าต่าง Popup สำหรับแสดงข้อมูล | UI Component |
| **PopupWindowConfig.java** | การตั้งค่าหน้าต่าง Popup | UI Configuration |
| **PopupWindowEditor.java** | เครื่องมือแก้ไข Popup (สำหรับ Dev) | Development Tool |

### 🌐 ส่วนเครือข่าย (Network Components)

| ไฟล์ | หน้าที่ | ส่วนที่ใช้งาน |
|------|---------|---------------|
| **GameClient.java** | จัดการการเชื่อมต่อฝั่งผู้เล่น | Client-Side Network |
| **GameServer.java** | จัดการเซิร์ฟเวอร์และ Logic เกม | Server-Side Network |
| **GameServerGUI.java** | หน้าต่าง GUI สำหรับเซิร์ฟเวอร์ | Server UI |

### 🎲 ส่วนวัตถุในเกม (Game Objects)

| ไฟล์ | หน้าที่ | ส่วนที่ใช้งาน |
|------|---------|---------------|
| **Player.java** | เก็บข้อมูลผู้เล่น (ตำแหน่ง, คะแนน, สี) | Game Entity |
| **HeadObject.java** | วัตถุหัวที่ต้องคลิก มีการเคลื่อนไหว | Game Entity |
| **Explosion.java** | เอฟเฟกต์ระเบิดเมื่อคลิกหัว | Visual Effect |
| **ComboText.java** | แสดงข้อความคะแนนลอยขึ้น | Visual Effect |

### 🎨 ส่วนสนับสนุน (Utility Components)

| ไฟล์ | หน้าที่ | ส่วนที่ใช้งาน |
|------|---------|---------------|
| **FontManager.java** | จัดการฟอนต์ภาษาไทย | Font Management |
| **SoundManager.java** | จัดการเสียงในเกม | Sound Management |
| **SpriteSheet.java** | จัดการ Animation sprites | Animation System |

---

## 🏗️ สถาปัตยกรรมระบบ

### 1. แบบจำลอง Client-Server

```
┌─────────────────────────────────────────────────────┐
│                   Game Server                        │
│  ┌──────────────────────────────────────────┐      │
│  │  GameServer.java                         │      │
│  │  - รับ/ส่งข้อมูลผู้เล่น                  │      │
│  │  - คำนวณ Physics                         │      │
│  │  - จัดการคะแนน                           │      │
│  │  - Spawn/Respawn Heads                   │      │
│  └──────────────────────────────────────────┘      │
└─────────────────────────────────────────────────────┘
                        ↕ TCP/IP Socket
┌──────────────┬──────────────┬──────────────┬─────────┐
│  Client 1    │  Client 2    │  Client 3    │Client 4 │
│ ┌──────────┐ │ ┌──────────┐ │ ┌──────────┐ │┌───────┐│
│ │GameClient│ │ │GameClient│ │ │GameClient│ ││GameCli││
│ │  .java   │ │ │  .java   │ │ │  .java   │ ││ent.jav││
│ └──────────┘ │ └──────────┘ │ └──────────┘ │└───────┘│
│      ↕       │      ↕       │      ↕       │    ↕    │
│ ┌──────────┐ │ ┌──────────┐ │ ┌──────────┐ │┌───────┐│
│ │GamePanel │ │ │GamePanel │ │ │GamePanel │ ││GamePan││
│ │  .java   │ │ │  .java   │ │ │  .java   │ ││el.java││
│ └──────────┘ │ └──────────┘ │ └──────────┘ │└───────┘│
└──────────────┴──────────────┴──────────────┴─────────┘
```

### 2. โปรโตคอลการสื่อสาร

#### 📤 Server → Client Messages
```
ID:P1,12345678              // กำหนด Player ID และสี
NEW:P2,100,200,12345678,0   // ผู้เล่นใหม่เข้าร่วม
UPDATE:P2,150,250,12345678,5 // อัพเดตตำแหน่งผู้เล่น
REMOVE:P2                   // ผู้เล่นออก
NEWHEAD:0,100,200,2.5,1.5,3 // หัวใหม่ปรากฏ
HEADSYNC:0,100,200,2.5,1.5,3|1,200,300,1.0,2.0,1  // ซิงค์ตำแหน่งหัวทั้งหมด
SCORE:P1,15                 // อัพเดตคะแนน
TIME:95                     // เวลาที่เหลือ (วินาที)
GAMESTARTING                // เกมกำลังจะเริ่ม
GAMEINPROGRESS              // เกมกำลังดำเนินอยู่
GAMEOVER                    // เกมจบ
NEWGAME                     // เริ่มเกมใหม่
```

#### 📥 Client → Server Messages
```
POS:150,250                 // ส่งตำแหน่ง cursor
HIT:5                       // คลิกหัว ID 5
```

---

## 🎨 1. การวาดด้วย Paint และ PaintComponent

### ทำงานอย่างไร?
Java Swing ใช้ระบบ **Double Buffering** เพื่อวาดภาพที่ลื่นไหล

### ตัวอย่างโค้ด: GamePanel.java

```java
public class GamePanel extends JPanel {
    private BufferedImage backgroundImage;
    
    public GamePanel(GameClient client) {
        setDoubleBuffered(true);  // เปิดใช้ Double Buffering
        
        // Timer สำหรับ repaint ทุก 16ms (≈60 FPS)
        Timer timer = new Timer(GameConfig.RENDER_UPDATE_INTERVAL, e -> repaint());
        timer.start();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // เปิด Anti-aliasing สำหรับภาพที่ลื่นไหล
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        // 1. วาดพื้นหลัง
        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
        
        // 2. วาดวัตถุในเกม (Heads)
        for (HeadObject head : heads.values()) {
            head.render(g2d);
        }
        
        // 3. วาด Effects (Explosions)
        for (Explosion explosion : explosions) {
            explosion.render(g2d);
        }
        
        // 4. วาด UI
        drawScoreboard(g2d);
        drawTimer(g2d);
    }
}
```

### ตัวอย่าง: การวาดข้อความและรูปภาพ

```java
private void drawTimer(Graphics2D g2d) {
    int minutes = (int) (remainingTime / 60);
    int seconds = (int) (remainingTime % 60);
    String timeText = String.format("%02d:%02d", minutes, seconds);
    
    // ตั้งค่าฟอนต์
    g2d.setFont(FontManager.getFont(Font.BOLD, 48));
    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth(timeText);
    
    // วาดพื้นหลังโปร่งใส
    g2d.setColor(new Color(0, 0, 0, 100));
    g2d.fillRoundRect(800 - textWidth/2 - 20, 20, textWidth + 40, 60, 15, 15);
    
    // วาดข้อความ
    g2d.setColor(Color.WHITE);
    g2d.drawString(timeText, 800 - textWidth/2, 65);
}
```

### MenuElement.java - ระบบวาด UI

```java
public class MenuElement {
    public enum ElementType { TEXT, IMAGE }
    
    private ElementType type;
    private String content;
    private BufferedImage image;
    private double x, y, width, height;
    
    public void render(Graphics2D g2d) {
        if (type == ElementType.IMAGE && image != null) {
            // วาดรูปภาพจากจุดกึ่งกลาง
            int drawX = (int)(x - width / 2);
            int drawY = (int)(y - height / 2);
            g2d.drawImage(image, drawX, drawY, (int)width, (int)height, null);
        } 
        else if (type == ElementType.TEXT) {
            // วาดข้อความจากจุดกึ่งกลาง
            g2d.setFont(FontManager.getThaiFont(fontSize));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(content);
            g2d.setColor(textColor);
            g2d.drawString(content, (int)(x - textWidth/2), (int)(y + fontSize/3));
        }
    }
}
```

---

## ⚙️ 2. การทำงานแบบ Multi-Tasking (Thread)

### Thread หลักในระบบ

```
┌─────────────────────────────────────────────────┐
│              Main Thread (EDT)                   │
│  - จัดการ UI events                             │
│  - Mouse clicks, movements                      │
│  - Repaint UI                                   │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│         Network Thread (GameClient)              │
│  - รับข้อมูลจาก Server (blocking I/O)           │
│  - ประมวลผล messages                            │
└─────────────────────────────────────────────────┘
                     ↓
┌─────────────────────────────────────────────────┐
│           Timer Thread (Swing Timer)             │
│  - repaint() ทุก 16ms                           │
│  - อัพเดต animation frames                      │
└─────────────────────────────────────────────────┘
```

### ตัวอย่างโค้ด 1: Network Thread (GameClient.java)

```java
public class GameClient extends JFrame {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    private void connectToServer() {
        try {
            socket = new Socket(serverIP, GameConfig.SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // สร้าง Thread แยกสำหรับรับข้อมูล
            new Thread(this::receiveMessages).start();
            
        } catch (IOException e) {
            // จัดการ error
        }
    }
    
    // Thread แยก: รับข้อมูลจาก Server
    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                processMessage(message);  // ประมวลผลข้อมูล
            }
        } catch (IOException e) {
            System.err.println("Connection lost");
        }
    }
    
    // ประมวลผลข้อมูลและอัพเดต UI ใน EDT
    private void processMessage(String message) {
        if (message.startsWith("NEW:")) {
            Player player = Player.fromMessage(message.substring(4));
            
            // อัพเดต UI ต้องทำใน EDT
            SwingUtilities.invokeLater(() -> {
                if (gamePanel != null) {
                    gamePanel.addPlayer(player);
                }
            });
        }
    }
}
```

### ตัวอย่างโค้ด 2: Game Start Delay Thread

```java
private void startGame() {
    if (gameStarted) return;
    gameStarted = true;
    
    // สร้าง Thread แยกสำหรับ delay
    new Thread(() -> {
        try {
            Thread.sleep(GameConfig.GAME_START_DELAY);  // รอ 3 วินาที
        } catch (InterruptedException e) {
        }
        
        // กลับมาสร้าง UI ใน EDT
        SwingUtilities.invokeLater(() -> {
            if (lobbyScreen != null) {
                lobbyScreen.dispose();
            }
            
            setTitle("Monster Pop Arena");
            gamePanel = new GamePanel(this);
            add(gamePanel);
            pack();
            setVisible(true);
        });
    }).start();
}
```

### ตัวอย่างโค้ด 3: Server Physics Thread (GameServer.java)

```java
public class GameServer {
    private Timer physicsTimer;
    
    private void startPhysicsLoop() {
        physicsTimer = new Timer();
        
        // สร้าง Thread ที่ทำงานทุก 16ms
        physicsTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                updatePhysics();  // อัพเดตตำแหน่งวัตถุ
                broadcastToAll("HEADSYNC:" + serializeHeads());  // ส่งข้อมูล
            }
        }, 0, GameConfig.PHYSICS_UPDATE_INTERVAL);
    }
    
    private void updatePhysics() {
        // อัพเดตตำแหน่งหัวทั้งหมด
        for (HeadObject head : heads.values()) {
            head.update();  // เคลื่อนที่ + ชนขอบ
        }
    }
}
```

### HeadObject.java - การอัพเดตตำแหน่ง

```java
public class HeadObject {
    private double x, y;      // ตำแหน่ง
    private double vx, vy;    // ความเร็ว
    
    public void update() {
        // เคลื่อนที่
        x += vx;
        y += vy;
        
        // ชนขอบซ้าย-ขวา
        if (x < 0 || x > GameConfig.WINDOW_WIDTH - GameConfig.HEAD_DEFAULT_SIZE) {
            vx = -vx;
            x = Math.max(0, Math.min(x, GameConfig.WINDOW_WIDTH - GameConfig.HEAD_DEFAULT_SIZE));
        }
        
        // ชนขอบบน-ล่าง
        if (y < 0 || y > GameConfig.WINDOW_HEIGHT - GameConfig.HEAD_DEFAULT_SIZE) {
            vy = -vy;
            y = Math.max(0, Math.min(y, GameConfig.WINDOW_HEIGHT - GameConfig.HEAD_DEFAULT_SIZE));
        }
        
        // อัพเดต animation frame
        animationTimer += GameConfig.PHYSICS_UPDATE_INTERVAL;
        if (animationTimer >= frameDelay) {
            currentFrame = (currentFrame + 1) % totalFrames;
            animationTimer = 0;
        }
    }
}
```

### การใช้ Thread-Safe Collections

```java
// ใน GamePanel.java
private Map<String, Player> players = new ConcurrentHashMap<>();
private Map<Integer, HeadObject> heads = new ConcurrentHashMap<>();
private List<Explosion> explosions = new CopyOnWriteArrayList<>();

// ConcurrentHashMap: หลาย Thread เข้าถึงพร้อมกันได้ปลอดภัย
// CopyOnWriteArrayList: เหมาะสำหรับ read มาก write น้อย
```

---

## 🌐 3. การเชื่อมต่อผ่านเครือข่าย (Client-Server)

### สถาปัตยกรรม TCP/IP

```
┌────────────────────────────────────────────────────┐
│                   GameServer                        │
│  ServerSocket (Port 25565)                         │
│  ┌──────────────────────────────────────────┐     │
│  │  while(true) {                           │     │
│  │    Socket client = serverSocket.accept() │     │
│  │    new Thread(clientHandler).start()     │     │
│  │  }                                       │     │
│  └──────────────────────────────────────────┘     │
│                                                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐ │
│  │ClientHandler│ │ClientHandler│ │ClientHandler│ │
│  │  Thread 1   │ │  Thread 2   │ │  Thread 3   │ │
│  │  (Player 1) │ │  (Player 2) │ │  (Player 3) │ │
│  └─────────────┘ └─────────────┘ └─────────────┘ │
└────────────────────────────────────────────────────┘
        ↕                ↕                ↕
┌─────────────┐  ┌─────────────┐  ┌─────────────┐
│  Client 1   │  │  Client 2   │  │  Client 3   │
│  Socket     │  │  Socket     │  │  Socket     │
│  (P1)       │  │  (P2)       │  │  (P3)       │
└─────────────┘  └─────────────┘  └─────────────┘
```

### ตัวอย่าง: Server Setup (GameServer.java)

```java
public class GameServer {
    private Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();
    private Map<String, Player> players = new ConcurrentHashMap<>();
    
    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(GameConfig.SERVER_PORT)) {
            log("เซิร์ฟเวอร์เริ่มทำงานที่ Port: " + GameConfig.SERVER_PORT);
            
            // Loop รอรับการเชื่อมต่อ
            while (true) {
                Socket socket = serverSocket.accept();
                log("มีผู้เล่นเชื่อมต่อจาก: " + socket.getInetAddress());
                
                // สร้าง Handler แยกสำหรับแต่ละ Client
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            log("เกิดข้อผิดพลาด: " + e.getMessage());
        }
    }
    
    // ส่งข้อมูลถึงทุกคน
    private void broadcastToAll(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }
    
    // ส่งข้อมูลถึงทุกคนยกเว้นผู้ส่ง
    private void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }
}
```

### ตัวอย่าง: Client Handler (GameServer.java)

```java
class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String playerId;
    
    @Override
    public void run() {
        try {
            // เปิด Stream สำหรับรับ/ส่งข้อมูล
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // สร้างผู้เล่นใหม่
            playerId = "P" + nextPlayerNumber++;
            Player newPlayer = new Player(playerId, randomColor);
            players.put(playerId, newPlayer);
            
            // ส่ง ID ให้ Client
            out.println("ID:" + playerId + "," + playerColor.getRGB());
            
            // ส่งข้อมูลผู้เล่นเดิมให้ Client ใหม่
            for (Player p : players.values()) {
                if (!p.getId().equals(playerId)) {
                    out.println("NEW:" + p.toMessage());
                }
            }
            
            // แจ้งผู้เล่นทุกคนว่ามีคนใหม่เข้ามา
            broadcast("NEW:" + newPlayer.toMessage(), this);
            
            // Loop รับข้อมูลจาก Client
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("POS:")) {
                    // อัพเดตตำแหน่ง
                    String[] parts = message.substring(4).split(",");
                    int x = Integer.parseInt(parts[0]);
                    int y = Integer.parseInt(parts[1]);
                    
                    Player player = players.get(playerId);
                    player.setPosition(x, y);
                    
                    // ส่งต่อไปยังผู้เล่นคนอื่น
                    broadcast("UPDATE:" + player.toMessage(), this);
                    
                } else if (message.startsWith("HIT:")) {
                    // ประมวลผลการคลิกหัว
                    int headId = Integer.parseInt(message.substring(4));
                    HeadObject head = heads.get(headId);
                    
                    if (head != null) {
                        Player player = players.get(playerId);
                        
                        if (head.isSkull()) {
                            player.addScore(-GameConfig.SKULL_PENALTY);
                        } else {
                            player.addScore(GameConfig.SCORE_PER_HIT);
                        }
                        
                        respawnHead(headId);
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
        }
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
```

### ตัวอย่าง: Client Connection (GameClient.java)

```java
public class GameClient extends JFrame {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    
    private void connectToServer() {
        try {
            // เชื่อมต่อไปยัง Server
            socket = new Socket(serverIP, GameConfig.SERVER_PORT);
            
            // เปิด Stream สำหรับรับ/ส่งข้อมูล
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            
            // สร้าง Thread รับข้อมูล
            new Thread(this::receiveMessages).start();
            
        } catch (IOException e) {
            // แสดง Dialog ให้ใส่ IP ใหม่
            showIPDialog();
        }
    }
    
    // Thread รับข้อมูลจาก Server
    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Connection lost");
        }
    }
    
    // ส่งตำแหน่ง cursor ไปยัง Server
    public void sendPosition(int x, int y) {
        if (out != null) {
            out.println("POS:" + x + "," + y);
        }
    }
    
    // ส่งข้อมูลการคลิกหัว
    public void sendHeadHit(int headId) {
        if (out != null) {
            out.println("HIT:" + headId);
        }
    }
}
```

### การส่งข้อมูลแบบมีประสิทธิภาพ

```java
// ใน GamePanel.java - จำกัดการส่งตำแหน่ง
addMouseMotionListener(new MouseMotionAdapter() {
    private long lastSendTime = 0;
    
    @Override
    public void mouseMoved(MouseEvent e) {
        if (myPlayerId != null) {
            Player me = players.get(myPlayerId);
            if (me != null) {
                me.setPosition(e.getX(), e.getY());
                
                // ส่งไปยัง Server เฉพาะทุก 50ms
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastSendTime >= GameConfig.POSITION_SEND_INTERVAL) {
                    client.sendPosition(e.getX(), e.getY());
                    lastSendTime = currentTime;
                }
            }
        }
    }
});
```

---

## 🔄 Flow การทำงานทั้งระบบ

### 1. เริ่มต้นเกม

```
[Client Start] → MainMenu → กดปุ่ม "เริ่มเกม"
                    ↓
              [GameClient]
                    ↓
         connectToServer(IP, Port)
                    ↓
         [Server accepts connection]
                    ↓
         Server: สร้าง ClientHandler Thread
                    ↓
         Server: ส่ง "ID:P1,<color>"
                    ↓
         Client: รับ ID → แสดง LobbyScreen
                    ↓
         รอผู้เล่นครบ (MIN_PLAYERS)
                    ↓
         Server: ส่ง "GAMESTARTING"
                    ↓
         Client: รอ 3 วินาที → แสดง GamePanel
```

### 2. ระหว่างเกม

```
[ผู้เล่นเคลื่อนเมาส์]
        ↓
Client: mouseMoved() → sendPosition(x,y)
        ↓
Server: รับ "POS:x,y" → อัพเดต Player
        ↓
Server: broadcast "UPDATE:P1,x,y,color,score"
        ↓
Clients อื่น: รับข้อมูล → อัพเดตตำแหน่ง cursor


[ผู้เล่นคลิกหัว]
        ↓
Client: checkHeadClick() → sendHeadHit(headId)
        ↓
Server: รับ "HIT:headId"
        ↓
Server: คำนวณคะแนน + respawnHead()
        ↓
Server: broadcast "SCORE:P1,<newScore>"
        ↓
All Clients: อัพเดตคะแนนใน Scoreboard


[Server Physics Loop - ทุก 16ms]
        ↓
Server: updatePhysics() → เคลื่อนหัวทั้งหมด
        ↓
Server: broadcast "HEADSYNC:<all heads data>"
        ↓
Clients: อัพเดตตำแหน่งหัว → repaint()
```

### 3. จบเกม

```
[เวลาหมด: 120 วินาที]
        ↓
Server: broadcast "GAMEOVER"
        ↓
Client: gamePanel.endGame()
        ↓
Client: แสดง GameOverScreen (Top 3)
        ↓
Client: disconnect หลัง 3 วินาที
        ↓
Server: รอ 5 วินาที → resetGame()
        ↓
Server: broadcast "NEWGAME"
```

---

## 📊 ตัวอย่างข้อมูลที่ส่งผ่าน Network

### การ Serialize/Deserialize

```java
// Player.java
public String toMessage() {
    return id + "," + x + "," + y + "," + color.getRGB() + "," + score;
}

public static Player fromMessage(String message) {
    String[] parts = message.split(",");
    String id = parts[0];
    int x = Integer.parseInt(parts[1]);
    int y = Integer.parseInt(parts[2]);
    Color color = new Color(Integer.parseInt(parts[3]));
    int score = Integer.parseInt(parts[4]);
    
    Player p = new Player(id, color);
    p.setPosition(x, y);
    p.setScore(score);
    return p;
}
```

```java
// HeadObject.java
public String toMessage() {
    return id + "," + x + "," + y + "," + vx + "," + vy + "," + imagePath;
}

public static HeadObject fromMessage(String message) {
    String[] parts = message.split(",");
    int id = Integer.parseInt(parts[0]);
    double x = Double.parseDouble(parts[1]);
    double y = Double.parseDouble(parts[2]);
    double vx = Double.parseDouble(parts[3]);
    double vy = Double.parseDouble(parts[4]);
    String imagePath = parts[5];
    
    return new HeadObject(id, x, y, vx, vy, imagePath);
}
```

---

## ⚡ การ Optimize Performance

### 1. Render Optimization
- ใช้ `setDoubleBuffered(true)` เพื่อลด flickering
- จำกัด repaint rate ที่ 60 FPS (16ms)
- ใช้ `CopyOnWriteArrayList` สำหรับ effects ที่มีการ iterate บ่อย

### 2. Network Optimization
- จำกัดการส่งตำแหน่งเมาส์ที่ 50ms
- ใช้ `ConcurrentHashMap` สำหรับข้อมูล multi-thread
- Batch update หลาย heads ในข้อความเดียว (HEADSYNC)

### 3. Thread Safety
- ใช้ `SwingUtilities.invokeLater()` สำหรับอัพเดต UI
- ใช้ Thread-safe collections
- แยก Thread สำหรับ blocking I/O

---

## 🎯 สรุป

ระบบเกม Monster Pop Arena ใช้สถาปัตยกรรม Client-Server แบบ Real-time โดย:

1. **การวาด (Rendering):** ใช้ `paintComponent()` + Double Buffering + Anti-aliasing
2. **Multi-Threading:** แยก Thread สำหรับ Network, Timer, และ UI (EDT)
3. **Networking:** ใช้ TCP Socket + Protocol แบบ text-based + Thread ต่อ Client

ระบบถูกออกแบบให้รองรับผู้เล่น 4 คนพร้อมกัน มีการซิงค์ข้อมูลแบบ Real-time และจัดการ Physics ฝั่ง Server เพื่อความเป็นธรรม

---

📝 เอกสารนี้สร้างขึ้นเพื่ออธิบายการทำงานของระบบเกม Monster Pop Arena

