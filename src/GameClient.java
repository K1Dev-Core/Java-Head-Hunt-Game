import javax.swing.*;
import java.io.*;
import java.net.*;

public class GameClient extends JFrame {
    private static final String SERVER_HOST = "89.38.101.103";
    private static final int SERVER_PORT = 8888;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GamePanel gamePanel;

    public GameClient() {
        setTitle("Head Hunt Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        gamePanel = new GamePanel(this);
        add(gamePanel);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        connectToServer();
    }

    private void connectToServer() {
        try {
            socket = new Socket(SERVER_HOST, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            new Thread(this::receiveMessages).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,
                    "Cannot connect to server. Make sure server is running.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void receiveMessages() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            System.err.println("Connection lost");
        }
    }

    private void processMessage(String message) {
        if (message.startsWith("ID:")) {
            String[] parts = message.substring(3).split(",");
            String myId = parts[0];
            java.awt.Color myColor = new java.awt.Color(Integer.parseInt(parts[1]));
            gamePanel.setMyPlayerId(myId, myColor);

        } else if (message.startsWith("NEW:")) {
            Player player = Player.fromMessage(message.substring(4));
            gamePanel.addPlayer(player);

        } else if (message.startsWith("UPDATE:")) {
            Player player = Player.fromMessage(message.substring(7));
            gamePanel.updatePlayer(player);

        } else if (message.startsWith("REMOVE:")) {
            String playerId = message.substring(7);
            gamePanel.removePlayer(playerId);

        } else if (message.startsWith("NEWHEAD:")) {
            HeadObject head = HeadObject.fromMessage(message.substring(8));
            gamePanel.addHead(head);

        } else if (message.startsWith("HEADSYNC:")) {
            String data = message.substring(9);
            if (!data.isEmpty()) {
                String[] headMessages = data.split("\\|");
                for (String headMsg : headMessages) {
                    HeadObject head = HeadObject.fromMessage(headMsg);
                    gamePanel.updateHead(head);
                }
            }

        } else if (message.startsWith("SCORE:")) {
            String[] parts = message.substring(6).split(",");
            String playerId = parts[0];
            int score = Integer.parseInt(parts[1]);
            gamePanel.updatePlayerScore(playerId, score);

        } else if (message.startsWith("TIME:")) {
            long remaining = Long.parseLong(message.substring(5));
            gamePanel.updateGameTime(remaining);

        } else if (message.startsWith("GAMEOVER")) {
            gamePanel.endGame();
        }
    }

    public void sendPosition(int x, int y) {
        if (out != null) {
            out.println("POS:" + x + "," + y);
        }
    }

    public void sendHeadHit(int headId) {
        if (out != null) {
            out.println("HIT:" + headId);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameClient::new);
    }
}
