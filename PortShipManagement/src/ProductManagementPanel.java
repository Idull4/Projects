import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ProductManagementPanel extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField categoryFilterField;

    public ProductManagementPanel() {
        setTitle("Product Management Panel");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Product Management Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"ProductID", "Name", "Category", "Description", "HazardCode"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Buton paneli
        JPanel controlPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton refreshBtn = new JButton("Refresh");
        JButton hazardBtn = new JButton("Hazardous Report");
        categoryFilterField = new JTextField(10);
        JButton filterBtn = new JButton("Filter by Category");

        controlPanel.add(addBtn);
        controlPanel.add(updateBtn);
        controlPanel.add(deleteBtn);
        controlPanel.add(hazardBtn);
        controlPanel.add(new JLabel("Category:"));
        controlPanel.add(categoryFilterField);
        controlPanel.add(filterBtn);
        controlPanel.add(refreshBtn);
        add(controlPanel, BorderLayout.SOUTH);

        // Listenerlar
        addBtn.addActionListener(e -> openForm(null));
        updateBtn.addActionListener(e -> updateForm());
        deleteBtn.addActionListener(e -> deleteProduct());
        filterBtn.addActionListener(e -> filterByCategory());
        refreshBtn.addActionListener(e -> loadData());
        hazardBtn.addActionListener(e -> showHazardous());

        loadData();

        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        controlPanel.add(backToMenuBtn);
    }

    private void loadData() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM product");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ProductID"),
                        rs.getString("Name"),
                        rs.getString("Category"),
                        rs.getString("Description"),
                        rs.getString("HazardCode")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load error: " + e.getMessage());
        }
    }

    private void openForm(Integer id) {
        JDialog dialog = new JDialog(this, id == null ? "Add Product" : "Update Product", true);
        dialog.setLayout(new GridLayout(6, 2));
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField categoryField = new JTextField();
        JTextField descField = new JTextField();
        JTextField hazardField = new JTextField();

        if (id != null) {
            int row = table.getSelectedRow();
            idField.setText(model.getValueAt(row, 0).toString());
            idField.setEditable(false);
            nameField.setText(model.getValueAt(row, 1).toString());
            categoryField.setText(model.getValueAt(row, 2).toString());
            descField.setText(model.getValueAt(row, 3).toString());
            hazardField.setText(model.getValueAt(row, 4) != null ? model.getValueAt(row, 4).toString() : "");
        }

        dialog.add(new JLabel("ProductID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Category:"));
        dialog.add(categoryField);
        dialog.add(new JLabel("Description:"));
        dialog.add(descField);
        dialog.add(new JLabel("Hazard Code:"));
        dialog.add(hazardField);

        JButton saveBtn = new JButton("Save");
        dialog.add(new JLabel());
        dialog.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try (Connection conn = DatabaseConnector.getConnection()) {
                if (id == null && idField.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Product ID cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String sql = (id == null) ?
                        "INSERT INTO product (ProductID, Name, Category, Description, HazardCode) VALUES (?, ?, ?, ?, ?)" :
                        "UPDATE product SET Name=?, Category=?, Description=?, HazardCode=? WHERE ProductID=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                if (id == null) {
                    ps.setInt(1, Integer.parseInt(idField.getText()));
                    ps.setString(2, nameField.getText());
                    ps.setString(3, categoryField.getText());
                    ps.setString(4, descField.getText());
                    ps.setString(5, hazardField.getText().isEmpty() ? null : hazardField.getText());
                } else {
                    ps.setString(1, nameField.getText());
                    ps.setString(2, categoryField.getText());
                    ps.setString(3, descField.getText());
                    ps.setString(4, hazardField.getText().isEmpty() ? null : hazardField.getText());
                    ps.setInt(5, id);
                }
                ps.executeUpdate();
                dialog.dispose();
                loadData();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Save error: " + ex.getMessage());
            }
        });


        dialog.setVisible(true);
    }

    private void updateForm() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to update.");
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        openForm(id);
    }

    private void deleteProduct() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a product to delete.");
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM product WHERE ProductID=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete error: " + e.getMessage());
        }
    }

    private void filterByCategory() {
        String category = categoryFilterField.getText().trim();
        model.setRowCount(0);

        if (category.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a category to filter.", "Input Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM product WHERE Category = ?")) {
            ps.setString(1, category);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ProductID"),
                        rs.getString("Name"),
                        rs.getString("Category"),
                        rs.getString("Description"),
                        rs.getString("HazardCode")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Filter error: " + e.getMessage());
        }
    }


    private void showHazardous() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM product WHERE HazardCode IS NOT NULL");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("ProductID"),
                        rs.getString("Name"),
                        rs.getString("Category"),
                        rs.getString("Description"),
                        rs.getString("HazardCode")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Hazard query error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductManagementPanel().setVisible(true));
    }
}

