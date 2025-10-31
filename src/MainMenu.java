import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MainMenu {
    private JFrame mainWindow;
    private JPanel contentPanel;
    private ArrayList<MenuElement> elements;
    private PopupWindowConfig config;
    private MenuElement startButton;
    private MenuElement exitButton;
    private boolean startHover = false;
    private boolean exitHover = false;
    
    public MainMenu() {
        config = new PopupWindowConfig();
        config.width = 1280;
        config.height = 720;
        config.backgroundColor = new Color(102, 102, 102);
        config.useBackgroundImage = false;
        
        elements = new ArrayList<>();
        
        MenuElement img1 = new MenuElement(MenuElement.ElementType.IMAGE, "res/1.png", 635.0, 376.0, 625.5, 582.4);
        elements.add(img1);
        
        MenuElement text = new MenuElement("HEAD HUNT", 630.0, 214.0, 103);
        text.setTextColor(new Color(0, 0, 0));
        elements.add(text);
        
        startButton = new MenuElement(MenuElement.ElementType.IMAGE, "res/4.png", 640.0, 339.0, 260.0, 75.0);
        elements.add(startButton);
        
        exitButton = new MenuElement(MenuElement.ElementType.IMAGE, "res/5.png", 644.0, 452.0, 260.0, 75.0);
        elements.add(exitButton);
        
        createWindow();
    }
    
    private void createWindow() {
        mainWindow = new JFrame("Head Hunt Game");
        mainWindow.setSize(config.width, config.height);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setResizable(false);
        
        contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(config.backgroundColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                for (int i = 0; i < elements.size(); i++) {
                    MenuElement element = elements.get(i);
                    
                    if (element == startButton && startHover) {
                        drawScaledElement(g2d, element, 1.1);
                    } else if (element == exitButton && exitHover) {
                        drawScaledElement(g2d, element, 1.1);
                    } else {
                        element.render(g2d);
                    }
                }
            }
            
            private void drawScaledElement(Graphics2D g2d, MenuElement element, double scale) {
                double x = element.getX();
                double y = element.getY();
                double w = element.getWidth() * scale;
                double h = element.getHeight() * scale;
                
                MenuElement scaled = new MenuElement(MenuElement.ElementType.IMAGE, 
                    element.getImagePath(), x, y, w, h);
                scaled.render(g2d);
            }
        };
        
        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                
                if (isInsideButton(x, y, 640.0, 339.0, 260.0, 75.0)) {
                    mainWindow.dispose();
                    SwingUtilities.invokeLater(GameClient::new);
                } else if (isInsideButton(x, y, 644.0, 452.0, 260.0, 75.0)) {
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
                
                startHover = isInsideButton(x, y, 640.0, 339.0, 260.0, 75.0);
                exitHover = isInsideButton(x, y, 644.0, 452.0, 260.0, 75.0);
                
                if (wasStartHover != startHover || wasExitHover != exitHover) {
                    contentPanel.repaint();
                }
            }
        });
        
        mainWindow.add(contentPanel);
        mainWindow.setVisible(true);
    }
    
    private boolean isInsideButton(int mouseX, int mouseY, double btnX, double btnY, double width, double height) {
        return mouseX >= btnX - width/2 && mouseX <= btnX + width/2 &&
               mouseY >= btnY - height/2 && mouseY <= btnY + height/2;
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}
