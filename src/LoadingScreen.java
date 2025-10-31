import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class LoadingScreen extends JFrame {
    private LoadingPanel loadingPanel;
    
    public LoadingScreen() {
        initializeWindow();
        createLoadingPanel();
        pack();
        centerWindow();
        setupCustomCursor();
    }
    
    private void setupCustomCursor() {
        try {
            BufferedImage normalImage = ImageIO.read(new File("res/pointer/5_pointer_USE-THIS-ONE_0.png"));
            int cursorSize = 32;
            BufferedImage scaledNormal = new BufferedImage(cursorSize, cursorSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaledNormal.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(normalImage, 0, 0, cursorSize, cursorSize, null);
            g2d.dispose();
            
            Cursor normalCursor = Toolkit.getDefaultToolkit().createCustomCursor(
                scaledNormal, new Point(0, 0), "normal cursor");
            setCursor(normalCursor);
        } catch (Exception e) {
        }
    }
    
    private void initializeWindow() {
        setTitle("Monster Pop Arena - Loading");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
    }
    
    private void createLoadingPanel() {
        loadingPanel = new LoadingPanel();
        add(loadingPanel);
    }
    
    private void centerWindow() {
        setLocationRelativeTo(null);
    }
    
    private class LoadingPanel extends JPanel {
        private BufferedImage backgroundImage;
        private BufferedImage logoImage;
        private ArrayList<BufferedImage> iconFrames;
        private int currentIconIndex = 0;
        private long lastFrameTime;
        private long frameDelay = 300;
        private boolean showAnimation;
        private long animationStartTime;
        private long delayAfterAnimation = 5000;
        private boolean inWaitingState;
        private boolean menuOpened = false;
        private long animationCompleteTime;
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
        
        public LoadingPanel() {
            setPreferredSize(new Dimension(GameConfig.WINDOW_WIDTH, GameConfig.WINDOW_HEIGHT));
            setLayout(null);
            loadBackgroundImage();
            loadLogoImage();
            loadIconFrames();
            animationStartTime = System.currentTimeMillis() + 500;
            startAnimationTimer();
        }
        
        private void loadBackgroundImage() {
            try {
                backgroundImage = ImageIO.read(new File("res/bg_menu.png"));
            } catch (Exception ex) {
            }
        }
        
        private void loadLogoImage() {
            try {
                logoImage = ImageIO.read(new File("res/logo.png"));
            } catch (Exception ex) {
            }
        }
        
        private void loadIconFrames() {
            iconFrames = new ArrayList<>();
            for (String path : iconPaths) {
                try {
                    File file = new File(path);
                    if (file.exists()) {
                        BufferedImage image = ImageIO.read(file);
                        if (image != null) {
                            iconFrames.add(image);
                        }
                    }
                } catch (Exception ex) {
                }
            }
        }
        
        private void startAnimationTimer() {
            javax.swing.Timer timer = new javax.swing.Timer(30, e -> {
                long currentTime = System.currentTimeMillis();
                
                if (!showAnimation && currentTime >= animationStartTime) {
                    showAnimation = true;
                    inWaitingState = false;
                }
                
                if (showAnimation && !inWaitingState) {
                    if (currentTime - lastFrameTime >= frameDelay) {
                        if (iconFrames.size() > 0) {
                            currentIconIndex = (currentIconIndex + 1) % iconFrames.size();
                            lastFrameTime = currentTime;
                        }
                    }
                    
                    if (currentTime - animationStartTime >= delayAfterAnimation && !menuOpened) {
                        inWaitingState = true;
                        animationCompleteTime = currentTime;
                    }
                }
                
                if (inWaitingState && !menuOpened) {
                    long elapsed = currentTime - animationCompleteTime;
                    if (elapsed > 100) {
                        menuOpened = true;
                        closeAndOpenMenu();
                    }
                }
                
                repaint();
            });
            timer.start();
        }
        
        private void closeAndOpenMenu() {
            SwingUtilities.invokeLater(() -> {
                LoadingScreen.this.setVisible(false);
                LoadingScreen.this.dispose();
                new MainMenu();
            });
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            
            if (!showAnimation) {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                return;
            }
            
            if (backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
            } else {
                g2d.setColor(new Color(240, 240, 240));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
            
            if (logoImage != null) {
                int logoWidth = 417;
                int logoHeight = 238;
                int logoX = (getWidth() - logoWidth) / 2;
                int logoY = 200;
                g2d.drawImage(logoImage, logoX, logoY, logoWidth, logoHeight, null);
            }
            
            if (iconFrames != null && iconFrames.size() > 0 && currentIconIndex >= 0 && currentIconIndex < iconFrames.size()) {
                BufferedImage currentIcon = iconFrames.get(currentIconIndex);
                if (currentIcon != null) {
                    int iconSize = 100;
                    int iconX = (getWidth() - iconSize) / 2;
                    int iconY = 500;
                    g2d.drawImage(currentIcon, iconX, iconY, iconSize, iconSize, null);
                }
            }
            
            g2d.setColor(new Color(51, 51, 51));
            g2d.setFont(FontManager.getThaiFont(42));
            String loadingText = "กำลังโหลด...";
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(loadingText);
            int textX = (getWidth() - textWidth) / 2;
            int textY = 650;
            g2d.drawString(loadingText, textX, textY);
            
            int dots = (int)((System.currentTimeMillis() / 500) % 4);
            String dotText = ".".repeat(dots);
            g2d.setFont(FontManager.getThaiFont(48));
            g2d.drawString(dotText, textX + textWidth + 10, textY);
            
            g2d.setColor(new Color(100, 100, 100));
            g2d.setFont(FontManager.getThaiFont(24));
            String creditText = "Monster Pop Arena";
            int creditWidth = g2d.getFontMetrics().stringWidth(creditText);
            g2d.drawString(creditText, (getWidth() - creditWidth) / 2, getHeight() - 50);
            
            g2d.setColor(new Color(100, 100, 100, 180));
            g2d.setFont(FontManager.getThaiFont(18));
            String versionText = "v " + GitVersion.getVersion();
            g2d.drawString(versionText, 15, getHeight() - 15);
        }
    }
    

}
