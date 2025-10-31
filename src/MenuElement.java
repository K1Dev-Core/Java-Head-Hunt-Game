import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class MenuElement {
    public enum ElementType {
        IMAGE, TEXT
    }

    private ElementType type;
    private String imagePath;
    private String text;
    private double x;
    private double y;
    private double width;
    private double height;
    private Font textFont;
    private Color textColor;
    private boolean selected;
    private BufferedImage image;

    public MenuElement(ElementType type, String imagePath, double x, double y, double width, double height) {
        this.type = type;
        this.imagePath = imagePath;
        this.x = x;
        this.y = y;
        this.selected = false;
        loadImage();
        if (width == 0 && height == 0 && image != null) {
            this.width = image.getWidth();
            this.height = image.getHeight();
        } else {
            this.width = width;
            this.height = height;
        }
    }

    public MenuElement(String text, double x, double y, int fontSize) {
        this.type = ElementType.TEXT;
        this.text = text;
        this.x = x;
        this.y = y;
        this.textFont = FontManager.getThaiFont(Font.BOLD, fontSize);
        this.textColor = Color.BLACK;
        this.selected = false;
        calculateTextBounds();
    }

    private void loadImage() {
        if (imagePath != null) {
            try {
                String fullPath = System.getProperty("user.dir") + File.separator + imagePath;
                File imageFile = new File(fullPath);
                if (imageFile.exists()) {
                    image = ImageIO.read(imageFile);
                }
            } catch (Exception e) {
                System.err.println("Cannot load image: " + imagePath);
            }
        }
    }

    private void calculateTextBounds() {
        if (text != null && textFont != null) {
            BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = tempImage.createGraphics();
            FontMetrics fm = g2d.getFontMetrics(textFont);
            this.width = fm.stringWidth(text);
            this.height = fm.getHeight();
            g2d.dispose();
        }
    }

    public void render(Graphics2D g2d) {
        if (type == ElementType.IMAGE && image != null) {
            g2d.drawImage(image, (int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height, null);
            if (selected) {
                g2d.setColor(Color.CYAN);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect((int) (x - width / 2), (int) (y - height / 2), (int) width, (int) height);
            }
        } else if (type == ElementType.TEXT && text != null) {
            g2d.setFont(textFont);
            g2d.setColor(textColor);
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(text);
            int textHeight = fm.getHeight();
            g2d.drawString(text, (int) (x - textWidth / 2), (int) (y + fm.getAscent() / 2));
            if (selected) {
                g2d.setColor(Color.CYAN);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect((int) (x - textWidth / 2), (int) (y - textHeight / 2), textWidth, textHeight);
            }
        }
    }

    public boolean contains(double px, double py) {
        if (type == ElementType.IMAGE) {
            return px >= x - width / 2 && px <= x + width / 2 &&
                    py >= y - height / 2 && py <= y + height / 2;
        } else if (type == ElementType.TEXT) {
            return px >= x - width / 2 && px <= x + width / 2 &&
                    py >= y - height / 2 && py <= y + height / 2;
        }
        return false;
    }

    public ElementType getType() {
        return type;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getText() {
        return text;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Font getTextFont() {
        return textFont;
    }

    public Color getTextColor() {
        return textColor;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setTextFont(Font font) {
        this.textFont = font;
        calculateTextBounds();
    }

    public void setTextColor(Color color) {
        this.textColor = color;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
