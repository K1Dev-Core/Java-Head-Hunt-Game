import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class GameOverScreen extends JFrame {
    private Map<Player, Integer> animatedScores = new HashMap<>();
    private Map<Player, Integer> targetScores = new HashMap<>();
    private Timer animationTimer;
    private List<MenuElement> dynamicTextElements = new ArrayList<>();
    private ArrayList<MenuElement> staticElements;
    private PopupWindowConfig config;
    private String myPlayerId;
    private int[] yPositions = { 299, 372, 444, 517 };

    public GameOverScreen(List<Player> sortedPlayers, String myPlayerId) {
        this.myPlayerId = myPlayerId;
        config = new PopupWindowConfig();
        config.width = 1280;
        config.height = 720;
        config.backgroundColor = new Color(102, 102, 102);
        config.useBackgroundImage = false;

        staticElements = new ArrayList<>();

        MenuElement img1 = new MenuElement(MenuElement.ElementType.IMAGE, "res/head/Hit 2 (36x30)-0.png", 279.0, 207.0,
                334.2, 278.5);
        staticElements.add(img1);

        MenuElement img2 = new MenuElement(MenuElement.ElementType.IMAGE, "res/3.png", 643.0, 362.0, 589.6, 602.2);
        staticElements.add(img2);

        MenuElement img3 = new MenuElement(MenuElement.ElementType.IMAGE, "res/5.png", 1232.0, 33.0, 52.0, 15.0);
        staticElements.add(img3);

        MenuElement titleText = new MenuElement("SCORE", 644.0, 220.0, 70);
        titleText.setTextColor(new Color(0, 0, 0));
        staticElements.add(titleText);

        for (int i = 0; i < Math.min(4, sortedPlayers.size()); i++) {
            Player p = sortedPlayers.get(i);
            animatedScores.put(p, 0);
            targetScores.put(p, p.getScore());
        }

        MenuElement exitButton = new MenuElement(MenuElement.ElementType.IMAGE, "res/5.png", 1176.0, 675.0, 161.7,
                46.7);
        staticElements.add(exitButton);

        MenuElement img4 = new MenuElement(MenuElement.ElementType.IMAGE, "res/head/Jump (36x36).png", 973.0, 588.0,
                180.0, 180.0);
        staticElements.add(img4);

        SoundManager.playSound("res/sfx/Score Fill.wav");
        startScoreAnimation(sortedPlayers);

        setTitle("Game Over - Results");
        setSize(config.width, config.height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(config.backgroundColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                for (MenuElement element : staticElements) {
                    element.render(g2d);
                }
                
                for (MenuElement element : dynamicTextElements) {
                    element.render(g2d);
                }
                
                if (exitHover) {
                    MenuElement scaled = new MenuElement(MenuElement.ElementType.IMAGE, 
                        exitButton.getImagePath(), exitButton.getX(), exitButton.getY(), 
                        exitButton.getWidth() * 1.1, exitButton.getHeight() * 1.1);
                    scaled.render(g2d);
                } else {
                    exitButton.render(g2d);
                }
            }
        };
        contentPanel.setDoubleBuffered(true);

        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                
                if (isInsideButton(x, y, 640.0, 600.0, 200.0, 60.0)) {
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
                
                boolean wasHover = exitHover;
                exitHover = isInsideButton(x, y, 640.0, 600.0, 200.0, 60.0);
                
                if (!wasHover && exitHover) {
                    SoundManager.playSound("res/sfx/UI_Click_Organic_mono.wav");
                }
                
                if (wasHover != exitHover) {
                    contentPanel.repaint();
                }
            }
        });

        add(contentPanel);
        setVisible(true);
    }

    private void startScoreAnimation(List<Player> sortedPlayers) {
        animationTimer = new Timer(20, e -> {
            boolean allComplete = true;
            dynamicTextElements.clear();

            int index = 0;
            for (Player p : sortedPlayers) {
                if (index >= 4)
                    break;

                int current = animatedScores.get(p);
                int target = targetScores.get(p);

                if (current < target) {
                    int increment = Math.max(1, (target - current) / 10);
                    current = Math.min(current + increment, target);
                    animatedScores.put(p, current);
                    allComplete = false;
                }

                String playerText = p.getId() + " : " + current;
                if (p.getId().equals(myPlayerId)) {
                    playerText += " (YOU)";
                }

                MenuElement playerElement = new MenuElement(playerText, 498.0, yPositions[index], 32);
                playerElement.setTextColor(new Color(0, 0, 0));
                dynamicTextElements.add(playerElement);

                index++;
            }

            repaint();

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
}
