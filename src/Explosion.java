import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class Explosion {
    private double x;
    private double y;
    private long startTime;
    private static final long DURATION = 500;
    private BufferedImage explosionImage;
    
    public Explosion(double x, double y) {
        this.x = x;
        this.y = y;
        this.startTime = System.currentTimeMillis();
        loadExplosionImage();
    }
    
    private void loadExplosionImage() {
        try {
            explosionImage = ImageIO.read(new File("res/bomb.gif"));
        } catch (Exception e) {
            System.err.println("Cannot load explosion image");
        }
    }
    
    public boolean isFinished() {
        return System.currentTimeMillis() - startTime > DURATION;
    }
    
    public void render(Graphics2D g2d) {
        if (explosionImage != null && !isFinished()) {
            long elapsed = System.currentTimeMillis() - startTime;
            float alpha = 1.0f - (elapsed / (float)DURATION);
            
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
            int size = 64;
            g2d.drawImage(explosionImage, (int)(x - size/2), (int)(y - size/2), size, size, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        }
    }
}
