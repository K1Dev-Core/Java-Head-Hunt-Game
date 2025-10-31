import java.awt.*;

public class Player {
    private String id;
    private int x;
    private int y;
    private Color color;

    public Player(String id) {
        this.id = id;
        this.x = 0;
        this.y = 0;
        this.color = Color.WHITE;
    }

    public Player(String id, Color color) {
        this.id = id;
        this.x = 0;
        this.y = 0;
        this.color = color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Color getColor() {
        return color;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public String toMessage() {
        return id + "," + x + "," + y + "," + color.getRGB();
    }

    public static Player fromMessage(String message) {
        String[] parts = message.split(",");
        Player player = new Player(parts[0]);
        player.x = Integer.parseInt(parts[1]);
        player.y = Integer.parseInt(parts[2]);
        player.color = new Color(Integer.parseInt(parts[3]));
        return player;
    }
}
