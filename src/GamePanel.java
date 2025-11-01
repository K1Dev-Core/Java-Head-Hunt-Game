import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class GamePanel extends JPanel {
    private BufferedImage backgroundImage;
    private Map<String, Player> players = new ConcurrentHashMap<>();
    private Map<Integer, HeadObject> heads = new ConcurrentHashMap<>();
    private String myPlayerId;
    private GameClient client;
    private long remainingTime = 120;
    private boolean gameEnded = false;
    private boolean showingGameOver = false;
    private float fadeAlpha = 0.0f;
    private long gameEndTime = 0;
    private java.util.List<Explosion> explosions = new java.util.concurrent.CopyOnWriteArrayList<>();
    private java.util.List<ComboText> comboTexts = new java.util.concurrent.CopyOnWriteArrayList<>();

    public GamePanel(GameClient client) {
        this.client = client;
        setBackground(GameConfig.GAME_BACKGROUND_COLOR);
        setPreferredSize(new Dimension(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT));
        loadBackgroundImage();
        setupMouseListener();
        setDoubleBuffered(true);

        Timer timer = new Timer(GameConfig.RENDER_UPDATE_INTERVAL, e -> repaint());
        timer.start();
    }

    public void updateGameTime(long remaining) {
        this.remainingTime = remaining;
    }

    public void endGame() {
        if (!gameEnded) {
            gameEnded = true;
            showingGameOver = true;
            gameEndTime = System.currentTimeMillis();
            SoundManager.playSound("res/sfx/Last Turn.wav");

            Timer fadeTimer = new Timer(30, null);
            fadeTimer.addActionListener(e -> {
                long elapsed = System.currentTimeMillis() - gameEndTime;

                if (elapsed < 1000) {
                    fadeAlpha = Math.min(0.8f, elapsed / 1000.0f * 0.8f);
                } else if (elapsed < 3000) {
                    fadeAlpha = 0.8f;
                } else if (elapsed < 4000) {
                    fadeAlpha = 0.8f - ((elapsed - 3000) / 1000.0f * 0.8f);
                } else {
                    fadeTimer.stop();
                    showGameOver();
                }
                repaint();
            });
            fadeTimer.start();
        }
    }

    public void resetGame() {
        gameEnded = false;
        showingGameOver = false;
        fadeAlpha = 0.0f;
        explosions.clear();
        comboTexts.clear();
    }

    private void showGameOver() {
        java.util.List<Player> sortedPlayers = new java.util.ArrayList<>(players.values());
        sortedPlayers.sort((p1, p2) -> p2.getScore() - p1.getScore());

        SwingUtilities.invokeLater(() -> {
            new GameOverScreen(sortedPlayers, myPlayerId, (GameClient) SwingUtilities.getWindowAncestor(this));

            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
        });
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File(GameConfig.BACKGROUND_IMAGE));
        } catch (Exception e) {
        }
    }

    private void setupCustomCursor(int playerNumber) {
        BufferedImage cursorImg = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                cursorImg, new Point(0, 0), "blank cursor");
        setCursor(blankCursor);
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
                        if (currentTime - lastSendTime >= GameConfig.POSITION_SEND_INTERVAL) {
                            client.sendPosition(e.getX(), e.getY());
                            lastSendTime = currentTime;
                        }
                    }
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (myPlayerId != null) {
                    checkHeadClick(e.getX(), e.getY());
                }
            }
        });
    }

    private void checkHeadClick(int mouseX, int mouseY) {
        if (gameEnded)
            return;
        for (HeadObject head : heads.values()) {
            int headX = (int) head.getX();
            int headY = (int) head.getY();
            int width = head.getWidth();
            int height = head.getHeight();
            if (mouseX >= headX - GameConfig.HEAD_HITBOX_PADDING
                    && mouseX <= headX + width + GameConfig.HEAD_HITBOX_PADDING &&
                    mouseY >= headY - GameConfig.HEAD_HITBOX_PADDING
                    && mouseY <= headY + height + GameConfig.HEAD_HITBOX_PADDING) {
                client.sendHeadHit(head.getId());
                break;
            }
        }
    }

    public void showExplosion(int x, int y, boolean isSkull) {
        int width = 70;
        int height = 70;
        if (isSkull) {
            SoundManager.playSound("res/sfx/Stamp.wav");
            comboTexts.add(new ComboText(x + width / 2, y + height / 2, -GameConfig.SKULL_PENALTY));
        } else {
            SoundManager.playSound("res/sfx/bubble-pop.wav");
            comboTexts.add(new ComboText(x + width / 2, y + height / 2, GameConfig.SCORE_PER_HIT));
        }
        explosions.add(new Explosion(x + width / 2, y + height / 2));
    }

    public void setMyPlayerId(String id, Color color) {
        this.myPlayerId = id;
        Player me = new Player(id, color);
        players.put(id, me);

        int playerNumber = Integer.parseInt(id.substring(1));
        setupCustomCursor(playerNumber);
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

    public void updateHead(HeadObject newHead) {
        HeadObject existingHead = heads.get(newHead.getId());
        if (existingHead != null) {
            existingHead.updateFromSync(newHead.getX(), newHead.getY(), 
                                       newHead.getVelocityX(), newHead.getVelocityY(),
                                       newHead.getImagePath(), newHead.isSkull());
        } else {
            heads.put(newHead.getId(), newHead);
        }
    }

    public void removeHead(int headId) {
        heads.remove(headId);
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

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }

        for (HeadObject head : heads.values()) {
            head.render(g2d);
        }

        explosions.removeIf(Explosion::isFinished);
        for (Explosion explosion : explosions) {
            explosion.render(g2d);
        }

        comboTexts.removeIf(ComboText::isFinished);
        for (ComboText combo : comboTexts) {
            combo.render(g2d);
        }

        drawOtherPlayersCursors(g2d);
        drawScoreboard(g2d);
        drawTimer(g2d);
        drawVersion(g2d);

        if (showingGameOver && fadeAlpha > 0) {
            int alpha = (int) (fadeAlpha * 255);
            g2d.setColor(new Color(0, 0, 0, alpha));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            if (fadeAlpha > 0.3f) {
                g2d.setFont(FontManager.getFont(Font.BOLD, 120));
                String gameOverText = "จบแล้ว";
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(gameOverText);
                int textHeight = fm.getHeight();

                float textAlphaFloat = Math.min(1.0f, (fadeAlpha - 0.3f) / 0.5f);
                int textAlpha = (int) (textAlphaFloat * 255);
                g2d.setColor(new Color(255, 255, 255, textAlpha));

                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() - textHeight) / 2 + fm.getAscent();
                g2d.drawString(gameOverText, x, y);
            }
        }
    }

    private static BufferedImage[] otherPlayerCursors = new BufferedImage[4];

    static {
        for (int i = 0; i < 4; i++) {
            try {
                otherPlayerCursors[i] = ImageIO.read(new File("res/cursor/" + (i + 1) + ".png"));
            } catch (Exception e) {
            }
        }
    }

    private void drawOtherPlayersCursors(Graphics2D g2d) {
        for (Player player : players.values()) {
            int x = player.getX();
            int y = player.getY();

            try {
                int playerNumber = Integer.parseInt(player.getId().substring(1));
                if (playerNumber > 0 && playerNumber <= otherPlayerCursors.length
                        && otherPlayerCursors[playerNumber - 1] != null) {
                    BufferedImage cursor = otherPlayerCursors[playerNumber - 1];
                    int cursorSize = GameConfig.CURSOR_SIZE;
                    g2d.drawImage(cursor, x - cursorSize / 2, y - cursorSize / 2, cursorSize, cursorSize, null);
                }
            } catch (Exception e) {
                g2d.setColor(player.getColor());
                g2d.setStroke(new BasicStroke(GameConfig.OTHER_CURSOR_STROKE_WIDTH));
                g2d.drawLine(x - GameConfig.OTHER_CURSOR_SIZE, y, x + GameConfig.OTHER_CURSOR_SIZE, y);
                g2d.drawLine(x, y - GameConfig.OTHER_CURSOR_SIZE, x, y + GameConfig.OTHER_CURSOR_SIZE);
                g2d.drawOval(x - GameConfig.OTHER_CURSOR_CIRCLE_SIZE, y - GameConfig.OTHER_CURSOR_CIRCLE_SIZE,
                        GameConfig.OTHER_CURSOR_CIRCLE_SIZE * 2, GameConfig.OTHER_CURSOR_CIRCLE_SIZE * 2);
            }
        }
    }

    private void drawTimer(Graphics2D g2d) {
        int minutes = (int) (remainingTime / 60);
        int seconds = (int) (remainingTime % 60);
        String timeText = String.format("%02d:%02d", minutes, seconds);

        g2d.setFont(FontManager.getFont(Font.BOLD, GameConfig.TIMER_FONT_SIZE));
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(timeText);

        g2d.setColor(new Color(0, 0, 0, 100));
        g2d.fillRoundRect(GameConfig.WINDOW_WIDTH / 2 - textWidth / 2 - 20, 20, textWidth + 40, 60, 15, 15);

        if (remainingTime <= GameConfig.TIMER_CRITICAL_THRESHOLD) {
            g2d.setColor(Color.RED);
        } else if (remainingTime <= GameConfig.TIMER_WARNING_THRESHOLD) {
            g2d.setColor(Color.YELLOW);
        } else {
            g2d.setColor(Color.WHITE);
        }

        g2d.drawString(timeText, GameConfig.WINDOW_WIDTH / 2 - textWidth / 2, 65);
    }

    private void drawScoreboard(Graphics2D g2d) {
        g2d.setColor(new Color(0, 0, 0, 150));
        g2d.fillRoundRect(GameConfig.SCOREBOARD_X, GameConfig.SCOREBOARD_Y, GameConfig.SCOREBOARD_WIDTH,
                40 + players.size() * GameConfig.SCOREBOARD_HEIGHT_PER_PLAYER, 10, 10);

        g2d.setColor(Color.WHITE);
        g2d.setFont(FontManager.getFont(Font.BOLD, GameConfig.SCOREBOARD_TITLE_SIZE));
        g2d.drawString("SCOREBOARD", 20, 35);

        g2d.setFont(FontManager.getFont(Font.PLAIN, GameConfig.SCOREBOARD_TEXT_SIZE));
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

            try {
                int playerNumber = Integer.parseInt(player.getId().substring(1));
                if (playerNumber > 0 && playerNumber <= otherPlayerCursors.length
                        && otherPlayerCursors[playerNumber - 1] != null) {
                    BufferedImage cursor = otherPlayerCursors[playerNumber - 1];
                    g2d.drawImage(cursor, 20, y - 12, 24, 24, null);
                }
            } catch (Exception e) {
                g2d.setColor(player.getColor());
                g2d.fillOval(20, y - 10, 15, 15);
            }

            g2d.setColor(Color.WHITE);
            String text = displayName + ": " + player.getScore();

            if (player.getId().equals(myPlayerId)) {
                text += " (ME)";
            }

            g2d.drawString(text, 48, y + 2);

            y += GameConfig.SCOREBOARD_HEIGHT_PER_PLAYER;
        }
    }

    private void drawVersion(Graphics2D g2d) {
        g2d.setColor(new Color(255, 255, 255, 120));
        g2d.setFont(FontManager.getFont(Font.PLAIN, 16));
        String versionText = "v " + GitVersion.getVersion();
        g2d.drawString(versionText, 15, getHeight() - 15);
    }

}
