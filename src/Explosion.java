import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class Explosion {
    private double x;
    private double y;
    private long startTime;
    private BufferedImage[] explosionFrames;
    
    public Explosion(double x, double y) {
        this.x = x;
        this.y = y;
        this.startTime = System.currentTimeMillis();
        loadExplosionFrames();
    }
    
    private void loadExplosionFrames() {
        explosionFrames = new BufferedImage[GameConfig.EXPLOSION_FRAMES.length];
        try {
            for (int i = 0; i < GameConfig.EXPLOSION_FRAMES.length; i++) {
                explosionFrames[i] = ImageIO.read(new File(GameConfig.EXPLOSION_FRAMES[i]));
            }
        } catch (Exception e) {
            System.err.println("Cannot load explosion frames: " + e.getMessage());
        }
    }
    
    public boolean isFinished() {
        return System.currentTimeMillis() - startTime > GameConfig.EXPLOSION_DURATION;
    }
    
    public void render(Graphics2D g2d) {
        if (explosionFrames != null && !isFinished()) {
            long elapsed = System.currentTimeMillis() - startTime;
            int frameIndex = (int)(elapsed / GameConfig.EXPLOSION_FRAME_DURATION) % explosionFrames.length;
            
            if (frameIndex < explosionFrames.length && explosionFrames[frameIndex] != null) {
                float alpha = 1.0f - (elapsed / (float)GameConfig.EXPLOSION_DURATION);
                
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                g2d.drawImage(explosionFrames[frameIndex], 
                    (int)(x - GameConfig.EXPLOSION_SIZE/2), 
                    (int)(y - GameConfig.EXPLOSION_SIZE/2), 
                    GameConfig.EXPLOSION_SIZE, GameConfig.EXPLOSION_SIZE, null);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
        }
    }
}
