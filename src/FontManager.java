import java.awt.*;
import java.io.InputStream;

public class FontManager {
    private static Font thaiFont;

    static {
        try {
            InputStream is = FontManager.class.getResourceAsStream("/fonts/THSarabunNew.ttf");
            if (is != null) {
                thaiFont = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(thaiFont);
            } else {
                thaiFont = new Font("Tahoma", Font.PLAIN, 16);
            }
        } catch (Exception e) {
            thaiFont = new Font("Tahoma", Font.PLAIN, 16);
        }
    }

    public static Font getThaiFont(int size) {
        return getThaiFont(Font.PLAIN, size);
    }

    public static Font getThaiFont(int style, int size) {
        if (thaiFont != null) {
            return thaiFont.deriveFont(style, size);
        }
        return new Font("Tahoma", style, size);
    }
}
