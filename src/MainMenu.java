import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.ArrayList;

public class MainMenu {
    private JFrame mainWindow;
    private JPanel contentPanel;
    private ArrayList<MenuElement> elements;
    private PopupWindowConfig config;
    private BufferedImage backgroundImage;
    private boolean startHover = false;
    private boolean exitHover = false;
    private String[] iconPaths = {
        "res/icon/Emote_Gossip.png",
        "res/icon/Emote_Happy-innocent.png",
        "res/icon/Emote_Love.png",
        "res/icon/Emote_Hah.png",
        "res/icon/Emote_JustRight.png",
        "res/icon/Emote_Sad.png",
        "res/icon/Emote_No-thanks.png",
        "res/icon/Emote_Plotting.png",
        "res/icon/Emote_puking.png",
        "res/icon/Emote_Joy.png",
        "res/icon/Emote_Nervous.png",
        "res/icon/Emote_Hm.png"
    };
    private int currentIconIndex = 0;
    private Cursor normalCursor;
    private Cursor pressedCursor;

    public MainMenu() {
        config = new PopupWindowConfig();
        config.width = 1600;
        config.height = 900;
        config.backgroundColor = new Color(240, 240, 240);
        config.useBackgroundImage = true;
        config.backgroundImagePath = "res/bg_menu.png";
        loadBackgroundImage();
        setupCustomCursor();

        elements = new ArrayList<>();

        MenuElement logo = new MenuElement(MenuElement.ElementType.IMAGE, "res/logo.png", 233.0, 161.0, 417.1, 238.3);
        elements.add(logo);

        MenuElement startButton = new MenuElement(MenuElement.ElementType.IMAGE, "res/button/Button-Medium-Blue.png", 800.0, 276.0, 540.0, 195.0);
        elements.add(startButton);

        MenuElement startText = new MenuElement("เริ่มเกม", 804.0, 292.0, 116);
        startText.setTextColor(new Color(255, 255, 255));
        elements.add(startText);

        MenuElement aboutButton = new MenuElement(MenuElement.ElementType.IMAGE, "res/button/Button-Medium-Gray.png", 796.0, 511.0, 540.0, 195.0);
        elements.add(aboutButton);

        MenuElement aboutText = new MenuElement("เกี่ยวกับ", 798.0, 518.0, 112);
        aboutText.setTextColor(new Color(255, 255, 255));
        elements.add(aboutText);

        MenuElement exitButton = new MenuElement(MenuElement.ElementType.IMAGE, "res/button/Button-Medium-Blue.png", 797.0, 750.0, 540.0, 195.0);
        elements.add(exitButton);

        MenuElement exitText = new MenuElement("ออก", 790.0, 754.0, 112);
        exitText.setTextColor(new Color(255, 255, 255));
        elements.add(exitText);

        createWindow();
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
        mainWindow = new JFrame("Monster Pop Arena");
        mainWindow.setSize(config.width, config.height);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setResizable(false);
        mainWindow.setUndecorated(true);

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

                for (MenuElement element : elements) {
                    element.render(g2d);
                }

                MenuElement animatedIcon = new MenuElement(MenuElement.ElementType.IMAGE, 
                    iconPaths[currentIconIndex], 429.0, 178.0, 132.8, 132.8);
                animatedIcon.render(g2d);

                if (startHover) {
                    MenuElement hoverButton = new MenuElement(MenuElement.ElementType.IMAGE, 
                        "res/button/Button-Medium-Pink.png", 800.0, 276.0, 540.0, 195.0);
                    hoverButton.render(g2d);
                    MenuElement startText = new MenuElement("เริ่มเกม", 804.0, 292.0, 116);
                    startText.setTextColor(new Color(255, 255, 255));
                    startText.render(g2d);
                }

                if (exitHover) {
                    MenuElement hoverButton = new MenuElement(MenuElement.ElementType.IMAGE, 
                        "res/button/Button-Medium-Pink.png", 797.0, 750.0, 540.0, 195.0);
                    hoverButton.render(g2d);
                    MenuElement exitText = new MenuElement("ออก", 790.0, 754.0, 112);
                    exitText.setTextColor(new Color(255, 255, 255));
                    exitText.render(g2d);
                }
                
                g2d.setColor(new Color(100, 100, 100, 180));
                g2d.setFont(FontManager.getThaiFont(18));
                String versionText = "v " + GitVersion.getVersion();
                g2d.drawString(versionText, 15, getHeight() - 15);
            }
        };

        if (normalCursor != null) {
            contentPanel.setCursor(normalCursor);
        }

        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (pressedCursor != null) {
                    contentPanel.setCursor(pressedCursor);
                }
                SoundManager.playSound("res/sfx/Button Click 1.wav");
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

                if (isInsideButton(x, y, 800.0, 276.0, 540.0, 195.0)) {
                    mainWindow.dispose();
                    SwingUtilities.invokeLater(GameClient::new);
                } else if (isInsideButton(x, y, 797.0, 750.0, 540.0, 195.0)) {
                    System.exit(0);
                }
            }
        });

        contentPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();

                boolean wasStartHover = startHover;
                boolean wasExitHover = exitHover;

                startHover = isInsideButton(x, y, 800.0, 276.0, 540.0, 195.0);
                exitHover = isInsideButton(x, y, 797.0, 750.0, 540.0, 195.0);

                if (!wasStartHover && startHover) {
                    SoundManager.playSound("res/sfx/UI_Click_Organic_mono.wav");
                }
                if (!wasExitHover && exitHover) {
                    SoundManager.playSound("res/sfx/UI_Click_Organic_mono.wav");
                }

                if (wasStartHover != startHover || wasExitHover != exitHover) {
                    contentPanel.repaint();
                }
            }
        });

        WindowDragger dragger = new WindowDragger(mainWindow);
        contentPanel.addMouseListener(dragger);
        contentPanel.addMouseMotionListener(dragger);

        mainWindow.add(contentPanel);
        mainWindow.setVisible(true);

        Timer iconTimer = new Timer(300, e -> {
            currentIconIndex = (currentIconIndex + 1) % iconPaths.length;
            contentPanel.repaint();
        });
        iconTimer.start();
    }

    private boolean isInsideButton(int mouseX, int mouseY, double btnX, double btnY, double width, double height) {
        return mouseX >= btnX - width / 2 && mouseX <= btnX + width / 2 &&
                mouseY >= btnY - height / 2 && mouseY <= btnY + height / 2;
    }

 
}
