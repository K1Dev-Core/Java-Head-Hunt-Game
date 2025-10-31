import javax.swing.*;
import java.io.*;
import java.net.*;

public class GameClient extends JFrame {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private GamePanel gamePanel;
    private LobbyScreen lobbyScreen;
    private String myPlayerId;
    private java.awt.Color myColor;
    private boolean gameStarted = false;
    private java.util.List<String> pendingPlayers = new java.util.ArrayList<>();
    private java.util.Map<String, Player> allPlayers = new java.util.concurrent.ConcurrentHashMap<>();

    public GameClient() {
        connectToServer();
    }

    private void connectToServer() {
        String serverIP = GameConfig.SERVER_IP;
        
        while (true) {
            try {
                socket = new Socket(serverIP, GameConfig.SERVER_PORT);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                new Thread(this::receiveMessages).start();
                break;

            } catch (IOException e) {
                JTextField textField = new JTextField(serverIP);
                textField.setFont(FontManager.getThaiFont(16));
                
                JLabel label = new JLabel("ไม่สามารถเชื่อมต่อ Server ได้\nกรุณาใส่ IP Address:");
                label.setFont(FontManager.getThaiFont(14));
                
                JPanel panel = new JPanel(new java.awt.BorderLayout(5, 5));
                panel.add(label, java.awt.BorderLayout.NORTH);
                panel.add(textField, java.awt.BorderLayout.CENTER);
                
                int result = JOptionPane.showConfirmDialog(
                    null,
                    panel,
                    "Connect to Server",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                String input = (result == JOptionPane.OK_OPTION) ? textField.getText() : null;
                
                if (input == null || input.trim().isEmpty()) {
                    int option = JOptionPane.showConfirmDialog(
                        null,
                        "You sure want to exit?",
                        "Sure ?",
                        JOptionPane.YES_NO_OPTION
                    );
                    
                    if (option == JOptionPane.YES_OPTION) {
                        System.exit(0);
                    }
                } else {
                    serverIP = input.trim();
                }
            }
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
            myPlayerId = parts[0];
            myColor = new java.awt.Color(Integer.parseInt(parts[1]));
            
            SwingUtilities.invokeLater(() -> {
                lobbyScreen = new LobbyScreen(myPlayerId);
                
                lobbyScreen.setOnExitCallback(() -> {
                    exitLobby();
                });
                
                synchronized(pendingPlayers) {
                    for (String playerId : pendingPlayers) {
                        lobbyScreen.addPlayer(playerId);
                    }
                    pendingPlayers.clear();
                }
            });

        } else if (message.startsWith("NEW:")) {
            Player player = Player.fromMessage(message.substring(4));
            allPlayers.put(player.getId(), player);
            
            if (!gameStarted) {
                if (lobbyScreen != null) {
                    lobbyScreen.addPlayer(player.getId());
                } else {
                    synchronized(pendingPlayers) {
                        if (!pendingPlayers.contains(player.getId())) {
                            pendingPlayers.add(player.getId());
                        }
                    }
                }
            }
            if (gamePanel != null) {
                gamePanel.addPlayer(player);
            }

        } else if (message.startsWith("PLAYERCOUNT:")) {
            
        } else if (message.startsWith("GAMESTARTING")) {
            if (!gameStarted) {
                SwingUtilities.invokeLater(() -> {
                    if (lobbyScreen != null) {
                        lobbyScreen.setGameStarting(true);
                    }
                });
                startGame();
            }

        } else if (message.startsWith("GAMEINPROGRESS")) {
            SwingUtilities.invokeLater(() -> {
                if (lobbyScreen != null) {
                    lobbyScreen.setGameInProgress(true);
                }
            });

        } else if (message.startsWith("UPDATE:")) {
            Player player = Player.fromMessage(message.substring(7));
            allPlayers.put(player.getId(), player);
            if (gamePanel != null) {
                gamePanel.updatePlayer(player);
            }

        } else if (message.startsWith("REMOVE:")) {
            String playerId = message.substring(7);
            allPlayers.remove(playerId);
            if (!gameStarted && lobbyScreen != null) {
                lobbyScreen.removePlayer(playerId);
            } else if (gamePanel != null) {
                gamePanel.removePlayer(playerId);
            }

        } else if (message.startsWith("NEWHEAD:")) {
            HeadObject head = HeadObject.fromMessage(message.substring(8));
            if (gamePanel != null) {
                gamePanel.addHead(head);
            }

        } else if (message.startsWith("HEADSYNC:")) {
            String data = message.substring(9);
            if (!data.isEmpty() && gamePanel != null) {
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
            Player p = allPlayers.get(playerId);
            if (p != null) {
                p.setScore(score);
            }
            if (gamePanel != null) {
                gamePanel.updatePlayerScore(playerId, score);
            }

        } else if (message.startsWith("TIME:")) {
            long remaining = Long.parseLong(message.substring(5));
            if (gamePanel != null) {
                gamePanel.updateGameTime(remaining);
            }

        } else if (message.startsWith("GAMEOVER")) {
            if (gamePanel != null) {
                gamePanel.endGame();
            }

        } else if (message.startsWith("NEWGAME")) {
            if (gamePanel != null) {
                gamePanel.resetGame();
            }
        }
    }

    private void startGame() {
        if (gameStarted) return;
        gameStarted = true;

        new Thread(() -> {
            try {
                Thread.sleep(GameConfig.GAME_START_DELAY);
            } catch (InterruptedException e) {
            }

            SwingUtilities.invokeLater(() -> {
                if (lobbyScreen != null) {
                    lobbyScreen.dispose();
                }

                setTitle("Monster Pop Arena");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setUndecorated(true);

                gamePanel = new GamePanel(this);
                gamePanel.setMyPlayerId(myPlayerId, myColor);
                
                for (Player p : allPlayers.values()) {
                    gamePanel.addPlayer(p);
                }
                
                add(gamePanel);

                pack();
                setLocationRelativeTo(null);
                setVisible(true);
            });
        }).start();
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

    private void exitLobby() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
        }
    }
    
    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
        }
    }


}
