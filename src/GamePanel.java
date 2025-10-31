import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GamePanel extends JPanel {
    private static final Color BACKGROUND_COLOR = new Color(60, 60, 60);
    private Map<String, Player> players = new ConcurrentHashMap<>();
    private Map<Integer, HeadObject> heads = new ConcurrentHashMap<>();
    private String myPlayerId;
    private BufferedImage crosshairImage;
    private GameClient client;

    public GamePanel(GameClient client) {
        this.client = client;
        setBackground(BACKGROUND_COLOR);
        setPreferredSize(new Dimension(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT));
        loadCrosshairImage();
        setupMouseListener();
        hideCursor();

        Timer timer = new Timer(16, e -> repaint());
        timer.start();
    }

    private void hideCursor() {
        BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        setCursor(blankCursor);
    }

    private void loadCrosshairImage() {
        try {
            crosshairImage = ImageIO.read(new File("res/crosshair182.png"));
        } catch (Exception e) {
            System.err.println("Cannot load crosshair image: " + e.getMessage());
        }
    }

    private void setupMouseListener() {
        addMouseMotionListener(new MouseMotionAdapter() {
            private long lastSendTime = 0;

            @Override
            public void mouseMoved(MouseEvent e) {
                if (myPlayerId != null) {
                    Player me = players.get(myPlayerId);
                    if (me != null) {
                        me.setPosition(e.getX(), e.getY());

                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastSendTime >= 10) {
                            client.sendPosition(e.getX(), e.getY());
                            lastSendTime = currentTime;
                        }
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (myPlayerId != null) {
                    checkHeadClick(e.getX(), e.getY());
                }
            }
        });
    }

    private void checkHeadClick(int mouseX, int mouseY) {
        for (HeadObject head : heads.values()) {
            int headX = (int) head.getX();
            int headY = (int) head.getY();
            int width = 72;
            int height = 72;
            
            if (mouseX >= headX && mouseX <= headX + width &&
                mouseY >= headY && mouseY <= headY + height) {
                SoundManager.playSound("res/sfx/bubble-pop.wav");
                client.sendHeadHit(head.getId());
                break;
            }
        }
    }

    public void setMyPlayerId(String id, Color color) {
        this.myPlayerId = id;
        Player me = new Player(id, color);
        players.put(id, me);
    }

    public void addPlayer(Player player) {
        players.put(player.getId(), player);
    }

    public void updatePlayer(Player player) {
        players.put(player.getId(), player);
    }

    public void removePlayer(String id) {
        players.remove(id);
    }

    public void addHead(HeadObject head) {
        heads.put(head.getId(), head);
    }

    public void updateHead(HeadObject head) {
        heads.put(head.getId(), head);
    }

    public void updatePlayerScore(String playerId, int score) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setScore(score);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (HeadObject head : heads.values()) {
            head.render(g2d);
        }

        for (Player player : players.values()) {
            drawCrosshair(g2d, player);
        }

        drawScoreboard(g2d);
    }

    private void drawScoreboard(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(10, 10, 220, 40 + players.size() * 30, 10, 10);

        g2d.setColor(Color.WHITE);
        g2d.setFont(FontManager.getFont(Font.BOLD, 18));
        g2d.drawString("SCOREBOARD", 20, 35);

        g2d.setFont(FontManager.getFont(Font.PLAIN, 14));
        int y = 60;

        java.util.List<Player> sortedPlayers = new java.util.ArrayList<>(players.values());
        sortedPlayers.sort((p1, p2) -> {
            String id1 = p1.getId().substring(1);
            String id2 = p2.getId().substring(1);
            try {
                return Integer.parseInt(id1) - Integer.parseInt(id2);
            } catch (Exception e) {
                return p1.getId().compareTo(p2.getId());
            }
        });

        for (Player player : sortedPlayers) {
            String displayName = player.getId();

            g2d.setColor(player.getColor());
            g2d.fillOval(20, y - 10, 15, 15);

            g2d.setColor(Color.WHITE);
            String text = displayName + ": " + player.getScore();

            if (player.getId().equals(myPlayerId)) {
                text += " (ME)";
            }

            g2d.drawString(text, 45, y + 2);

            y += 30;
        }
    }

    private void drawCrosshair(Graphics2D g2d, Player player) {
        int x = player.getX();
        int y = player.getY();

        if (crosshairImage != null) {
            int imgW = crosshairImage.getWidth();
            int imgH = crosshairImage.getHeight();
            int scaledW = imgW / 3;
            int scaledH = imgH / 3;

            if (player.getId().equals(myPlayerId)) {
                g2d.drawImage(crosshairImage, x - scaledW / 2, y - scaledH / 2, scaledW, scaledH, null);
            } else {
                BufferedImage tinted = new BufferedImage(imgW, imgH, BufferedImage.TYPE_INT_ARGB);
                Graphics2D tg = tinted.createGraphics();
                tg.drawImage(crosshairImage, 0, 0, null);
                tg.setComposite(AlphaComposite.SrcAtop);
                tg.setColor(player.getColor());
                tg.fillRect(0, 0, imgW, imgH);
                tg.dispose();

                g2d.drawImage(tinted, x - scaledW / 2, y - scaledH / 2, scaledW, scaledH, null);
            }
        } else {
            g2d.setColor(player.getColor());
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(x - 10, y, x + 10, y);
            g2d.drawLine(x, y - 10, x, y + 10);
            g2d.drawOval(x - 15, y - 15, 30, 30);
        }
    }
}
