import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class PopupWindow extends JFrame {
    private PopupWindowConfig config;
    private ArrayList<MenuElement> elements;
    private BufferedImage backgroundImage;

    public PopupWindow(PopupWindowConfig config, ArrayList<MenuElement> elements) {
        this.config = config;
        this.elements = elements;

        setTitle("Popup Window");
        setSize(config.width, config.height);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        loadBackground();

        PopupPanel panel = new PopupPanel();
        add(panel);
    }

    private void loadBackground() {
        if (config.useBackgroundImage && config.backgroundImagePath != null) {
            try {
                String fullPath = System.getProperty("user.dir") + File.separator + config.backgroundImagePath;
                File imageFile = new File(fullPath);
                if (imageFile.exists()) {
                    backgroundImage = ImageIO.read(imageFile);
                }
            } catch (Exception ex) {
                System.err.println("Cannot load background: " + ex.getMessage());
            }
        }
    }

    class PopupPanel extends JPanel {
        public PopupPanel() {
            setPreferredSize(new Dimension(config.width, config.height));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (config.useBackgroundImage && backgroundImage != null) {
                g2d.drawImage(backgroundImage, 0, 0, config.width, config.height, null);
            } else {
                g2d.setColor(config.backgroundColor);
                g2d.fillRect(0, 0, config.width, config.height);
            }

            for (MenuElement element : elements) {
                element.render(g2d);
            }
        }
    }
}
