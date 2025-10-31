import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class MainMenu extends JFrame {
    private PopupWindow mainWindow;
    
    public MainMenu() {
        PopupWindowConfig config = new PopupWindowConfig();
        config.width = 1280;
        config.height = 720;
        config.backgroundColor = new Color(102, 102, 102);
        config.useBackgroundImage = false;
        
        ArrayList<MenuElement> elements = new ArrayList<>();
        
        MenuElement img1 = new MenuElement(MenuElement.ElementType.IMAGE, "res/1.png", 635.0, 376.0, 625.5, 582.4);
        elements.add(img1);
        
        MenuElement text = new MenuElement("HEAD HUNT", 630.0, 214.0, 103);
        text.setTextColor(new Color(0, 0, 0));
        elements.add(text);
        
        MenuElement startButton = new MenuElement(MenuElement.ElementType.IMAGE, "res/4.png", 640.0, 339.0, 260.0, 75.0);
        elements.add(startButton);
        
        MenuElement exitButton = new MenuElement(MenuElement.ElementType.IMAGE, "res/5.png", 644.0, 452.0, 260.0, 75.0);
        elements.add(exitButton);
        
        mainWindow = new PopupWindow(config, elements);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                
                if (isInsideButton(x, y, 640.0, 339.0, 260.0, 75.0)) {
                    mainWindow.dispose();
                    showGameModeSelection();
                } else if (isInsideButton(x, y, 644.0, 452.0, 260.0, 75.0)) {
                    System.exit(0);
                }
            }
        });
        
        mainWindow.setVisible(true);
    }
    
    private boolean isInsideButton(int mouseX, int mouseY, double btnX, double btnY, double width, double height) {
        return mouseX >= btnX - width/2 && mouseX <= btnX + width/2 &&
               mouseY >= btnY - height/2 && mouseY <= btnY + height/2;
    }
    
    private void showGameModeSelection() {
        UIManager.put("OptionPane.messageFont", FontManager.getFont(14));
        UIManager.put("OptionPane.buttonFont", FontManager.getFont(14));
        
        String[] options = { "Server", "Client" };
        int choice = JOptionPane.showOptionDialog(
                null,
                "เลือกโหมด",
                "Head Hunt Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if (choice == 0) {
            new Thread(() -> GameServer.main(new String[]{})).start();
            JOptionPane.showMessageDialog(null, "Server เริ่มทำงานแล้ว!");
        } else if (choice == 1) {
            SwingUtilities.invokeLater(GameClient::new);
        } else {
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainMenu::new);
    }
}
