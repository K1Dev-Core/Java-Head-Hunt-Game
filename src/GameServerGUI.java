import javax.swing.*;
import java.awt.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class GameServerGUI extends JFrame {
    private JTextArea logArea;
    private JLabel statusLabel;
    private JLabel ipLabel;
    private JButton startButton;
    private JButton stopButton;
    private GameServer gameServer;
    private Thread serverThread;
    private boolean serverRunning = false;

    public GameServerGUI() {
        setTitle("Monster Pop Arena - Server");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                if (serverRunning) {
                    addLog("========================================");
                    addLog("กำลังหยุดเซิร์ฟเวอร์...");
                    if (serverThread != null) {
                        serverThread.interrupt();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {
                    }
                }
                System.exit(0);
            }
        });

        initComponents();

        setVisible(true);
        
        startServer();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(240, 240, 240));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

     
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setBackground(new Color(52, 152, 219));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("MONSTER POP ARENA SERVER", SwingConstants.CENTER);
        titleLabel.setFont(FontManager.getThaiFont(Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.NORTH);

      
        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        ipPanel.setBackground(new Color(52, 152, 219));

        ipLabel = new JLabel("Server IP: " + getServerIP());
        ipLabel.setFont(FontManager.getThaiFont(Font.BOLD, 30));
        ipLabel.setForeground(Color.WHITE);
        ipPanel.add(ipLabel);


        headerPanel.add(ipPanel, BorderLayout.CENTER);

      

        mainPanel.add(headerPanel, BorderLayout.NORTH);


        JPanel logPanel = new JPanel(new BorderLayout(5, 5));
        logPanel.setBackground(new Color(240, 240, 240));

        JLabel logTitleLabel = new JLabel("Server Logs:");
        logTitleLabel.setFont(FontManager.getThaiFont(Font.BOLD, 18));
        logTitleLabel.setForeground(new Color(44, 62, 80));
        logPanel.add(logTitleLabel, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(FontManager.getThaiFont(Font.PLAIN, 14));
        logArea.setBackground(new Color(44, 62, 80));
        logArea.setForeground(new Color(236, 240, 241));
        logArea.setMargin(new Insets(10, 10, 10, 10));
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        logPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(logPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(new Color(240, 240, 240));

        startButton = new JButton("เริ่มเซิร์ฟเวอร์");
        startButton.setFont(FontManager.getThaiFont(Font.BOLD, 20));
        startButton.setPreferredSize(new Dimension(250, 50));
        startButton.setBackground(new Color(46, 204, 113));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(false);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> startServer());

        stopButton = new JButton("หยุดเซิร์ฟเวอร์");
        stopButton.setFont(FontManager.getThaiFont(Font.BOLD, 20));
        stopButton.setPreferredSize(new Dimension(250, 50));
        stopButton.setBackground(new Color(231, 76, 60));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFocusPainted(false);
        stopButton.setBorderPainted(false);
        stopButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        stopButton.setEnabled(false);
        stopButton.addActionListener(e -> stopServer());

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        addLog("GUI พร้อมใช้งาน");
    }

    private String getServerIP() {
        try {
            InetAddress localhost = InetAddress.getLocalHost();
            String localIP = localhost.getHostAddress();
            
           
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                if (iface.isLoopback() || !iface.isUp()) continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (addr instanceof Inet4Address) {
                        return addr.getHostAddress();
                    }
                }
            }
            
            return localIP;
        } catch (Exception e) {
            return "Unknown";
        }
    }

    private void startServer() {
        if (!serverRunning) {
            serverRunning = true;
            if (startButton != null) {
                startButton.setEnabled(false);
            }
            if (stopButton != null) {
                stopButton.setEnabled(true);
            }
            if (statusLabel != null) {
                statusLabel.setText("สถานะ: กำลังรัน");
                statusLabel.setForeground(new Color(46, 204, 113));
            }
            
            addLog("========================================");
            addLog("เริ่มเซิร์ฟเวอร์...");
            addLog("Port: " + GameConfig.SERVER_PORT);
            addLog("IP Address: " + getServerIP());
            addLog("========================================");

            gameServer = new GameServer();
            gameServer.setLogCallback(this::addLog);
            
            serverThread = new Thread(() -> {
                gameServer.start();
            });
            serverThread.start();
        }
    }

    private void stopServer() {
        if (serverRunning) {
            serverRunning = false;
            if (startButton != null) {
                startButton.setEnabled(true);
            }
            if (stopButton != null) {
                stopButton.setEnabled(false);
            }
            if (statusLabel != null) {
                statusLabel.setText("สถานะ: ปิด");
                statusLabel.setForeground(new Color(231, 76, 60));
            }
            
            addLog("========================================");
            addLog("หยุดเซิร์ฟเวอร์");
            addLog("========================================");
            
            if (serverThread != null) {
                serverThread.interrupt();
            }
            
            System.exit(0);
        }
    }

    public void addLog(String message) {
        SwingUtilities.invokeLater(() -> {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(GameServerGUI::new);
        
    }
}

