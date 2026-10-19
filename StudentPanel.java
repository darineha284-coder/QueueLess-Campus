import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.*;

public class StudentPanel extends JFrame {
    private Map<Integer, Integer> cart = new LinkedHashMap<>();
    private DefaultListModel<String> menu = new DefaultListModel<>();
    private JList<String> list = new JList<>(menu);
    private JLabel totalLabel = new JLabel("Total: Rs. 0");

    public StudentPanel() {
        setTitle("Student Menu");
        setSize(450, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JButton add = new JButton("Add to Cart");
        JButton order = new JButton("Place Order");

        add.addActionListener(e -> addItem());
        order.addActionListener(e -> placeOrder());

        JPanel buttons = new JPanel();
        buttons.add(add);
        buttons.add(order);

        add(new JScrollPane(list), BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);
        add(totalLabel, BorderLayout.NORTH);

        loadMenu();
        setVisible(true);
    }

    private void loadMenu() {
        try (Connection c = DBConnection.getConnection();
             Statement s = c.createStatement();
             ResultSet r = s.executeQuery(
                     "SELECT id,name,price FROM food_items WHERE available=TRUE")) {
            while (r.next()) {
                menu.addElement(r.getInt("id") + " | " +
                        r.getString("name") + " | Rs. " + r.getDouble("price"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void addItem() {
        int i = list.getSelectedIndex();
        if (i < 0) return;

        int id = Integer.parseInt(menu.get(i).split("\\|")[0].trim());
        cart.put(id, cart.getOrDefault(id, 0) + 1);

        double total = 0;
        try (Connection c = DBConnection.getConnection();
             PreparedStatement p = c.prepareStatement(
                     "SELECT price FROM food_items WHERE id=?")) {
            for (int food : cart.keySet()) {
                p.setInt(1, food);
                try (ResultSet r = p.executeQuery()) {
                    if (r.next()) total += r.getDouble(1) * cart.get(food);
                }
            }
            totalLabel.setText(String.format("Total: Rs. %.2f", total));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty");
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Student name:");
        if (name == null || name.trim().isEmpty()) return;

        String pickup = JOptionPane.showInputDialog(this, "Pickup time:");
        if (pickup == null || pickup.trim().isEmpty()) return;

        try (Connection c = DBConnection.getConnection()) {
            c.setAutoCommit(false);
            double total = 0;

            for (int id : cart.keySet()) {
                try (PreparedStatement p = c.prepareStatement(
                        "SELECT price FROM food_items WHERE id=? AND available=TRUE")) {
                    p.setInt(1, id);
                    try (ResultSet r = p.executeQuery()) {
                        if (!r.next()) throw new SQLException("Food unavailable");
                        total += r.getDouble(1) * cart.get(id);
                    }
                }
            }

            int token;
            try (Statement s = c.createStatement();
                 ResultSet r = s.executeQuery(
                         "SELECT COALESCE(MAX(token_number),0)+1 FROM orders")) {
                r.next();
                token = r.getInt(1);
            }

            int orderId;
            try (PreparedStatement p = c.prepareStatement(
                    "INSERT INTO orders(token_number,student_name,pickup_time,total_amount,status) " +
                            "VALUES(?,?,?,?, 'PLACED')", Statement.RETURN_GENERATED_KEYS)) {
                p.setInt(1, token);
                p.setString(2, name.trim());
                p.setString(3, pickup.trim());
                p.setDouble(4, total);
                p.executeUpdate();
                try (ResultSet r = p.getGeneratedKeys()) {
                    r.next();
                    orderId = r.getInt(1);
                }
            }

            String sql = "INSERT INTO order_items(order_id,food_id,food_name,price,quantity) " +
                    "SELECT ?,id,name,price,? FROM food_items WHERE id=?";

            try (PreparedStatement p = c.prepareStatement(sql)) {
                for (int id : cart.keySet()) {
                    p.setInt(1, orderId);
                    p.setInt(2, cart.get(id));
                    p.setInt(3, id);
                    p.executeUpdate();
                }
            }

            c.commit();
            JOptionPane.showMessageDialog(this, "Order placed! Token: " + token);
            cart.clear();
            totalLabel.setText("Total: Rs. 0");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }
}