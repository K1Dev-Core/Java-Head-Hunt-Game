import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GameOverScreen extends JFrame {
    private Map<Player, Integer> animatedScores = new HashMap<>();
    private Map<Player, Integer> targetScores = new HashMap<>();
    private Timer animationTimer;
    private List<Player> sortedPlayers;
    private ArrayList<MenuElement> staticElements;
    private PopupWindowConfig config;
    private String myPlayerId;
    private JPanel contentPanel;
    private BufferedImage backgroundImage;
    private static BufferedImage[] playerCursors = new BufferedImage[4];
    private boolean backHover = false;
    private GameClient gameClient;
    private Cursor normalCursor;
    private Cursor pressedCursor;
    
    static {
        for (int i = 0; i < 4; i++) {
            try {
                playerCursors[i] = ImageIO.read(new File("res/cursor/" + (i + 1) + ".png"));
            } catch (Exception e) {
            }
        }
    }

    public GameOverScreen(List<Player> sortedPlayers, String myPlayerId, GameClient gameClient) {
        this.myPlayerId = myPlayerId;
        this.sortedPlayers = sortedPlayers;
        this.gameClient = gameClient;
        
        config = new PopupWindowConfig();
        config.width = GameConfig.WINDOW_WIDTH;
        config.height = GameConfig.WINDOW_HEIGHT;
        config.backgroundColor = new Color(240, 240, 240);
        config.useBackgroundImage = true;
        config.backgroundImagePath = "res/bg_menu.png";

        try {
            backgroundImage = ImageIO.read(new File(config.backgroundImagePath));
        } catch (Exception e) {
        }

        staticElements = new ArrayList<>();

        MenuElement notepad = new MenuElement(MenuElement.ElementType.IMAGE, 
            "res/QuestNotepad_Small.png", 825.0, 153.0, 756.0, 274.0);
        staticElements.add(notepad);

        MenuElement titleText = new MenuElement("สรุปคะแนน", 812.0, 173.0, 141);
        titleText.setTextColor(new Color(51, 51, 51));
        staticElements.add(titleText);

        MenuElement pillar1 = new MenuElement(MenuElement.ElementType.IMAGE, 
            "res/Better_Pillar 1.png", 815.0, 907.0, 356.0, 906.0);
        staticElements.add(pillar1);
        
        MenuElement pillar2 = new MenuElement(MenuElement.ElementType.IMAGE, 
            "res/Better_Pillar 1.png", 426.0, 1028.0, 295.5, 752.0);
        staticElements.add(pillar2);
        
        MenuElement pillar3 = new MenuElement(MenuElement.ElementType.IMAGE, 
            "res/Better_Pillar 1.png", 1220.0, 1026.0, 356.0, 906.0);
        staticElements.add(pillar3);

        for (int i = 0; i < Math.min(3, sortedPlayers.size()); i++) {
            Player p = sortedPlayers.get(i);
            animatedScores.put(p, 0);
            targetScores.put(p, p.getScore());
        }

        SoundManager.playSound("res/sfx/Score Fill.wav");
        startScoreAnimation(sortedPlayers);
        setupCustomCursor();
        
        if (gameClient != null) {
            Timer disconnectTimer = new Timer(3000, e -> {
                gameClient.disconnect();
            });
            disconnectTimer.setRepeats(false);
            disconnectTimer.start();
        }

        setTitle("Game Over - Results");
        setSize(config.width, config.height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);

        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (backgroundImage != null) {
                    g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2d.setColor(config.backgroundColor);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }

                for (MenuElement element : staticElements) {
                    element.render(g2d);
                }

                double[] cursorX = {790.0, 430.0, 1214.0};
                double[] cursorY = {428.0, 627.0, 562.0};
                double cursorSize = 132.1;
                
                double[] textX = {950.0, 565.0, 1336.0};
                double[] textY = {420.0, 619.0, 547.0};

                for (int i = 0; i < Math.min(3, sortedPlayers.size()); i++) {
                    Player p = sortedPlayers.get(i);
                    
                    try {
                        int playerNumber = Integer.parseInt(p.getId().substring(1));
                        if (playerNumber > 0 && playerNumber <= playerCursors.length 
                            && playerCursors[playerNumber - 1] != null) {
                            BufferedImage cursor = playerCursors[playerNumber - 1];
                            g2d.drawImage(cursor, (int)(cursorX[i] - cursorSize/2), 
                                (int)(cursorY[i] - cursorSize/2), (int)cursorSize, (int)cursorSize, null);
                        }
                    } catch (Exception e) {
                    }

                    int score = animatedScores.getOrDefault(p, 0);
                    String scoreText = p.getId();
                    if (p.getId().equals(myPlayerId)) {
                        scoreText = "[ME] " + scoreText;
                    }
                    scoreText += " [" + score + " แต้ม]";
                    
                    MenuElement textElement = new MenuElement(scoreText, textX[i], textY[i], 32);
                    textElement.setTextColor(new Color(51, 51, 51));
                    textElement.render(g2d);
                }

                if (backHover) {
                    MenuElement backButton = new MenuElement(MenuElement.ElementType.IMAGE, 
                        "res/button/Button-Small-Pink.png", 205.0, 123.0, 367.0, 196.0);
                    backButton.render(g2d);
                } else {
                    MenuElement backButton = new MenuElement(MenuElement.ElementType.IMAGE, 
                        "res/button/Button-Small-Blue.png", 205.0, 123.0, 367.0, 196.0);
                    backButton.render(g2d);
                }

                MenuElement backArrow = new MenuElement(MenuElement.ElementType.IMAGE, 
                    "res/button/Back-Arrow.png", 194.0, 120.0, 167.0, 123.0);
                backArrow.render(g2d);
                
                g2d.setColor(new Color(100, 100, 100, 180));
                g2d.setFont(FontManager.getThaiFont(18));
                String versionText = "v " + GitVersion.getVersion();
                g2d.drawString(versionText, 15, getHeight() - 15);
            }
        };
        contentPanel.setDoubleBuffered(true);

        if (normalCursor != null) {
            contentPanel.setCursor(normalCursor);
        }

        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (pressedCursor != null) {
                    contentPanel.setCursor(pressedCursor);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (normalCursor != null) {
                    contentPanel.setCursor(normalCursor);
                }
            }


            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (isInsideButton(x, y, 205.0, 123.0, 367.0, 196.0)) {
                    if (gameClient != null) {
                        gameClient.disconnect();
                    }
                    dispose();
                    SwingUtilities.invokeLater(MainMenu::new);
                }
            }
        });

        contentPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                boolean wasBackHover = backHover;
                backHover = isInsideButton(x, y, 205.0, 123.0, 367.0, 196.0);

                if (!wasBackHover && backHover) {
                    SoundManager.playSound("res/sfx/UI_Click_Organic_mono.wav");
                }

                if (wasBackHover != backHover) {
                    contentPanel.repaint();
                }
            }
        });

        add(contentPanel);
        setVisible(true);
    }

    private void startScoreAnimation(List<Player> sortedPlayers) {
        animationTimer = new Timer(GameConfig.GAMEOVER_ANIMATION_INTERVAL, e -> {
            boolean allComplete = true;

            for (int i = 0; i < Math.min(3, sortedPlayers.size()); i++) {
                Player p = sortedPlayers.get(i);
                
                int current = animatedScores.get(p);
                int target = targetScores.get(p);

                if (current < target) {
                    int increment = Math.max(1, (target - current) / 10);
                    current = Math.min(current + increment, target);
                    animatedScores.put(p, current);
                    allComplete = false;
                }
            }

            contentPanel.repaint();

            if (allComplete) {
                animationTimer.stop();
            }
        });
        animationTimer.start();
    }

    private boolean isInsideButton(int mouseX, int mouseY, double btnX, double btnY, double width, double height) {
        return mouseX >= btnX - width / 2 && mouseX <= btnX + width / 2 &&
               mouseY >= btnY - height / 2 && mouseY <= btnY + height / 2;
    }

    @Override
    public void dispose() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        super.dispose();
    }
    
    private void setupCustomCursor() {
        try {
            BufferedImage normalImage = ImageIO.read(new File("res/pointer/5_pointer_USE-THIS-ONE_0.png"));
            BufferedImage pressedImage = ImageIO.read(new File("res/pointer/6_pointer-press_USE-THIS-ONE_0.png"));

            int cursorSize = 32;
            BufferedImage scaledNormal = new BufferedImage(cursorSize, cursorSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledNormal.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(normalImage, 0, 0, cursorSize, cursorSize, null);
            g2d.dispose();

            BufferedImage scaledPressed = new BufferedImage(cursorSize, cursorSize, BufferedImage.TYPE_INT_ARGB);
            g2d = scaledPressed.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(pressedImage, 0, 0, cursorSize, cursorSize, null);
            g2d.dispose();

            normalCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                scaledNormal, new Point(0, 0), "normal cursor");
            pressedCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                scaledPressed, new Point(0, 0), "pressed cursor");
        } catch (Exception e) {
        }
    }

}



