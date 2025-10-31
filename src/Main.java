import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        UIManager.put("OptionPane.messageFont", FontManager.getFont(14));
        UIManager.put("OptionPane.buttonFont", FontManager.getFont(14));
        
        String[] options = { "Server", "Client" };
        int choice = JOptionPane.showOptionDialog(
                null,
                "เลือกโหมด",
                "Head Hunt Game",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if (choice == 0) {
            new Thread(() -> GameServer.main(args)).start();
            JOptionPane.showMessageDialog(null, "Server เริ่มทำงานแล้ว!");
        } else if (choice == 1) {
            SwingUtilities.invokeLater(GameClient::new);
        } else {
            System.exit(0);
        }
    }
}
