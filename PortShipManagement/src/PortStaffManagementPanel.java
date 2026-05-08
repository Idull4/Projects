import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List;

public class PortStaffManagementPanel extends JFrame {
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField roleField, portIDField, shiftStartField, shiftEndField;
    private Map<Integer, String> portMap = new HashMap<>();

    public PortStaffManagementPanel() {
        setTitle("Port Staff Management Panel");
        setSize(1100, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Başlık
        JLabel title = new JLabel("Port Staff Management Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        // Tablo modeli ve tablo
        tableModel = new DefaultTableModel(new String[]{
                "StaffID", "FullName", "Role", "Phone", "Email", "AssignedPortID", "PortName", "ShiftStart", "ShiftEnd"
        }, 0);
        staffTable = new JTable(tableModel);
        add(new JScrollPane(staffTable), BorderLayout.CENTER);

        // 🔍 Arama Paneli
        JPanel searchPanel = new JPanel(new FlowLayout());
        roleField = new JTextField(10);
        portIDField = new JTextField(5);
        shiftStartField = new JTextField(6);
        shiftEndField = new JTextField(6);
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");

        searchPanel.add(new JLabel("Role:"));
        searchPanel.add(roleField);
        searchPanel.add(new JLabel("PortID:"));
        searchPanel.add(portIDField);
        searchPanel.add(new JLabel("Shift Start:"));
        searchPanel.add(shiftStartField);
        searchPanel.add(new JLabel("Shift End:"));
        searchPanel.add(shiftEndField);
        searchPanel.add(searchBtn);
        searchPanel.add(refreshBtn);
        add(searchPanel, BorderLayout.NORTH);

        // Butonlar
        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // Eventler
        searchBtn.addActionListener(e -> search());
        refreshBtn.addActionListener(e -> loadData());
        addBtn.addActionListener(e -> openForm(false));
        updateBtn.addActionListener(e -> openForm(true));
        deleteBtn.addActionListener(e -> delete());

        loadPortMap();
        loadData();

        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        buttonPanel.add(backToMenuBtn);
    }

    private void loadPortMap() {
        portMap.clear();
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT PortID, Name FROM port")) {
            while (rs.next()) {
                portMap.put(rs.getInt("PortID"), rs.getString("Name"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to load ports: " + e.getMessage());
        }
    }

    private void loadData() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM portstaff")) {
            while (rs.next()) {
                int portID = rs.getInt("AssignedPortID");
                String portName = portMap.getOrDefault(portID, "-");
                tableModel.addRow(new Object[]{
                        rs.getInt("StaffID"),
                        rs.getString("FullName"),
                        rs.getString("Role"),
                        rs.getString("Phone"),
                        rs.getString("Email"),
                        portID,
                        portName,
                        rs.getTime("ShiftStart"),
                        rs.getTime("ShiftEnd")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load error: " + e.getMessage());
        }
    }

    private void search() {
        tableModel.setRowCount(0);
        StringBuilder query = new StringBuilder("SELECT * FROM portstaff WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (!roleField.getText().trim().isEmpty()) {
            query.append(" AND Role = ?");
            params.add(roleField.getText().trim());
        }
        if (!portIDField.getText().trim().isEmpty()) {
            query.append(" AND AssignedPortID = ?");
            params.add(Integer.parseInt(portIDField.getText().trim()));
        }
        if (!shiftStartField.getText().trim().isEmpty()) {
            query.append(" AND ShiftStart = ?");
            params.add(Time.valueOf(shiftStartField.getText().trim()));
        }
        if (!shiftEndField.getText().trim().isEmpty()) {
            query.append(" AND ShiftEnd = ?");
            params.add(Time.valueOf(shiftEndField.getText().trim()));
        }

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int portID = rs.getInt("AssignedPortID");
                String portName = portMap.getOrDefault(portID, "-");
                tableModel.addRow(new Object[]{
                        rs.getInt("StaffID"),
                        rs.getString("FullName"),
                        rs.getString("Role"),
                        rs.getString("Phone"),
                        rs.getString("Email"),
                        portID,
                        portName,
                        rs.getTime("ShiftStart"),
                        rs.getTime("ShiftEnd")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search error: " + e.getMessage());
        }
    }


    private void openForm(boolean isUpdate) {
        int selectedRow = staffTable.getSelectedRow();
        if (isUpdate && selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }

        JTextField staffIdField = new JTextField();
        JTextField fullNameField = new JTextField();
        JTextField roleField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField assignedPortField = new JTextField();
        JTextField shiftStartField = new JTextField();
        JTextField shiftEndField = new JTextField();

        if (isUpdate) {
            staffIdField.setText(tableModel.getValueAt(selectedRow, 0).toString());
            staffIdField.setEditable(false);
            fullNameField.setText(tableModel.getValueAt(selectedRow, 1).toString());
            roleField.setText(tableModel.getValueAt(selectedRow, 2).toString());
            phoneField.setText(tableModel.getValueAt(selectedRow, 3).toString());
            emailField.setText(tableModel.getValueAt(selectedRow, 4).toString());
            assignedPortField.setText(tableModel.getValueAt(selectedRow, 5).toString());
            shiftStartField.setText(tableModel.getValueAt(selectedRow, 7).toString());
            shiftEndField.setText(tableModel.getValueAt(selectedRow, 8).toString());
        }

        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Staff ID:")); panel.add(staffIdField);
        panel.add(new JLabel("Full Name:")); panel.add(fullNameField);
        panel.add(new JLabel("Role:")); panel.add(roleField);
        panel.add(new JLabel("Phone:")); panel.add(phoneField);
        panel.add(new JLabel("Email:")); panel.add(emailField);
        panel.add(new JLabel("Assigned Port ID:")); panel.add(assignedPortField);
        panel.add(new JLabel("Shift Start (HH:MM:SS):")); panel.add(shiftStartField);
        panel.add(new JLabel("Shift End (HH:MM:SS):")); panel.add(shiftEndField);

        int result = JOptionPane.showConfirmDialog(this, panel, isUpdate ? "Update Staff" : "Add Staff", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if (!isUpdate && staffIdField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Staff ID cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnector.getConnection()) {
                PreparedStatement ps;
                if (isUpdate) {
                    ps = conn.prepareStatement("""
                    UPDATE portstaff SET FullName=?, Role=?, Phone=?, Email=?, AssignedPortID=?, ShiftStart=?, ShiftEnd=? 
                    WHERE StaffID=?
                """);
                    ps.setString(1, fullNameField.getText());
                    ps.setString(2, roleField.getText());
                    ps.setString(3, phoneField.getText());
                    ps.setString(4, emailField.getText());
                    ps.setInt(5, Integer.parseInt(assignedPortField.getText()));
                    ps.setTime(6, Time.valueOf(shiftStartField.getText()));
                    ps.setTime(7, Time.valueOf(shiftEndField.getText()));
                    ps.setInt(8, Integer.parseInt(staffIdField.getText()));
                } else {
                    ps = conn.prepareStatement("""
                    INSERT INTO portstaff (StaffID, FullName, Role, Phone, Email, AssignedPortID, ShiftStart, ShiftEnd)
                    VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """);
                    ps.setInt(1, Integer.parseInt(staffIdField.getText()));
                    ps.setString(2, fullNameField.getText());
                    ps.setString(3, roleField.getText());
                    ps.setString(4, phoneField.getText());
                    ps.setString(5, emailField.getText());
                    ps.setInt(6, Integer.parseInt(assignedPortField.getText()));
                    ps.setTime(7, Time.valueOf(shiftStartField.getText()));
                    ps.setTime(8, Time.valueOf(shiftEndField.getText()));
                }
                ps.executeUpdate();
                loadData();
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Save error: " + e.getMessage());
            }
        }
    }



    private void delete() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }
        int staffId = (int) tableModel.getValueAt(selectedRow, 0);
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM portstaff WHERE StaffID = ?")) {
            ps.setInt(1, staffId);
            ps.executeUpdate();
            loadData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PortStaffManagementPanel().setVisible(true));
    }
}
