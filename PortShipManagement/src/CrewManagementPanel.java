import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class CrewManagementPanel extends JFrame {
    private JTable crewTable;
    private DefaultTableModel tableModel;
    private Vector<Integer> shipIDs;
    private Vector<Integer> supervisorIDs;

    public CrewManagementPanel() {
        setTitle("Crew Management Panel");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Crew Management Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new String[]{
                "CrewID", "Name", "Ranking", "HireDate", "SupervisorID", "SupervisorName", "AssignedShip"
        }, 0);
        crewTable = new JTable(tableModel);
        add(new JScrollPane(crewTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add Crewmember");
        JButton deleteBtn = new JButton("Delete");
        JButton assignBtn = new JButton("Assign to Ship");
        JButton changeSupBtn = new JButton("Change Supervisor");
        JButton filterSupBtn = new JButton("Filter by Supervisor");
        JButton refreshBtn = new JButton("Refresh");
        buttonPanel.add(addBtn); buttonPanel.add(deleteBtn); buttonPanel.add(assignBtn);
        buttonPanel.add(changeSupBtn); buttonPanel.add(filterSupBtn); buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadCrew());
        addBtn.addActionListener(e -> openCrewForm());
        deleteBtn.addActionListener(e -> deleteCrew());
        assignBtn.addActionListener(e -> assignToShip());
        changeSupBtn.addActionListener(e -> changeSupervisor());
        filterSupBtn.addActionListener(e -> filterBySupervisor());

        loadCrew();

        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        buttonPanel.add(backToMenuBtn);
    }

    private void loadCrew() {
        tableModel.setRowCount(0);
        String sql = """
            SELECT c.*, s.Name AS SupervisorName, sh.Name AS ShipName
            FROM crewmember c
            LEFT JOIN crewmember s ON c.SupervisorID = s.CrewID
            LEFT JOIN ship sh ON c.ShipID = sh.ShipID
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("CrewID"),
                        rs.getString("Name"),
                        rs.getString("Ranking"),
                        rs.getDate("HireDate"),
                        rs.getObject("SupervisorID"),
                        rs.getString("SupervisorName"),
                        rs.getString("ShipName") != null ? rs.getString("ShipName") : "-"
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading crew: " + e.getMessage());
        }
    }

    private void openCrewForm() {
        JDialog form = new JDialog(this, "Add Crewmember", true);
        form.setLayout(new GridLayout(7, 2));
        form.setSize(400, 350);
        form.setLocationRelativeTo(this);

        JTextField crewIDField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField rankingField = new JTextField();
        JTextField hireDateField = new JTextField();

        JComboBox<String> shipBox = new JComboBox<>();
        JComboBox<String> supervisorBox = new JComboBox<>();
        shipIDs = new Vector<>();
        supervisorIDs = new Vector<>();

        try (Connection conn = DatabaseConnector.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT ShipID, Name FROM ship")) {
            while (rs.next()) {
                shipIDs.add(rs.getInt("ShipID"));
                shipBox.addItem(rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load ships.");
            return;
        }

        try (Connection conn = DatabaseConnector.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT CrewID, Name FROM crewmember")) {
            supervisorBox.addItem("None");
            supervisorIDs.add(null);
            while (rs.next()) {
                int id = rs.getInt("CrewID");
                String name = rs.getString("Name");
                supervisorBox.addItem(id + " - " + name);
                supervisorIDs.add(id);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load supervisors.");
            return;
        }

        form.add(new JLabel("CrewID:")); form.add(crewIDField);
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Ranking:")); form.add(rankingField);
        form.add(new JLabel("Hire Date (YYYY-MM-DD):")); form.add(hireDateField);
        form.add(new JLabel("Ship:")); form.add(shipBox);
        form.add(new JLabel("Supervisor (ID - Name):")); form.add(supervisorBox);

        JButton saveBtn = new JButton("Save");
        form.add(new JLabel()); form.add(saveBtn);

        saveBtn.addActionListener(e -> {
            // 🔍 Zorunlu alan kontrolü
            if (crewIDField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "CrewID cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "Name cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (rankingField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "Ranking cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (hireDateField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "Hire Date cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnector.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO crewmember (CrewID, Name, Ranking, HireDate, ShipID, SupervisorID)
            VALUES (?, ?, ?, ?, ?, ?)
        """);
                ps.setInt(1, Integer.parseInt(crewIDField.getText()));
                ps.setString(2, nameField.getText());
                ps.setString(3, rankingField.getText());
                ps.setDate(4, Date.valueOf(hireDateField.getText()));
                ps.setInt(5, shipIDs.get(shipBox.getSelectedIndex()));
                Integer supId = supervisorIDs.get(supervisorBox.getSelectedIndex());
                if (supId == null) ps.setNull(6, Types.INTEGER);
                else ps.setInt(6, supId);

                ps.executeUpdate();
                form.dispose();
                loadCrew();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(form, "Insert error: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(form, "Invalid date format. Use YYYY-MM-DD.");
            }
        });


        form.setVisible(true);
    }

    private void deleteCrew() {
        int row = crewTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a crewmember to delete.");
            return;
        }
        int crewId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM crewmember WHERE CrewID = ?");
            ps.setInt(1, crewId);
            ps.executeUpdate();
            loadCrew();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete error: " + e.getMessage());
        }
    }

    private void assignToShip() {
        int row = crewTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a crewmember.");
            return;
        }

        int crewId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        JComboBox<String> shipBox = new JComboBox<>();
        shipIDs = new Vector<>();

        try (Connection conn = DatabaseConnector.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT ShipID, Name FROM ship")) {
            while (rs.next()) {
                shipIDs.add(rs.getInt("ShipID"));
                shipBox.addItem(rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load ships.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, shipBox, "Select Ship", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int selectedShipId = shipIDs.get(shipBox.getSelectedIndex());
            try (Connection conn = DatabaseConnector.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE crewmember SET ShipID=? WHERE CrewID=?");
                ps.setInt(1, selectedShipId);
                ps.setInt(2, crewId);
                ps.executeUpdate();
                loadCrew();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Assign error: " + e.getMessage());
            }
        }
    }

    private void changeSupervisor() {
        int row = crewTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a crewmember.");
            return;
        }
        int crewId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        JComboBox<String> supBox = new JComboBox<>();
        Vector<Integer> supIDs = new Vector<>();

        try (Connection conn = DatabaseConnector.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT CrewID, Name FROM crewmember")) {
            supBox.addItem("None");
            supIDs.add(null);
            while (rs.next()) {
                int id = rs.getInt("CrewID");
                String name = rs.getString("Name");
                if (id != crewId) {
                    supBox.addItem(name);
                    supIDs.add(id);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load supervisors.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, supBox, "Select New Supervisor", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection conn = DatabaseConnector.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE crewmember SET SupervisorID=? WHERE CrewID=?");
                Integer supId = supIDs.get(supBox.getSelectedIndex());
                if (supId == null) ps.setNull(1, Types.INTEGER);
                else ps.setInt(1, supId);
                ps.setInt(2, crewId);
                ps.executeUpdate();
                loadCrew();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Supervisor change failed: " + e.getMessage());
            }
        }
    }

    private void filterBySupervisor() {
        JComboBox<String> supBox = new JComboBox<>();
        Vector<Integer> supIDs = new Vector<>();

        try (Connection conn = DatabaseConnector.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT CrewID, Name FROM crewmember")) {
            while (rs.next()) {
                supIDs.add(rs.getInt("CrewID"));
                supBox.addItem(rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load supervisors.");
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, supBox, "Select Supervisor", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            int selectedSupID = supIDs.get(supBox.getSelectedIndex());
            tableModel.setRowCount(0);
            String sql = """
                SELECT c.*, s.Name AS SupervisorName, sh.Name AS ShipName
                FROM crewmember c
                LEFT JOIN crewmember s ON c.SupervisorID = s.CrewID
                LEFT JOIN ship sh ON c.ShipID = sh.ShipID
                WHERE c.SupervisorID = ?
            """;

            try (Connection conn = DatabaseConnector.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, selectedSupID);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    tableModel.addRow(new Object[]{
                            rs.getInt("CrewID"),
                            rs.getString("Name"),
                            rs.getString("Ranking"),
                            rs.getDate("HireDate"),
                            rs.getObject("SupervisorID"),
                            rs.getString("SupervisorName"),
                            rs.getString("ShipName") != null ? rs.getString("ShipName") : "-"
                    });
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Filter error: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CrewManagementPanel().setVisible(true));
    }
}
