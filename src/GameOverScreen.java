import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GameOverScreen extends JFrame {
    
    public GameOverScreen(List<Player> sortedPlayers, String myPlayerId) {
        PopupWindowConfig config = new PopupWindowConfig();
        config.width = 1280;
        config.height = 720;
        config.backgroundColor = new Color(102, 102, 102);
        config.useBackgroundImage = false;
        
        ArrayList<MenuElement> elements = new ArrayList<>();
        
        MenuElement img1 = new MenuElement(MenuElement.ElementType.IMAGE, "res/head/Hit 2 (36x30)-0.png", 279.0, 207.0, 334.2, 278.5);
        elements.add(img1);
        
        MenuElement img2 = new MenuElement(MenuElement.ElementType.IMAGE, "res/3.png", 643.0, 362.0, 589.6, 602.2);
        elements.add(img2);
        
        MenuElement img3 = new MenuElement(MenuElement.ElementType.IMAGE, "res/5.png", 1232.0, 33.0, 52.0, 15.0);
        elements.add(img3);
        
        MenuElement titleText = new MenuElement("SCORE", 644.0, 220.0, 70);
        titleText.setTextColor(new Color(0, 0, 0));
        elements.add(titleText);
        
        int[] yPositions = {299, 372, 444, 517};
        for (int i = 0; i < Math.min(4, sortedPlayers.size()); i++) {
            Player p = sortedPlayers.get(i);
            String playerText = p.getId() + " : " + p.getScore();
            if (p.getId().equals(myPlayerId)) {
                playerText += " (YOU)";
            }
            
            MenuElement playerElement = new MenuElement(playerText, 498.0, yPositions[i], 32);
            playerElement.setTextColor(new Color(0, 0, 0));
            elements.add(playerElement);
        }
        
        MenuElement exitButton = new MenuElement(MenuElement.ElementType.IMAGE, "res/5.png", 1176.0, 675.0, 161.7, 46.7);
        elements.add(exitButton);
        
        MenuElement img4 = new MenuElement(MenuElement.ElementType.IMAGE, "res/head/Jump (36x36).png", 973.0, 588.0, 180.0, 180.0);
        elements.add(img4);
        
        setTitle("Game Over - Results");
        setSize(config.width, config.height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(config.backgroundColor);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                for (MenuElement element : elements) {
                    element.render(g2d);
                }
            }
        };
        
        contentPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                
                if (isInsideButton(x, y, 1176.0, 675.0, 161.7, 46.7)) {
                    dispose();
                }
            }
        });
        
        add(contentPanel);
        setVisible(true);
    }
    
    private boolean isInsideButton(int mouseX, int mouseY, double btnX, double btnY, double width, double height) {
        return mouseX >= btnX - width/2 && mouseX <= btnX + width/2 &&
               mouseY >= btnY - height/2 && mouseY <= btnY + height/2;
    }
}
