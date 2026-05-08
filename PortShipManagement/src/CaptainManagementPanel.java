import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class CaptainManagementPanel extends JFrame {
    private JTable captainTable;
    private DefaultTableModel tableModel;
    private Vector<Integer> shipIDs;
    private JComboBox<String> shipComboBox;

    public CaptainManagementPanel() {
        setTitle("Captain Management Panel");
        setSize(1150, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Captain Management Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{
                "CaptainID", "Name", "LicenseNumber", "Nationality", "HireDate", "ExperienceYears", "AssignedShip"
        }, 0);
        captainTable = new JTable(tableModel);
        add(new JScrollPane(captainTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Captain");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton assignBtn = new JButton("Assign to Ship");
        JButton historyBtn = new JButton("View Assignment History");
        JButton refreshBtn = new JButton("Refresh");
        JButton filterByShipBtn = new JButton("Filter by Assigned Ship");

        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn);
        btnPanel.add(assignBtn); btnPanel.add(historyBtn);
        btnPanel.add(filterByShipBtn); btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadCaptains());
        addBtn.addActionListener(e -> openCaptainForm(null));
        updateBtn.addActionListener(e -> {
            int row = captainTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a captain to update.");
            } else {
                openCaptainForm(row);
            }
        });
        deleteBtn.addActionListener(e -> deleteCaptain());
        assignBtn.addActionListener(e -> assignCaptainToShip());
        historyBtn.addActionListener(e -> showAssignmentHistory());
        filterByShipBtn.addActionListener(e -> filterCaptainsByAssignedShip());

        loadCaptains();

        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        btnPanel.add(backToMenuBtn);
    }

    private void loadCaptains() {
        tableModel.setRowCount(0);
        String sql = """
            SELECT c.*, 
                   s.Name AS AssignedShip
            FROM captain c
            LEFT JOIN (
                SELECT CaptainID, ShipID
                FROM captain_ship_history h1
                WHERE AssignDate = (
                    SELECT MAX(AssignDate)
                    FROM captain_ship_history h2
                    WHERE h2.CaptainID = h1.CaptainID
                )
            ) latest ON c.CaptainID = latest.CaptainID
            LEFT JOIN ship s ON latest.ShipID = s.ShipID
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("CaptainID"),
                        rs.getString("Name"),
                        rs.getString("LicenseNumber"),
                        rs.getString("Nationality"),
                        rs.getDate("HireDate"),
                        rs.getInt("ExperienceYears"),
                        rs.getString("AssignedShip") != null ? rs.getString("AssignedShip") : "-"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading captains: " + e.getMessage());
        }
    }

    private void filterCaptainsByAssignedShip() {
        JComboBox<String> shipBox = new JComboBox<>();
        Vector<Integer> shipIDs = new Vector<>();

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ShipID, Name FROM ship")) {
            while (rs.next()) {
                shipIDs.add(rs.getInt("ShipID"));
                shipBox.addItem(rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load ships: " + e.getMessage());
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, shipBox, "Select a Ship", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        int selectedShipID = shipIDs.get(shipBox.getSelectedIndex());
        tableModel.setRowCount(0);

        String sql = """
            SELECT c.*, s.Name AS AssignedShip
            FROM captain c
            JOIN (
                SELECT CaptainID, MAX(AssignDate) AS MaxDate
                FROM captain_ship_history
                GROUP BY CaptainID
            ) latest ON c.CaptainID = latest.CaptainID
            JOIN captain_ship_history h ON c.CaptainID = h.CaptainID AND h.AssignDate = latest.MaxDate
            JOIN ship s ON h.ShipID = s.ShipID
            WHERE h.ShipID = ?
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, selectedShipID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("CaptainID"),
                        rs.getString("Name"),
                        rs.getString("LicenseNumber"),
                        rs.getString("Nationality"),
                        rs.getDate("HireDate"),
                        rs.getInt("ExperienceYears"),
                        rs.getString("AssignedShip")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Filtering failed: " + e.getMessage());
        }
    }

    private void openCaptainForm(Integer rowIndex) {
        JDialog form = new JDialog(this, "Captain Form", true);
        form.setLayout(new GridLayout(7, 2));
        form.setSize(400, 350);
        form.setLocationRelativeTo(this);

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField licenseField = new JTextField();
        JTextField nationalityField = new JTextField();
        JTextField hireDateField = new JTextField();
        JTextField expField = new JTextField();

        form.add(new JLabel("Captain ID:")); form.add(idField);
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("License Number:")); form.add(licenseField);
        form.add(new JLabel("Nationality:")); form.add(nationalityField);
        form.add(new JLabel("Hire Date (YYYY-MM-DD):")); form.add(hireDateField);
        form.add(new JLabel("Experience (years):")); form.add(expField);

        JButton saveBtn = new JButton("Save");
        form.add(new JLabel()); form.add(saveBtn);

        if (rowIndex != null) {
            idField.setText(tableModel.getValueAt(rowIndex, 0).toString());
            idField.setEditable(false);
            nameField.setText(tableModel.getValueAt(rowIndex, 1).toString());
            licenseField.setText(tableModel.getValueAt(rowIndex, 2).toString());
            nationalityField.setText(tableModel.getValueAt(rowIndex, 3).toString());
            hireDateField.setText(tableModel.getValueAt(rowIndex, 4).toString());
            expField.setText(tableModel.getValueAt(rowIndex, 5).toString());
        }

        saveBtn.addActionListener(e -> {
            if (idField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "Captain ID cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnector.getConnection()) {
                if (rowIndex == null) {
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO captain VALUES (?, ?, ?, ?, ?, ?)");
                    ps.setInt(1, Integer.parseInt(idField.getText()));
                    ps.setString(2, nameField.getText());
                    ps.setString(3, licenseField.getText());
                    ps.setString(4, nationalityField.getText());
                    ps.setDate(5, Date.valueOf(hireDateField.getText()));
                    ps.setInt(6, Integer.parseInt(expField.getText()));
                    ps.executeUpdate();
                } else {
                    PreparedStatement ps = conn.prepareStatement("""
                UPDATE captain SET Name=?, LicenseNumber=?, Nationality=?, HireDate=?, ExperienceYears=?
                WHERE CaptainID=?
            """);
                    ps.setString(1, nameField.getText());
                    ps.setString(2, licenseField.getText());
                    ps.setString(3, nationalityField.getText());
                    ps.setDate(4, Date.valueOf(hireDateField.getText()));
                    ps.setInt(5, Integer.parseInt(expField.getText()));
                    ps.setInt(6, Integer.parseInt(idField.getText()));
                    ps.executeUpdate();
                }
                loadCaptains();
                form.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });

        form.setVisible(true);
    }

    private void deleteCaptain() {
        int row = captainTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a captain to delete.");
            return;
        }
        int captainId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM captain WHERE CaptainID=?");
            ps.setInt(1, captainId);
            ps.executeUpdate();
            loadCaptains();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete error: " + e.getMessage());
        }
    }

    private void assignCaptainToShip() {
        int row = captainTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a captain first.");
            return;
        }
        int captainId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        JDialog dialog = new JDialog(this, "Assign Captain to Ship", true);
        dialog.setLayout(new GridLayout(3, 1));
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(this);

        shipComboBox = new JComboBox<>();
        shipIDs = new Vector<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT ShipID, Name FROM ship")) {
            while (rs.next()) {
                shipIDs.add(rs.getInt("ShipID"));
                shipComboBox.addItem(rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load ships: " + e.getMessage());
            return;
        }

        JButton assignBtn = new JButton("Assign");

        dialog.add(new JLabel("Select Ship:"));
        dialog.add(shipComboBox);
        dialog.add(assignBtn);

        assignBtn.addActionListener(e -> {
            int selectedShipId = shipIDs.get(shipComboBox.getSelectedIndex());
            try (Connection conn = DatabaseConnector.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("""
                    INSERT INTO captain_ship_history (CaptainID, ShipID, AssignDate)
                    VALUES (?, ?, NOW())
                """);
                ps.setInt(1, captainId);
                ps.setInt(2, selectedShipId);
                ps.executeUpdate();
                dialog.dispose();
                loadCaptains();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Assignment failed: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void showAssignmentHistory() {
        int row = captainTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a captain.");
            return;
        }
        int captainId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        StringBuilder history = new StringBuilder("Assignment History:\n");

        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("""
                SELECT s.Name, h.AssignDate FROM captain_ship_history h
                JOIN ship s ON h.ShipID = s.ShipID
                WHERE h.CaptainID = ?
                ORDER BY h.AssignDate DESC
            """);
            ps.setInt(1, captainId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                history.append(rs.getDate("AssignDate")).append(" → ").append(rs.getString("Name")).append("\n");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load history: " + e.getMessage());
            return;
        }

        JOptionPane.showMessageDialog(this, history.length() > 20 ? history.toString() : "No history found.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CaptainManagementPanel().setVisible(true));
    }
}
