import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class VoyageManagementPanel extends JFrame {
    private JTable voyageTable;
    private DefaultTableModel tableModel;

    public VoyageManagementPanel() {
        setTitle("Voyage Management Panel");
        setSize(1100, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initFilterPanel();

        tableModel = new DefaultTableModel(new String[]{
                "VoyageID", "ShipID", "DeparturePortID", "ArrivalPortID",
                "DepartureDate", "ArrivalDate", "Status"
        }, 0);
        voyageTable = new JTable(tableModel);
        add(new JScrollPane(voyageTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton reportBtn = new JButton("Delay Report");
        JButton refreshBtn = new JButton("Refresh");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(reportBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadVoyageData());
        addBtn.addActionListener(e -> openVoyageForm(null));
        updateBtn.addActionListener(e -> {
            int row = voyageTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Please select a row.");
            } else {
                openVoyageForm(row);
            }
        });
        deleteBtn.addActionListener(e -> deleteSelectedVoyage());
        reportBtn.addActionListener(e -> showDelayedVoyages());

        loadVoyageData();

        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        buttonPanel.add(backToMenuBtn);
    }

    private void initFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField shipField = new JTextField(5);
        JTextField portField = new JTextField(5);
        JTextField dateField = new JTextField(10);
        JButton filterBtn = new JButton("Filter");
        JButton clearBtn = new JButton("Clear");

        filterPanel.add(new JLabel("Ship ID:"));
        filterPanel.add(shipField);
        filterPanel.add(new JLabel("Port ID:"));
        filterPanel.add(portField);
        filterPanel.add(new JLabel("Departure Date:"));
        filterPanel.add(dateField);
        filterPanel.add(filterBtn);
        filterPanel.add(clearBtn);

        add(filterPanel, BorderLayout.NORTH);

        filterBtn.addActionListener(e -> {
            String ship = shipField.getText().trim();
            String port = portField.getText().trim();
            String date = dateField.getText().trim();
            loadVoyageDataWithFilters(ship, port, date);
        });

        clearBtn.addActionListener(e -> {
            shipField.setText("");
            portField.setText("");
            dateField.setText("");
            loadVoyageData();
        });
    }

    private void loadVoyageData() {
        tableModel.setRowCount(0);
        String sql = """
            SELECT v.VoyageID, v.ShipID, v.DeparturePortID, v.ArrivalPortID,
                   v.DepartureDate, v.ArrivalDate, s.Status
            FROM voyage v
            LEFT JOIN voyagestatus s ON v.VoyageID = s.VoyageID
        """;

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("VoyageID"),
                        rs.getInt("ShipID"),
                        rs.getInt("DeparturePortID"),
                        rs.getInt("ArrivalPortID"),
                        rs.getDate("DepartureDate"),
                        rs.getDate("ArrivalDate"),
                        rs.getString("Status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Data upload error: " + e.getMessage());
        }
    }

    private void loadVoyageDataWithFilters(String ship, String port, String date) {
        tableModel.setRowCount(0);
        StringBuilder sql = new StringBuilder("""
            SELECT v.VoyageID, v.ShipID, v.DeparturePortID, v.ArrivalPortID,
                   v.DepartureDate, v.ArrivalDate, s.Status
            FROM voyage v
            LEFT JOIN voyagestatus s ON v.VoyageID = s.VoyageID
            WHERE 1=1
        """);

        if (!ship.isEmpty()) sql.append(" AND v.ShipID = ").append(ship);
        if (!port.isEmpty()) sql.append(" AND (v.DeparturePortID = ").append(port)
                .append(" OR v.ArrivalPortID = ").append(port).append(")");
        if (!date.isEmpty()) sql.append(" AND v.DepartureDate = '").append(date).append("'");

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql.toString())) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("VoyageID"),
                        rs.getInt("ShipID"),
                        rs.getInt("DeparturePortID"),
                        rs.getInt("ArrivalPortID"),
                        rs.getDate("DepartureDate"),
                        rs.getDate("ArrivalDate"),
                        rs.getString("Status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Filtering Error: " + e.getMessage());
        }
    }

    private void openVoyageForm(Integer rowIndex) {
        JDialog form = new JDialog(this, "Voyage Form", true);
        form.setSize(400, 400);
        form.setLayout(new GridLayout(8, 2));
        form.setLocationRelativeTo(this);

        JTextField voyageId = new JTextField();
        JTextField shipId = new JTextField();
        JTextField depPort = new JTextField();
        JTextField arrPort = new JTextField();
        JTextField depDate = new JTextField();
        JTextField arrDate = new JTextField();
        JTextField status = new JTextField();

        if (rowIndex != null) {
            voyageId.setText(tableModel.getValueAt(rowIndex, 0).toString());
            voyageId.setEditable(false);
            shipId.setText(tableModel.getValueAt(rowIndex, 1).toString());
            depPort.setText(tableModel.getValueAt(rowIndex, 2).toString());
            arrPort.setText(tableModel.getValueAt(rowIndex, 3).toString());
            depDate.setText(tableModel.getValueAt(rowIndex, 4).toString());
            arrDate.setText(tableModel.getValueAt(rowIndex, 5).toString());
            status.setText(tableModel.getValueAt(rowIndex, 6) != null ? tableModel.getValueAt(rowIndex, 6).toString() : "");
        }

        form.add(new JLabel("VoyageID:")); form.add(voyageId);
        form.add(new JLabel("ShipID:")); form.add(shipId);
        form.add(new JLabel("DeparturePortID:")); form.add(depPort);
        form.add(new JLabel("ArrivalPortID:")); form.add(arrPort);
        form.add(new JLabel("DepartureDate:")); form.add(depDate);
        form.add(new JLabel("ArrivalDate:")); form.add(arrDate);
        form.add(new JLabel("Status:")); form.add(status);

        JButton saveBtn = new JButton("Kaydet");
        form.add(new JLabel()); form.add(saveBtn);

        saveBtn.addActionListener(e -> {
            if (rowIndex == null && voyageId.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(form, "VoyageID cannot be empty!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnector.getConnection()) {
                if (rowIndex == null) {
                    PreparedStatement ps = conn.prepareStatement("INSERT INTO voyage VALUES (?, ?, ?, ?, ?, ?)");
                    ps.setInt(1, Integer.parseInt(voyageId.getText()));
                    ps.setInt(2, Integer.parseInt(shipId.getText()));
                    ps.setInt(3, Integer.parseInt(depPort.getText()));
                    ps.setInt(4, Integer.parseInt(arrPort.getText()));
                    ps.setDate(5, Date.valueOf(depDate.getText()));
                    ps.setDate(6, Date.valueOf(arrDate.getText()));
                    ps.executeUpdate();

                    if (!status.getText().isBlank()) {
                        PreparedStatement ps2 = conn.prepareStatement(
                                "INSERT INTO voyagestatus (VoyageID, Status, LastUpdate) VALUES (?, ?, NOW())");
                        ps2.setInt(1, Integer.parseInt(voyageId.getText()));
                        ps2.setString(2, status.getText());
                        ps2.executeUpdate();
                    }
                } else {
                    PreparedStatement ps = conn.prepareStatement("""
                UPDATE voyage SET ShipID=?, DeparturePortID=?, ArrivalPortID=?,
                DepartureDate=?, ArrivalDate=? WHERE VoyageID=?
            """);
                    ps.setInt(1, Integer.parseInt(shipId.getText()));
                    ps.setInt(2, Integer.parseInt(depPort.getText()));
                    ps.setInt(3, Integer.parseInt(arrPort.getText()));
                    ps.setDate(4, Date.valueOf(depDate.getText()));
                    ps.setDate(5, Date.valueOf(arrDate.getText()));
                    ps.setInt(6, Integer.parseInt(voyageId.getText()));
                    ps.executeUpdate();

                    PreparedStatement ps2 = conn.prepareStatement("""
                INSERT INTO voyagestatus (VoyageID, Status, LastUpdate)
                VALUES (?, ?, NOW())
                ON DUPLICATE KEY UPDATE Status=VALUES(Status), LastUpdate=NOW()
            """);
                    ps2.setInt(1, Integer.parseInt(voyageId.getText()));
                    ps2.setString(2, status.getText());
                    ps2.executeUpdate();
                }
                loadVoyageData();
                form.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Hata: " + ex.getMessage());
            }
        });


        form.setVisible(true);
    }

    private void deleteSelectedVoyage() {
        int row = voyageTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row delete.");
            return;
        }
        int voyageId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement("DELETE FROM voyagestatus WHERE VoyageID=?");
            ps1.setInt(1, voyageId);
            ps1.executeUpdate();

            PreparedStatement ps2 = conn.prepareStatement("DELETE FROM voyage WHERE VoyageID=?");
            ps2.setInt(1, voyageId);
            ps2.executeUpdate();

            loadVoyageData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Deletion error: " + e.getMessage());
        }
    }

    private void showDelayedVoyages() {
        tableModel.setRowCount(0);
        String sql = """
            SELECT v.VoyageID, v.ShipID, v.DeparturePortID, v.ArrivalPortID,
                   v.DepartureDate, v.ArrivalDate, s.Status
            FROM voyage v
            JOIN voyagestatus s ON v.VoyageID = s.VoyageID
            WHERE s.Status = 'Delayed'
        """;
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("VoyageID"),
                        rs.getInt("ShipID"),
                        rs.getInt("DeparturePortID"),
                        rs.getInt("ArrivalPortID"),
                        rs.getDate("DepartureDate"),
                        rs.getDate("ArrivalDate"),
                        rs.getString("Status")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Report Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VoyageManagementPanel().setVisible(true));
    }
}
