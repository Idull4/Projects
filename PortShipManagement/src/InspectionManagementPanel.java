import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InspectionManagementPanel extends JFrame {
    private JTable table;
    private DefaultTableModel model;
    private JTextField cargoSearchField, portSearchField, resultSearchField;

    public InspectionManagementPanel() {
        setTitle("Inspection Management Panel");
        setSize(1100, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Inspection Management Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "InspectionID", "CargoID", "PortID", "PortName", "InspectionDate", "Result", "Notes"
        }, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton viewCargoBtn = new JButton("View Cargo Details");
        JButton refreshBtn = new JButton("Refresh");

        cargoSearchField = new JTextField(6);
        portSearchField = new JTextField(6);
        resultSearchField = new JTextField(8);
        JButton searchBtn = new JButton("Search");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(viewCargoBtn);
        buttonPanel.add(refreshBtn);
        buttonPanel.add(new JLabel("CargoID:"));
        buttonPanel.add(cargoSearchField);
        buttonPanel.add(new JLabel("PortID:"));
        buttonPanel.add(portSearchField);
        buttonPanel.add(new JLabel("Result:"));
        buttonPanel.add(resultSearchField);
        buttonPanel.add(searchBtn);

        add(buttonPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> openForm(false));
        updateBtn.addActionListener(e -> openForm(true));
        deleteBtn.addActionListener(e -> deleteInspection());
        viewCargoBtn.addActionListener(e -> showCargoDetails());
        refreshBtn.addActionListener(e -> loadInspections());
        searchBtn.addActionListener(e -> filterInspections());

        loadInspections();

        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        buttonPanel.add(backToMenuBtn);
    }

    private void loadInspections() {
        model.setRowCount(0);
        String sql = """
            SELECT i.*, p.Name AS PortName
            FROM inspection i
            JOIN port p ON i.PortID = p.PortID
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("InspectionID"),
                        rs.getInt("CargoID"),
                        rs.getInt("PortID"),
                        rs.getString("PortName"),
                        rs.getDate("InspectionDate"),
                        rs.getString("Result"),
                        rs.getString("Notes")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load error: " + e.getMessage());
        }
    }

    private void openForm(boolean isUpdate) {
        JDialog form = new JDialog(this, isUpdate ? "Update Inspection" : "Add Inspection", true);
        form.setLayout(new GridLayout(8, 2));
        form.setSize(400, 400);
        form.setLocationRelativeTo(this);

        JTextField idField = new JTextField();
        JTextField cargoField = new JTextField();
        JTextField portField = new JTextField();
        JTextField portNameField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField resultField = new JTextField();
        JTextField notesField = new JTextField();

        if (isUpdate && table.getSelectedRow() != -1) {
            int row = table.getSelectedRow();
            idField.setText(table.getValueAt(row, 0).toString());
            cargoField.setText(table.getValueAt(row, 1).toString());
            portField.setText(table.getValueAt(row, 2).toString());
            portNameField.setText(table.getValueAt(row, 3).toString());
            dateField.setText(table.getValueAt(row, 4).toString());
            resultField.setText(table.getValueAt(row, 5).toString());
            notesField.setText(table.getValueAt(row, 6).toString());
        }

        form.add(new JLabel("InspectionID:")); form.add(idField);
        form.add(new JLabel("CargoID:")); form.add(cargoField);
        form.add(new JLabel("PortID:")); form.add(portField);
        form.add(new JLabel("PortName:")); form.add(portNameField);
        form.add(new JLabel("InspectionDate (YYYY-MM-DD):")); form.add(dateField);
        form.add(new JLabel("Result:")); form.add(resultField);
        form.add(new JLabel("Notes:")); form.add(notesField);

        JButton saveBtn = new JButton("Save");
        form.add(new JLabel()); form.add(saveBtn);

        saveBtn.addActionListener(e -> {
            if (!isUpdate && idField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "InspectionID cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (cargoField.getText().trim().isEmpty() ||
                    portField.getText().trim().isEmpty() ||
                    dateField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "Please fill all required fields (CargoID, PortID, InspectionDate).", "Missing Data", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try (Connection conn = DatabaseConnector.getConnection()) {
                int inspectionID = Integer.parseInt(idField.getText());
                int cargoID = Integer.parseInt(cargoField.getText());
                int portID = Integer.parseInt(portField.getText());
                java.sql.Date inspectionDate = java.sql.Date.valueOf(dateField.getText());
                String result = resultField.getText();
                String notes = notesField.getText();

                if (isUpdate) {
                    PreparedStatement ps = conn.prepareStatement("""
                        UPDATE inspection SET CargoID=?, PortID=?, InspectionDate=?, Result=?, Notes=? WHERE InspectionID=?
                    """);
                    ps.setInt(1, cargoID);
                    ps.setInt(2, portID);
                    ps.setDate(3, inspectionDate);
                    ps.setString(4, result);
                    ps.setString(5, notes);
                    ps.setInt(6, inspectionID);
                    ps.executeUpdate();
                } else {
                    PreparedStatement ps = conn.prepareStatement("""
                        INSERT INTO inspection (InspectionID, CargoID, PortID, InspectionDate, Result, Notes)
                        VALUES (?, ?, ?, ?, ?, ?)
                    """);
                    ps.setInt(1, inspectionID);
                    ps.setInt(2, cargoID);
                    ps.setInt(3, portID);
                    ps.setDate(4, inspectionDate);
                    ps.setString(5, result);
                    ps.setString(6, notes);
                    ps.executeUpdate();
                }

                form.dispose();
                loadInspections();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Save error: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            }
        });

        form.setVisible(true);
    }

    private void deleteInspection() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }
        int id = (int) model.getValueAt(row, 0);
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM inspection WHERE InspectionID = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
            loadInspections();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete error: " + e.getMessage());
        }
    }

    private void showCargoDetails() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an inspection.");
            return;
        }
        int cargoID = (int) model.getValueAt(row, 1);
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM cargo WHERE CargoID = ?")) {
            ps.setInt(1, cargoID);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                StringBuilder detail = new StringBuilder("Cargo Details:\n");
                ResultSetMetaData meta = rs.getMetaData();
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    detail.append(meta.getColumnName(i)).append(": ").append(rs.getString(i)).append("\n");
                }
                JOptionPane.showMessageDialog(this, detail.toString());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Cargo detail error: " + e.getMessage());
        }
    }

    private void filterInspections() {
        String cargoFilter = cargoSearchField.getText().trim();
        String portFilter = portSearchField.getText().trim();
        String resultFilter = resultSearchField.getText().trim();

        model.setRowCount(0);

        StringBuilder sql = new StringBuilder("""
        SELECT i.*, p.Name AS PortName
        FROM inspection i
        JOIN port p ON i.PortID = p.PortID
        WHERE 1=1
    """);

        List<Object> params = new ArrayList<>();

        if (!cargoFilter.isEmpty()) {
            sql.append(" AND i.CargoID = ?");
            params.add(Integer.parseInt(cargoFilter));
        }
        if (!portFilter.isEmpty()) {
            sql.append(" AND i.PortID = ?");
            params.add(Integer.parseInt(portFilter));
        }
        if (!resultFilter.isEmpty()) {
            sql.append(" AND i.Result LIKE ?");
            params.add("%" + resultFilter + "%");
        }

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("InspectionID"),
                        rs.getInt("CargoID"),
                        rs.getInt("PortID"),
                        rs.getString("PortName"),
                        rs.getDate("InspectionDate"),
                        rs.getString("Result"),
                        rs.getString("Notes")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Filter error: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InspectionManagementPanel().setVisible(true));
    }
}
