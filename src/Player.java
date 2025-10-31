import java.awt.*;

public class Player {
    private String id;
    private int x;
    private int y;
    private Color color;
    private int score;

    public Player(String id) {
        this.id = id;
        this.x = 0;
        this.y = 0;
        this.color = Color.WHITE;
        this.score = 0;
    }

    public Player(String id, Color color) {
        this.id = id;
        this.x = 0;
        this.y = 0;
        this.color = color;
        this.score = 0;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addScore(int points) {
        this.score += points;
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
        return id + "," + x + "," + y + "," + color.getRGB() + "," + score;
    }

    public static Player fromMessage(String message) {
        String[] parts = message.split(",");
        Player player = new Player(parts[0]);
        player.x = Integer.parseInt(parts[1]);
        player.y = Integer.parseInt(parts[2]);
        player.color = new Color(Integer.parseInt(parts[3]));
        if (parts.length > 4) {
            player.score = Integer.parseInt(parts[4]);
        }
        return player;
    }
}
