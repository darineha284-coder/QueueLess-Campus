import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class Main extends JFrame {

    public Main() {
        setTitle("QueueLess Campus");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("QueueLess Campus", SwingConstants.CENTER);
        JLabel tagline = new JLabel("Skip the queue. Enjoy your break.",
                SwingConstants.CENTER);

        JButton student = new JButton("Student");
        JButton admin = new JButton("Admin Login");
        JButton test = new JButton("Test Database");

        student.addActionListener(e -> new StudentPanel());

        admin.addActionListener(e -> {
            JPasswordField field = new JPasswordField();
            int result = JOptionPane.showConfirmDialog(
                    this, field, "Admin Password", JOptionPane.OK_CANCEL_OPTION);

            if (result == JOptionPane.OK_OPTION &&
                    "admin123".equals(new String(field.getPassword()))) {
                new AdminPanel();
            } else if (result == JOptionPane.OK_OPTION) {
                JOptionPane.showMessageDialog(this, "Incorrect password");
            }
        });

        test.addActionListener(e -> {
            try (Connection c = DBConnection.getConnection()) {
                JOptionPane.showMessageDialog(this, "Database Connected!");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage());
            }
        });

        setLayout(new GridLayout(5, 1, 8, 8));
        add(title);
        add(tagline);
        add(student);
        add(admin);
        add(test);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}