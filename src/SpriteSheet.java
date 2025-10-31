import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class SpriteSheet {
    private BufferedImage spriteSheet;
    private BufferedImage[] frames;
    private int frameWidth;
    private int frameHeight;
    private int columns;
    private int rows;
    private int totalFrames;
    
    public SpriteSheet(String imagePath, int frameWidth, int frameHeight, int columns, int rows) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.columns = columns;
        this.rows = rows;
        this.totalFrames = columns * rows;
        
        loadSpriteSheet(imagePath);
        extractFrames();
    }
    
    private void loadSpriteSheet(String imagePath) {
        try {
            spriteSheet = ImageIO.read(new File(imagePath));
        } catch (Exception e) {
            System.err.println("Cannot load sprite sheet: " + imagePath);
            e.printStackTrace();
        }
    }
    
    private void extractFrames() {
        frames = new BufferedImage[totalFrames];
        
        if (spriteSheet == null) {
            return;
        }
        
        int index = 0;
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                int x = col * frameWidth;
                int y = row * frameHeight;
                
                frames[index] = spriteSheet.getSubimage(x, y, frameWidth, frameHeight);
                index++;
            }
        }
    }
    
    public BufferedImage getFrame(int frameIndex) {
        if (frames == null || frameIndex < 0 || frameIndex >= totalFrames) {
            return null;
        }
        return frames[frameIndex];
    }
    
    public int getTotalFrames() {
        return totalFrames;
    }
    
    public int getFrameWidth() {
        return frameWidth;
    }
    
    public int getFrameHeight() {
        return frameHeight;
    }
}

