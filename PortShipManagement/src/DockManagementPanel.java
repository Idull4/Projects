import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Vector;

public class DockManagementPanel extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField maxSizeField, depthField, portNameField;
    private Vector<Integer> portIDs;

    public DockManagementPanel() {
        setTitle("Dock Management Panel");
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"DockID", "Name", "MaxSize", "Depth", "PortID", "PortName"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel searchPanel = new JPanel();
        maxSizeField = new JTextField(5);
        depthField = new JTextField(5);
        portNameField = new JTextField(10);
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");
        searchPanel.add(new JLabel("Max Size:"));
        searchPanel.add(maxSizeField);
        searchPanel.add(new JLabel("Depth:"));
        searchPanel.add(depthField);
        searchPanel.add(new JLabel("Port Name:"));
        searchPanel.add(portNameField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);
        add(searchPanel, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadData());
        searchBtn.addActionListener(e -> filterData());
        addBtn.addActionListener(e -> openForm(true));
        updateBtn.addActionListener(e -> openForm(false));
        deleteBtn.addActionListener(e -> deleteRecord());

        loadData();

        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        btnPanel.add(backToMenuBtn);
    }

    private void loadData() {
        model.setRowCount(0);
        String sql = "SELECT d.*, p.Name AS PortName FROM dock d JOIN port p ON d.PortID = p.PortID";
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("DockID"),
                        rs.getString("Name"),
                        rs.getDouble("MaxSize"),
                        rs.getDouble("Depth"),
                        rs.getInt("PortID"),
                        rs.getString("PortName")
                });
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void filterData() {
        model.setRowCount(0);
        StringBuilder sql = new StringBuilder("""
            SELECT d.*, p.Name AS PortName
            FROM dock d
            JOIN port p ON d.PortID = p.PortID
            WHERE 1=1
        """);
        List<Object> params = new ArrayList<>();

        if (!maxSizeField.getText().isEmpty()) {
            sql.append(" AND d.MaxSize = ?");
            params.add(Double.valueOf(maxSizeField.getText()));
        }
        if (!depthField.getText().isEmpty()) {
            sql.append(" AND d.Depth = ?");
            params.add(Double.valueOf(depthField.getText()));
        }
        if (!portNameField.getText().isEmpty()) {
            sql.append(" AND p.Name LIKE ?");
            params.add("%" + portNameField.getText() + "%");
        }

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++)
                ps.setObject(i + 1, params.get(i));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("DockID"),
                        rs.getString("Name"),
                        rs.getDouble("MaxSize"),
                        rs.getDouble("Depth"),
                        rs.getInt("PortID"),
                        rs.getString("PortName")
                });
            }
        } catch (SQLException e) {
            showError(e.getMessage());
        }
    }

    private void openForm(boolean isAdd) {
        JDialog form = new JDialog(this, isAdd ? "Add Dock" : "Update Dock", true);
        form.setLayout(new GridLayout(6, 2));
        form.setSize(400, 300);
        form.setLocationRelativeTo(this);

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField maxSize = new JTextField();
        JTextField depth = new JTextField();
        JComboBox<String> portBox = new JComboBox<>();
        portIDs = new Vector<>();

        try (Connection conn = DatabaseConnector.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT PortID, Name FROM port")) {
            while (rs.next()) {
                portIDs.add(rs.getInt("PortID"));
                portBox.addItem(rs.getString("Name"));
            }
        } catch (SQLException e) {
            showError("Error loading ports: " + e.getMessage());
            return;
        }

        form.add(new JLabel("DockID:")); form.add(idField);
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Max Size:")); form.add(maxSize);
        form.add(new JLabel("Depth:")); form.add(depth);
        form.add(new JLabel("Port:")); form.add(portBox);
        JButton save = new JButton("Save");
        form.add(new JLabel()); form.add(save);

        if (!isAdd) {
            int row = table.getSelectedRow();
            if (row == -1) {
                showError("Select a dock to update.");
                return;
            }
            idField.setText(model.getValueAt(row, 0).toString());
            nameField.setText(model.getValueAt(row, 1).toString());
            maxSize.setText(model.getValueAt(row, 2).toString());
            depth.setText(model.getValueAt(row, 3).toString());
            int portId = (int) model.getValueAt(row, 4);
            portBox.setSelectedIndex(portIDs.indexOf(portId));
            idField.setEditable(false);
        }

        save.addActionListener(e -> {
            if (isAdd && idField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "DockID cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "Name cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (maxSize.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "Max Size cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (depth.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "Depth cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try (Connection conn = DatabaseConnector.getConnection()) {
                PreparedStatement ps = isAdd ?
                        conn.prepareStatement("INSERT INTO dock (DockID, Name, MaxSize, Depth, PortID) VALUES (?, ?, ?, ?, ?)") :
                        conn.prepareStatement("UPDATE dock SET Name=?, MaxSize=?, Depth=?, PortID=? WHERE DockID=?");

                if (isAdd) {
                    ps.setInt(1, Integer.parseInt(idField.getText()));
                    ps.setString(2, nameField.getText());
                    ps.setDouble(3, Double.parseDouble(maxSize.getText()));
                    ps.setDouble(4, Double.parseDouble(depth.getText()));
                    ps.setInt(5, portIDs.get(portBox.getSelectedIndex()));
                } else {
                    ps.setString(1, nameField.getText());
                    ps.setDouble(2, Double.parseDouble(maxSize.getText()));
                    ps.setDouble(3, Double.parseDouble(depth.getText()));
                    ps.setInt(4, portIDs.get(portBox.getSelectedIndex()));
                    ps.setInt(5, Integer.parseInt(idField.getText()));
                }

                ps.executeUpdate();
                form.dispose();
                loadData();
            } catch (SQLException ex) {
                showError("Save error: " + ex.getMessage());
            }
        });

        form.setVisible(true);
    }

    private void deleteRecord() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showError("Select a dock to delete.");
            return;
        }
        int dockID = Integer.parseInt(model.getValueAt(row, 0).toString());
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM dock WHERE DockID=?");
            ps.setInt(1, dockID);
            ps.executeUpdate();
            loadData();
        } catch (SQLException e) {
            showError("Delete error: " + e.getMessage());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DockManagementPanel().setVisible(true));
    }
}
