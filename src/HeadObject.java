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
    private int width;
    private int height;
    private static BufferedImage[][] animationFrames;
    private int animationIndex;
    private int currentFrame = 0;
    long lastFrameTime;
    private boolean isSkull;
    private long spawnTime;

    public HeadObject(int id, double x, double y, double velocityX, double velocityY, String imagePath) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.imagePath = imagePath;
        this.lastFrameTime = System.currentTimeMillis();
        this.spawnTime = System.currentTimeMillis();
        this.currentFrame = 0;

        try {
            this.animationIndex = Integer.parseInt(imagePath);
            if (this.animationIndex < 0 || this.animationIndex >= GameConfig.HEAD_ANIMATIONS.length) {
                this.animationIndex = 0;
            }
        } catch (NumberFormatException e) {
            this.animationIndex = 0;
        }

        this.isSkull = (this.animationIndex == GameConfig.SKULL_INDEX);
        loadImage();
    }

    static {
        try {
            System.out.println("🎬 กำลังโหลดเฟรมอนิเมชั่น...");
            animationFrames = new BufferedImage[GameConfig.HEAD_ANIMATIONS.length][];
            for (int i = 0; i < GameConfig.HEAD_ANIMATIONS.length; i++) {
                GameConfig.AnimationConfig config = GameConfig.HEAD_ANIMATIONS[i];
                animationFrames[i] = new BufferedImage[config.frameCount];

                int loadedFrames = 0;
                for (int frame = 0; frame < config.frameCount; frame++) {
                    String framePath = config.folder + (frame + 1) + ".png";
                    try {
                        File imageFile = PathResolver.getFile(framePath);
                        if (imageFile.exists()) {
                            animationFrames[i][frame] = ImageIO.read(imageFile);
                            loadedFrames++;
                        } else {
                            System.err.println("❌ ไม่พบไฟล์: " + imageFile.getAbsolutePath());
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Error โหลด " + framePath + ": " + e.getMessage());
                        e.printStackTrace();
                    }
                }
                System.out.println(
                        "✅ โหลด " + config.folder + " สำเร็จ: " + loadedFrames + "/" + config.frameCount + " เฟรม");
            }
            System.out.println("🎬 โหลดเฟรมอนิเมชั่นเสร็จสิ้น!");
        } catch (Exception e) {
            System.err.println("❌ Error ใน static block: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadImage() {
        if (animationFrames != null && animationIndex >= 0 && animationIndex < animationFrames.length 
            && animationFrames[animationIndex] != null && animationFrames[animationIndex].length > 0 
            && animationFrames[animationIndex][0] != null) {
            BufferedImage firstFrame = animationFrames[animationIndex][0];
            width = firstFrame.getWidth() * GameConfig.HEAD_IMAGE_SCALE;
            height = firstFrame.getHeight() * GameConfig.HEAD_IMAGE_SCALE;
        } else {
            width = GameConfig.HEAD_DEFAULT_SIZE;
            height = GameConfig.HEAD_DEFAULT_SIZE;
        }
    }

    public void update() {
        x += velocityX;
        y += velocityY;

        if (x < 0) {
            x = 0;
            velocityX = -velocityX;
        } else if (x > GameConfig.WINDOW_WIDTH - width) {
            x = GameConfig.WINDOW_WIDTH - width;
            velocityX = -velocityX;
        }

        if (y > GameConfig.WINDOW_HEIGHT - height) {
            y = GameConfig.WINDOW_HEIGHT - height;
            velocityY = -velocityY;
        }

        if (y < 0) {
            y = 0;
            velocityY = -velocityY;
        }
    }

    public void render(Graphics2D g2d) {
        updateAnimation();

        if (animationFrames != null && animationIndex >= 0 && animationIndex < animationFrames.length
                && animationFrames[animationIndex] != null && animationFrames[animationIndex].length > 0) {

            if (currentFrame >= animationFrames[animationIndex].length) {
                currentFrame = 0;
            }

            BufferedImage frame = animationFrames[animationIndex][currentFrame];
            if (frame != null) {
                g2d.drawImage(frame, (int) x, (int) y, width, height, null);
            } else {
                g2d.setColor(Color.ORANGE);
                g2d.fillOval((int) x, (int) y, width, height);
            }
        } else {
            g2d.setColor(Color.RED);
            g2d.fillOval((int) x, (int) y, width, height);
        }
    }

    private void updateAnimation() {
        if (animationFrames == null || animationIndex < 0 || animationIndex >= animationFrames.length 
            || animationFrames[animationIndex] == null || animationFrames[animationIndex].length == 0) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        
        if (lastFrameTime == 0) {
            lastFrameTime = currentTime;
        }
        
        long elapsed = currentTime - lastFrameTime;
        
        if (elapsed >= GameConfig.HEAD_ANIMATION_SPEED) {
            currentFrame = (currentFrame + 1) % animationFrames[animationIndex].length;
            lastFrameTime = currentTime;
        }
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void setVelocity(double vx, double vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }
    
    /**
     * อัพเดทจาก sync message โดยไม่ทำลาย animation state
     */
    public void updateFromSync(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.velocityX = vx;
        this.velocityY = vy;
        // ไม่ทำอะไรกับ lastFrameTime และ currentFrame เพื่อให้อนิเมชั่นเล่นต่อได้
    }

    public boolean isSkull() {
        return isSkull;
    }

    public boolean isSkullExpired() {
        if (!isSkull)
            return false;
        return (System.currentTimeMillis() - spawnTime) > GameConfig.SKULL_LIFETIME;
    }

    public String toMessage() {
        return id + "," + x + "," + y + "," + velocityX + "," + velocityY + "," + imagePath + ","
                + (isSkull ? "1" : "0");
    }

    public static HeadObject fromMessage(String message) {
        String[] parts = message.split(",", 7);
        int id = Integer.parseInt(parts[0]);
        double x = Double.parseDouble(parts[1]);
        double y = Double.parseDouble(parts[2]);
        double vx = Double.parseDouble(parts[3]);
        double vy = Double.parseDouble(parts[4]);
        String path = parts[5];
        HeadObject head = new HeadObject(id, x, y, vx, vy, path);

        if (parts.length > 6) {
            head.isSkull = parts[6].equals("1");
        }

        return head;
    }
}
