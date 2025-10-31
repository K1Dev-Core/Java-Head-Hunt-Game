import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class FontManager {
    private static Font arabicaFont;

    static {
        try {
            File fontFile = new File("res/Arabica.ttf");
            if (fontFile.exists()) {
                InputStream is = new FileInputStream(fontFile);
                arabicaFont = Font.createFont(Font.TRUETYPE_FONT, is);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(arabicaFont);
                is.close();
            } else {
                arabicaFont = new Font("Arial", Font.PLAIN, 16);
            }
        } catch (Exception e) {
            arabicaFont = new Font("Arial", Font.PLAIN, 16);
        }
    }

    public static Font getFont(int size) {
        return getFont(Font.PLAIN, size);
    }

    public static Font getFont(int style, int size) {
        if (arabicaFont != null) {
            return arabicaFont.deriveFont(style, (float)size);
        }
        return new Font("Arial", style, size);
    }
    
    public static Font getThaiFont(int size) {
        return getFont(Font.PLAIN, size);
    }

    public static Font getThaiFont(int style, int size) {
        return getFont(style, size);
    }
}
