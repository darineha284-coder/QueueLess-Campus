import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class AdminPanel extends JFrame {

    public AdminPanel() {
        setTitle("Admin Dashboard");
        setSize(350, 250);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JButton add = new JButton("Add Food");
        JButton view = new JButton("View Orders");
        JButton update = new JButton("Update Status");

        add.addActionListener(e -> addFood());
        view.addActionListener(e -> viewOrders());
        update.addActionListener(e -> updateStatus());

        setLayout(new GridLayout(3, 1, 8, 8));
        add(add);
        add(view);
        add(update);
        setVisible(true);
    }

    private void addFood() {
        String name = JOptionPane.showInputDialog(this, "Food name:");
        if (name == null || name.trim().isEmpty()) return;

        String input = JOptionPane.showInputDialog(this, "Price:");
        try {
            double price = Double.parseDouble(input);
            if (price <= 0) throw new NumberFormatException();

            try (Connection c = DBConnection.getConnection();
                 PreparedStatement p = c.prepareStatement(
                         "INSERT INTO food_items(name,price,available) VALUES(?,?,TRUE)")) {
                p.setString(1, name.trim());
                p.setDouble(2, price);
                p.executeUpdate();
                JOptionPane.showMessageDialog(this, "Food added");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input or database error");
        }
    }

    private void viewOrders() {
        StringBuilder data = new StringBuilder();
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(
                     "SELECT token_number,student_name,total_amount,status FROM orders")) {
            while (r.next()) {
                data.append("Token: ").append(r.getInt(1))
                        .append(" | ").append(r.getString(2))
                        .append(" | Rs. ").append(r.getDouble(3))
                        .append(" | ").append(r.getString(4)).append("\n");
            }
            JOptionPane.showMessageDialog(this, data.length() == 0 ?
                    "No orders yet" : data.toString());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void updateStatus() {
        try {
            String input = JOptionPane.showInputDialog(this, "Token number:");
            if (input == null) return;
            int token = Integer.parseInt(input);

            String[] options = {"PLACED", "PREPARING", "READY", "COMPLETED"};
            String status = (String) JOptionPane.showInputDialog(
                    this, "Select status:", "Update",
                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            if (status == null) return;

            try (Connection c = DBConnection.getConnection();
                 PreparedStatement p = c.prepareStatement(
                         "UPDATE orders SET status=? WHERE token_number=?")) {
                p.setString(1, status);
                p.setInt(2, token);
                JOptionPane.showMessageDialog(this,
                        p.executeUpdate() > 0 ? "Status updated" : "Token not found");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input or database error");
        }
    }
}