import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PortManagementPanel extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField locationFilter, countryFilter, capacityFilter;

    public PortManagementPanel() {
        setTitle("Port Management Panel");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Port Management Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{"PortID", "Name", "Location", "Country", "CapacityDocks"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout());
        locationFilter = new JTextField(10);
        countryFilter = new JTextField(10);
        capacityFilter = new JTextField(5);
        JButton searchBtn = new JButton("Search");
        JButton refreshBtn = new JButton("Refresh");

        filterPanel.add(new JLabel("Location:"));
        filterPanel.add(locationFilter);
        filterPanel.add(new JLabel("Country:"));
        filterPanel.add(countryFilter);
        filterPanel.add(new JLabel("Capacity:"));
        filterPanel.add(capacityFilter);
        filterPanel.add(searchBtn);
        filterPanel.add(refreshBtn);
        add(filterPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadPorts());
        searchBtn.addActionListener(e -> filterPorts());

        addBtn.addActionListener(e -> openAddForm());
        updateBtn.addActionListener(e -> openUpdateForm());
        deleteBtn.addActionListener(e -> deletePort());

        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        buttonPanel.add(backToMenuBtn);

        loadPorts();
    }

    private void loadPorts() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection();
             ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM port")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("PortID"),
                        rs.getString("Name"),
                        rs.getString("Location"),
                        rs.getString("Country"),
                        rs.getInt("CapacityDocks")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading ports: " + e.getMessage());
        }
    }

    private void filterPorts() {
        model.setRowCount(0);
        StringBuilder query = new StringBuilder("SELECT * FROM port WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (!locationFilter.getText().trim().isEmpty()) {
            query.append(" AND Location = ?");
            params.add(locationFilter.getText().trim());
        }
        if (!countryFilter.getText().trim().isEmpty()) {
            query.append(" AND Country = ?");
            params.add(countryFilter.getText().trim());
        }
        if (!capacityFilter.getText().trim().isEmpty()) {
            query.append(" AND CapacityDocks = ?");
            try {
                params.add(Integer.parseInt(capacityFilter.getText().trim()));
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Capacity must be a number.");
                return;
            }
        }

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("PortID"),
                        rs.getString("Name"),
                        rs.getString("Location"),
                        rs.getString("Country"),
                        rs.getInt("CapacityDocks")
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search failed: " + e.getMessage());
        }
    }


    private void openAddForm() {
        JDialog form = new JDialog(this, "Add Port", true);
        form.setLayout(new GridLayout(6, 2));
        form.setSize(400, 300);
        form.setLocationRelativeTo(this);

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField countryField = new JTextField();
        JTextField capacityField = new JTextField();

        form.add(new JLabel("PortID:")); form.add(idField);
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Location:")); form.add(locationField);
        form.add(new JLabel("Country:")); form.add(countryField);
        form.add(new JLabel("CapacityDocks:")); form.add(capacityField);

        JButton save = new JButton("Save");
        form.add(new JLabel()); form.add(save);

        save.addActionListener(e -> {
            if (idField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "PortID cannot be empty!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int portID = Integer.parseInt(idField.getText().trim());
                int capacity = capacityField.getText().trim().isEmpty() ? 0 : Integer.parseInt(capacityField.getText().trim());

                try (Connection conn = DatabaseConnector.getConnection()) {
                    PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO port (PortID, Name, Location, Country, CapacityDocks) VALUES (?, ?, ?, ?, ?)");
                    ps.setInt(1, portID);
                    ps.setString(2, nameField.getText().trim());
                    ps.setString(3, locationField.getText().trim());
                    ps.setString(4, countryField.getText().trim());
                    ps.setInt(5, capacity);
                    ps.executeUpdate();
                    form.dispose();
                    loadPorts();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(form, "PortID (and Capacity if filled) must be numeric.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(form, "Insert error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        form.setVisible(true);
    }

    private void openUpdateForm() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a port to update.");
            return;
        }
        int portId = (int) model.getValueAt(row, 0);
        String name = model.getValueAt(row, 1).toString();
        String location = model.getValueAt(row, 2).toString();
        String country = model.getValueAt(row, 3).toString();
        int capacity = (int) model.getValueAt(row, 4);

        JDialog form = new JDialog(this, "Update Port", true);
        form.setLayout(new GridLayout(5, 2));
        form.setSize(400, 250);
        form.setLocationRelativeTo(this);

        JTextField nameField = new JTextField(name);
        JTextField locationField = new JTextField(location);
        JTextField countryField = new JTextField(country);
        JTextField capacityField = new JTextField(String.valueOf(capacity));

        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Location:")); form.add(locationField);
        form.add(new JLabel("Country:")); form.add(countryField);
        form.add(new JLabel("CapacityDocks:")); form.add(capacityField);

        JButton save = new JButton("Save");
        form.add(new JLabel()); form.add(save);

        save.addActionListener(e -> {
            try (Connection conn = DatabaseConnector.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("UPDATE port SET Name=?, Location=?, Country=?, CapacityDocks=? WHERE PortID=?");
                ps.setString(1, nameField.getText());
                ps.setString(2, locationField.getText());
                ps.setString(3, countryField.getText());
                ps.setInt(4, Integer.parseInt(capacityField.getText()));
                ps.setInt(5, portId);
                ps.executeUpdate();
                form.dispose();
                loadPorts();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(form, "Update error: " + ex.getMessage());
            }
        });

        form.setVisible(true);
    }

    private void deletePort() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a port to delete.");
            return;
        }
        int portId = (int) model.getValueAt(row, 0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM port WHERE PortID = ?");
            ps.setInt(1, portId);
            ps.executeUpdate();
            loadPorts();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PortManagementPanel().setVisible(true));
    }
}
