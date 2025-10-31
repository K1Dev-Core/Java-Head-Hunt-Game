import java.awt.*;

public class ComboText {
    private double x;
    private double y;
    private String text;
    private long startTime;
    private static final long DURATION = 1000;
    private Color color;
    
    public ComboText(double x, double y, int points) {
        this.x = x;
        this.y = y;
        this.text = "+" + points;
        this.startTime = System.currentTimeMillis();
        this.color = new Color(255, 215, 0);
    }
    
    public boolean isFinished() {
        return System.currentTimeMillis() - startTime > DURATION;
    }
    
    public void render(Graphics2D g2d) {
        if (isFinished()) return;
        
        long elapsed = System.currentTimeMillis() - startTime;
        float progress = elapsed / (float)DURATION;
        
        float alpha = 1.0f - progress;
        float yOffset = -progress * 50;
        float scale = 1.0f + (progress * 0.5f);
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        Font font = FontManager.getFont(Font.BOLD, (int)(32 * scale));
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        
        g2d.setColor(Color.BLACK);
        g2d.drawString(text, (int)(x - textWidth/2 + 2), (int)(y + yOffset + 2));
        
        g2d.setColor(color);
        g2d.drawString(text, (int)(x - textWidth/2), (int)(y + yOffset));
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
    }
}
