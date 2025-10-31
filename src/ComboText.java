import java.awt.*;

public class ComboText {
    private double x;
    private double y;
    private String text;
    private long startTime;
    private Color color;
    
    public ComboText(double x, double y, int points) {
        this.x = x;
        this.y = y;
        if (points >= 0) {
            this.text = "+" + points;
            this.color = GameConfig.COMBO_TEXT_COLOR;
        } else {
            this.text = String.valueOf(points);
            this.color = Color.RED;
        }
        this.startTime = System.currentTimeMillis();
    }
    
    public ComboText(double x, double y, int points, Color color) {
        this.x = x;
        this.y = y;
        if (points >= 0) {
            this.text = "+" + points;
        } else {
            this.text = String.valueOf(points);
        }
        this.startTime = System.currentTimeMillis();
        this.color = color;
    }
    
    public boolean isFinished() {
        return System.currentTimeMillis() - startTime > GameConfig.COMBO_TEXT_DURATION;
    }
    
    public void render(Graphics2D g2d) {
        if (isFinished()) return;
        
        long elapsed = System.currentTimeMillis() - startTime;
        float progress = elapsed / (float)GameConfig.COMBO_TEXT_DURATION;
        
        float alpha = 1.0f - progress;
        float yOffset = -progress * GameConfig.COMBO_TEXT_Y_OFFSET;
        float scale = 1.0f + (progress * GameConfig.COMBO_TEXT_SCALE);
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        Font font = FontManager.getFont(Font.BOLD, (int)(GameConfig.COMBO_TEXT_FONT_SIZE * scale));
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
