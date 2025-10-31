import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class LobbyScreen extends JFrame {
    private JPanel contentPanel;
    private PopupWindowConfig config;
    private BufferedImage backgroundImage;
    private List<String> playerIds = new ArrayList<>();
    private String myPlayerId;
    private Cursor normalCursor;
    private Cursor pressedCursor;
    private boolean backHover = false;
    private boolean gameStarting = false;
    private boolean gameInProgress = false;
    private Runnable onExitCallback;
    private int teamHover = -1;
    private float[] teamScales = {1.0f, 1.0f, 1.0f, 1.0f};
    private float[] targetScales = {1.0f, 1.0f, 1.0f, 1.0f};
    private Timer scaleAnimationTimer;

    public LobbyScreen(String myPlayerId) {
        this.myPlayerId = myPlayerId;
        config = new PopupWindowConfig();
        config.width = 1600;
        config.height = 900;
        config.backgroundColor = new Color(240, 240, 240);
        config.useBackgroundImage = true;
        config.backgroundImagePath = "res/bg_menu.png";

        loadBackgroundImage();
        setupCustomCursor();
        playerIds.add(myPlayerId);
        startScaleAnimation();

        createWindow();
    }
    
    private void startScaleAnimation() {
        scaleAnimationTimer = new Timer(16, e -> {
            boolean needsRepaint = false;
            for (int i = 0; i < 4; i++) {
                if (Math.abs(teamScales[i] - targetScales[i]) > 0.001f) {
                    teamScales[i] += (targetScales[i] - teamScales[i]) * 0.2f;
                    needsRepaint = true;
                }
            }
            if (needsRepaint && contentPanel != null) {
                contentPanel.repaint();
            }
        });
        scaleAnimationTimer.start();
    }

    private void loadBackgroundImage() {
        if (config.useBackgroundImage && config.backgroundImagePath != null) {
            try {
                backgroundImage = ImageIO.read(new File(config.backgroundImagePath));
            } catch (Exception e) {
            }
        }
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

    private void createWindow() {
        setTitle("Lobby - Monster Pop Arena");
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

                MenuElement notepad = new MenuElement(MenuElement.ElementType.IMAGE, 
                    "res/QuestNotepad_Small.png", 807.0, 128.0, 627.5, 227.4);
                notepad.render(g2d);

                MenuElement titleText;
                if (gameInProgress) {
                    titleText = new MenuElement("เกมเริ่มไปแล้ว กรุณารอจนกว่าเกมจะจบ", 450.0, 146.0, 40);
                } else if (gameStarting) {
                    titleText = new MenuElement("กำลังเริ่มเกม...", 809.0, 146.0, 101);
                } else {
                    titleText = new MenuElement("รอผู้เล่นครบ", 809.0, 146.0, 101);
                }
                titleText.setTextColor(new Color(51, 51, 51));
                titleText.render(g2d);

                double[] playerXPositions = {587.0, 727.0, 866.0, 1000.0};
                double[] playerYPositions = {446.0, 447.0, 450.0, 451.0};
                double[] playerSizes = {107.5, 108.0, 109.7, 115.5};
                double[] textXPositions = {586.0, 698.0, 835.0, 967.0};
                double[] textYPositions = {510.0, 510.0, 514.0, 515.0};

                for (int i = 0; i < GameConfig.MAX_PLAYERS; i++) {
                    if (i < playerIds.size()) {
                        String playerId = playerIds.get(i);
                        
                        int playerNumber = Integer.parseInt(playerId.substring(1));
                        
                        MenuElement playerImg = new MenuElement(MenuElement.ElementType.IMAGE, 
                            "res/players/" + playerNumber + ".png", 
                            playerXPositions[i], playerYPositions[i], 
                            playerSizes[i], playerSizes[i]);
                        playerImg.render(g2d);

                        String playerLabel = playerId;
                        if (playerId.equals(myPlayerId)) {
                            playerLabel += " [me]";
                        }
                        MenuElement playerText = new MenuElement(playerLabel, textXPositions[i], textYPositions[i], 32);
                        playerText.setTextColor(new Color(51, 51, 51));
                        playerText.render(g2d);
                    }
                }

                MenuElement countButton = new MenuElement(MenuElement.ElementType.IMAGE, 
                    "res/button/Button-Small-Blue.png", 812.0, 777.0, 367.0, 196.0);
                countButton.render(g2d);

                MenuElement countText = new MenuElement(
                    playerIds.size() + "/" + GameConfig.MAX_PLAYERS, 808.0, 775.0, 124);
                countText.setTextColor(new Color(255, 255, 255));
                countText.render(g2d);

                MenuElement paperStack = new MenuElement(MenuElement.ElementType.IMAGE, 
                    "res/Paper-Stack.png", 1413.0, 462.0, 698.2, 940.1);
                paperStack.render(g2d);

                MenuElement blueBird = new MenuElement(MenuElement.ElementType.IMAGE, 
                    "res/head/BlueBird/1.png", 1108.0, 112.0, 64.8, 49.7);
                blueBird.render(g2d);

                MenuElement howToTitle = new MenuElement("การเล่น", 1339.0, 37.0, 32);
                howToTitle.setTextColor(new Color(0, 0, 0));
                howToTitle.render(g2d);

                MenuElement howToText1 = new MenuElement("โจมตีมอนเตอร์ให้ได้มากที่สุด", 1363.0, 88.0, 36);
                howToText1.setTextColor(new Color(0, 0, 0));
                howToText1.render(g2d);

                MenuElement howToText2 = new MenuElement("ระวังมอนเตอร์ระเบิด", 1352.0, 147.0, 32);
                howToText2.setTextColor(new Color(0, 0, 0));
                howToText2.render(g2d);

                MenuElement devText = new MenuElement("Developer", 1352.0, 250.0, 28);
                devText.setTextColor(new Color(0, 0, 0));
                devText.render(g2d);

                MenuElement skull = new MenuElement(MenuElement.ElementType.IMAGE, 
                    "res/head/Skull/3.png", 1502.0, 156.0, 50.0, 50.0);
                skull.render(g2d);

                MenuElement exclamation = new MenuElement(MenuElement.ElementType.IMAGE, 
                    "res/icon/peonparticle-explanationmark.png", 1211.0, 140.0, 60.1, 75.5);
                exclamation.render(g2d);

                MenuElement duck = new MenuElement(MenuElement.ElementType.IMAGE, 
                    "res/head/Duck/10.png", 1107.0, 810.0, 75.6, 75.6);
                duck.render(g2d);

                double[][] teamPositions = {
                    {1215.0, 387.0, 175.2, 261.9},
                    {1448.0, 387.0, 182.6, 272.9},
                    {1217.0, 673.0, 179.0, 267.6},
                    {1449.0, 675.0, 178.5, 266.8}
                };
                
                String[] teamPaths = {
                    "res/teams/1.png",
                    "res/teams/4.png",
                    "res/teams/2.png",
                    "res/teams/3.png"
                };
                
                for (int i = 0; i < 4; i++) {
                    double x = teamPositions[i][0];
                    double y = teamPositions[i][1];
                    double w = teamPositions[i][2];
                    double h = teamPositions[i][3];
                    
                    float scale = teamScales[i];
                    MenuElement team = new MenuElement(MenuElement.ElementType.IMAGE, 
                        teamPaths[i], x, y, w * scale, h * scale);
                    team.render(g2d);
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
            }
        };

        if (normalCursor != null) {
            contentPanel.setCursor(normalCursor);
        }

        contentPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mousePressed(java.awt.event.MouseEvent e) {
                if (pressedCursor != null) {
                    contentPanel.setCursor(pressedCursor);
                }
                SoundManager.playSound("res/sfx/Button Click 1.wav");
            }

            @Override
            public void mouseReleased(java.awt.event.MouseEvent e) {
                if (normalCursor != null) {
                    contentPanel.setCursor(normalCursor);
                }
            }

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                if (isInsideButton(x, y, 205.0, 123.0, 367.0, 196.0)) {
                    if (onExitCallback != null) {
                        onExitCallback.run();
                    }
                    dispose();
                    SwingUtilities.invokeLater(MainMenu::new);
                }
            }
        });

        contentPanel.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                boolean wasBackHover = backHover;
                backHover = isInsideButton(x, y, 205.0, 123.0, 367.0, 196.0);

                if (!wasBackHover && backHover) {
                    SoundManager.playSound("res/sfx/UI_Click_Organic_mono.wav");
                }

                int oldTeamHover = teamHover;
                teamHover = -1;
                
                double[][] teamPositions = {
                    {1215.0, 387.0, 175.2, 261.9},
                    {1448.0, 387.0, 182.6, 272.9},
                    {1217.0, 673.0, 179.0, 267.6},
                    {1449.0, 675.0, 178.5, 266.8}
                };
                
                for (int i = 0; i < 4; i++) {
                    if (isInsideButton(x, y, teamPositions[i][0], teamPositions[i][1], 
                                      teamPositions[i][2], teamPositions[i][3])) {
                        teamHover = i;
                        break;
                    }
                }

                for (int i = 0; i < 4; i++) {
                    targetScales[i] = (teamHover == i) ? 1.08f : 1.0f;
                }

                if (oldTeamHover != teamHover && teamHover != -1) {
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

    private boolean isInsideButton(int mouseX, int mouseY, double btnX, double btnY, double width, double height) {
        return mouseX >= btnX - width / 2 && mouseX <= btnX + width / 2 &&
               mouseY >= btnY - height / 2 && mouseY <= btnY + height / 2;
    }

    public void addPlayer(String playerId) {
        if (!playerIds.contains(playerId) && playerIds.size() < GameConfig.MAX_PLAYERS) {
            playerIds.add(playerId);
            SwingUtilities.invokeLater(() -> contentPanel.repaint());
        }
    }

    public void removePlayer(String playerId) {
        playerIds.remove(playerId);
        this.gameStarting = false;
        this.gameInProgress = false;
        SwingUtilities.invokeLater(() -> contentPanel.repaint());
    }

    public int getPlayerCount() {
        return playerIds.size();
    }

    public void setGameStarting(boolean starting) {
        if (this.gameStarting != starting) {
            this.gameStarting = starting;
            if (starting) {
                this.gameInProgress = false;
            }
            if (contentPanel != null) {
                SwingUtilities.invokeLater(() -> contentPanel.repaint());
            }
        }
    }
    
    public void setGameInProgress(boolean inProgress) {
        if (this.gameInProgress != inProgress) {
            this.gameInProgress = inProgress;
            if (inProgress) {
                this.gameStarting = false;
            }
            if (contentPanel != null) {
                SwingUtilities.invokeLater(() -> contentPanel.repaint());
            }
        }
    }

    public void setOnExitCallback(Runnable callback) {
        this.onExitCallback = callback;
    }
    
    @Override
    public void dispose() {
        if (scaleAnimationTimer != null) {
            scaleAnimationTimer.stop();
        }
        super.dispose();
    }
}

