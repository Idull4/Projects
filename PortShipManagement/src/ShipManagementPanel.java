import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Vector;

public class ShipManagementPanel extends JFrame {
    private JTable shipTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> companyComboBox;
    private Vector<Integer> companyIDs;

    public ShipManagementPanel() {
        setTitle("Ship Management Panel");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Ship Management Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{
                "ShipID", "Name", "Type", "FlagCountry", "Capacity", "BuiltYear", "Status",
                "EngineType", "InsuranceExpiry", "LastMaintenanceDate", "EngineerName",
                "OperatingCompanyID", "CompanyName"
        }, 0);
        shipTable = new JTable(tableModel);
        add(new JScrollPane(shipTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Ship");
        JButton updateBtn = new JButton("Update Ship");
        JButton deleteBtn = new JButton("Delete");
        JButton maintBtn = new JButton("Show Maintenance Alerts");
        JButton alarmBtn = new JButton("Show Insurance Alerts");
        JButton refreshBtn = new JButton("Refresh");

        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn);
        btnPanel.add(maintBtn); btnPanel.add(alarmBtn); btnPanel.add(refreshBtn);
        add(btnPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadShips());
        addBtn.addActionListener(e -> openShipForm(null));
        updateBtn.addActionListener(e -> {
            int row = shipTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row to update.");
            } else {
                openShipForm(row);
            }
        });
        deleteBtn.addActionListener(e -> deleteShip());
        maintBtn.addActionListener(e -> filterOldMaintenanceShips());
        alarmBtn.addActionListener(e -> filterInsuranceAlerts());

        loadShips();

        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        btnPanel.add(backToMenuBtn);
    }

    private void loadShips() {
        tableModel.setRowCount(0);
        String sql = """
            SELECT s.*, c.Name AS CompanyName
            FROM ship s
            LEFT JOIN company c ON s.OperatingCompanyID = c.CompanyID
        """;
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("ShipID"), rs.getString("Name"), rs.getString("Type"),
                        rs.getString("FlagCountry"), rs.getInt("Capacity"), rs.getInt("BuiltYear"),
                        rs.getString("Status"), rs.getString("EngineType"), rs.getDate("InsuranceExpiry"),
                        rs.getDate("LastMaintenanceDate"), rs.getString("EngineerName"),
                        rs.getInt("OperatingCompanyID"), rs.getString("CompanyName")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading ships: " + e.getMessage());
        }
    }

    private void openShipForm(Integer rowIndex) {
        JDialog form = new JDialog(this, "Ship Form", true);
        form.setLayout(new GridLayout(13, 2));
        form.setSize(500, 500);
        form.setLocationRelativeTo(this);

        JTextField[] fields = new JTextField[11];
        String[] labels = {"ShipID", "Name", "Type", "FlagCountry", "Capacity", "BuiltYear", "Status",
                "EngineType", "InsuranceExpiry (YYYY-MM-DD)", "LastMaintenanceDate", "EngineerName"};

        for (int i = 0; i < fields.length; i++) {
            fields[i] = new JTextField();
            form.add(new JLabel(labels[i]));
            form.add(fields[i]);
        }

        companyComboBox = new JComboBox<>();
        loadCompanies();
        form.add(new JLabel("Company:"));
        form.add(companyComboBox);

        if (rowIndex != null) {
            for (int i = 0; i < fields.length; i++) {
                fields[i].setText(String.valueOf(tableModel.getValueAt(rowIndex, i)));
            }
            fields[0].setEditable(false);
            companyComboBox.setSelectedItem(tableModel.getValueAt(rowIndex, 12).toString());
        }

        JButton saveBtn = new JButton("Save");
        form.add(new JLabel());
        form.add(saveBtn);

        saveBtn.addActionListener(e -> {
            if (rowIndex == null && fields[0].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "ShipID cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Integer.parseInt(fields[4].getText());
                Integer.parseInt(fields[5].getText());
                Date.valueOf(fields[8].getText());
                Date.valueOf(fields[9].getText());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid numeric or date input.");
                return;
            }

            try (Connection conn = DatabaseConnector.getConnection()) {
                if (rowIndex == null) {
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO ship VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                    for (int i = 0; i < fields.length; i++) ps.setString(i + 1, fields[i].getText());
                    ps.setInt(12, companyIDs.get(companyComboBox.getSelectedIndex()));
                    ps.executeUpdate();
                } else {
                    PreparedStatement ps = conn.prepareStatement("""
                UPDATE ship SET Name=?, Type=?, FlagCountry=?, Capacity=?, BuiltYear=?, Status=?,
                EngineType=?, InsuranceExpiry=?, LastMaintenanceDate=?, EngineerName=?, OperatingCompanyID=?
                WHERE ShipID=?
            """);
                    for (int i = 1; i < fields.length; i++) ps.setString(i, fields[i].getText());
                    ps.setInt(11, companyIDs.get(companyComboBox.getSelectedIndex()));
                    ps.setString(12, fields[0].getText());
                    ps.executeUpdate();
                }
                loadShips();
                form.dispose();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
            }
        });


        form.setVisible(true);
    }

    private void loadCompanies() {
        companyComboBox.removeAllItems();
        companyIDs = new Vector<>();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT CompanyID, Name FROM company")) {
            while (rs.next()) {
                companyIDs.add(rs.getInt("CompanyID"));
                companyComboBox.addItem(rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load companies: " + e.getMessage());
        }
    }

    private void deleteShip() {
        int row = shipTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete.");
            return;
        }
        int shipId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM ship WHERE ShipID=?");
            ps.setInt(1, shipId);
            ps.executeUpdate();
            loadShips();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete error: " + e.getMessage());
        }
    }

    private void filterOldMaintenanceShips() {
        LocalDate today = LocalDate.now();
        tableModel.setRowCount(0);

        String sql = """
            SELECT s.*, c.Name AS CompanyName
            FROM ship s
            LEFT JOIN company c ON s.OperatingCompanyID = c.CompanyID
            WHERE DATEDIFF(CURDATE(), LastMaintenanceDate) > 180
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("ShipID"), rs.getString("Name"), rs.getString("Type"),
                        rs.getString("FlagCountry"), rs.getInt("Capacity"), rs.getInt("BuiltYear"),
                        rs.getString("Status"), rs.getString("EngineType"), rs.getDate("InsuranceExpiry"),
                        rs.getDate("LastMaintenanceDate"), rs.getString("EngineerName"),
                        rs.getInt("OperatingCompanyID"), rs.getString("CompanyName")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering maintenance alerts: " + e.getMessage());
        }
    }

    private void filterInsuranceAlerts() {
        LocalDate today = LocalDate.now();
        tableModel.setRowCount(0);

        String sql = """
            SELECT s.*, c.Name AS CompanyName
            FROM ship s
            LEFT JOIN company c ON s.OperatingCompanyID = c.CompanyID
            WHERE InsuranceExpiry <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("ShipID"), rs.getString("Name"), rs.getString("Type"),
                        rs.getString("FlagCountry"), rs.getInt("Capacity"), rs.getInt("BuiltYear"),
                        rs.getString("Status"), rs.getString("EngineType"), rs.getDate("InsuranceExpiry"),
                        rs.getDate("LastMaintenanceDate"), rs.getString("EngineerName"),
                        rs.getInt("OperatingCompanyID"), rs.getString("CompanyName")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error filtering insurance alerts: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ShipManagementPanel().setVisible(true));
    }
}
