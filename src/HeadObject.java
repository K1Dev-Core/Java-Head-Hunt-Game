import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class HeadObject {
    private int id;
    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private String imagePath;
    private transient BufferedImage image;
    private static final double GRAVITY = 0.5;
    private static final double BOUNCE = 0.8;
    private int width;
    private int height;

    public HeadObject(int id, double x, double y, double velocityX, double velocityY, String imagePath) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.imagePath = imagePath;
        loadImage();
    }

    private void loadImage() {
        try {
            image = ImageIO.read(new File(imagePath));
            width = image.getWidth();
            height = image.getHeight();
        } catch (Exception e) {
            width = 36;
            height = 36;
        }
    }

    public void update() {
        velocityY += GRAVITY;
        x += velocityX;
        y += velocityY;

        if (x < 0) {
            x = 0;
            velocityX = -velocityX * BOUNCE;
        } else if (x > GameConfig.WINDOW_WIDTH - width) {
            x = GameConfig.WINDOW_WIDTH - width;
            velocityX = -velocityX * BOUNCE;
        }

        if (y > GameConfig.WINDOW_HEIGHT - height) {
            y = GameConfig.WINDOW_HEIGHT - height;
            velocityY = -velocityY * BOUNCE;
            velocityX *= 0.98;
        }

        if (y < 0) {
            y = 0;
            velocityY = -velocityY * BOUNCE;
        }
    }

    public void render(Graphics2D g2d) {
        if (image != null) {
            g2d.drawImage(image, (int)x, (int)y, null);
        } else {
            g2d.setColor(Color.RED);
            g2d.fillOval((int)x, (int)y, width, height);
        }
    }

    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getVelocityX() { return velocityX; }
    public double getVelocityY() { return velocityY; }
    public String getImagePath() { return imagePath; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setVelocity(double vx, double vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }

    public String toMessage() {
        return id + "," + x + "," + y + "," + velocityX + "," + velocityY + "," + imagePath;
    }

    public static HeadObject fromMessage(String message) {
        String[] parts = message.split(",", 6);
        int id = Integer.parseInt(parts[0]);
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double vx = Double.parseDouble(parts[3]);
        double vy = Double.parseDouble(parts[4]);
        String path = parts[5];
        return new HeadObject(id, x, y, vx, vy, path);
    }
}
