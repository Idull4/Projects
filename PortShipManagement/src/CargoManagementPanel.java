import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class CargoManagementPanel extends JFrame {
    private JTable cargoTable;
    private DefaultTableModel tableModel;
    private JTextField productFilterField, portFilterField, statusFilterField;
    private JButton addButton, editButton, deleteButton, filterButton;

    public CargoManagementPanel() {
        setTitle("Cargo Management Panel");
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Tablo
        String[] columns = {"CargoID", "VoyageID", "ProductID", "DestinationPortID", "Weight", "ContainerType",
                "ContainerSeal", "ContainerStatus", "LoadedByStaffID", "LoadingDate", "UnloadingDate"};
        tableModel = new DefaultTableModel(columns, 0);
        cargoTable = new JTable(tableModel);
        add(new JScrollPane(cargoTable), BorderLayout.CENTER);

        // Filtre ve Buton Paneli
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        addButton = new JButton("Add Cargo");
        editButton = new JButton("Edit Selected");
        deleteButton = new JButton("Delete Selected");

        productFilterField = new JTextField(6);
        portFilterField = new JTextField(6);
        statusFilterField = new JTextField(6);
        filterButton = new JButton("Filter");


        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(new JLabel("ProductID:")); topPanel.add(productFilterField);
        topPanel.add(new JLabel("DestinationPortID:")); topPanel.add(portFilterField);
        topPanel.add(new JLabel("CargoStatus:")); topPanel.add(statusFilterField);
        topPanel.add(filterButton);
        add(topPanel, BorderLayout.NORTH);

        fillTable();


        filterButton.addActionListener(e -> filterCargo());
        addButton.addActionListener(e -> openCargoDialog(null));
        editButton.addActionListener(e -> {
            int row = cargoTable.getSelectedRow();
            if (row != -1) {
                Vector<?> data = tableModel.getDataVector().elementAt(row);
                openCargoDialog(data);
            } else {
                JOptionPane.showMessageDialog(this, "Select a row to edit.");
            }
        });
        deleteButton.addActionListener(e -> deleteSelectedCargo());

        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        topPanel.add(backToMenuBtn);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> {
            fillTable(); // tabloyu sıfırla
            productFilterField.setText(""); // filtre alanlarını temizle
            portFilterField.setText("");
            statusFilterField.setText("");
        });
        topPanel.add(refreshBtn);

    }

    private void fillTable() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM cargo")) {
            while (rs.next()) {
                Object[] row = new Object[11];
                for (int i = 0; i < 11; i++) row[i] = rs.getObject(i + 1);
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void filterCargo() {
        tableModel.setRowCount(0);

        StringBuilder query = new StringBuilder("SELECT * FROM cargo WHERE 1=1");

        if (!productFilterField.getText().trim().isEmpty()) {
            try {
                int productId = Integer.parseInt(productFilterField.getText().trim());
                query.append(" AND ProductID = ").append(productId);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "ProductID must be a number.");
                return;
            }
        }

        if (!portFilterField.getText().trim().isEmpty()) {
            try {
                int portId = Integer.parseInt(portFilterField.getText().trim());
                query.append(" AND DestinationPortID = ").append(portId);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "DestinationPortID must be a number.");
                return;
            }
        }

        if (!statusFilterField.getText().trim().isEmpty()) {
            query.append(" AND ContainerStatus LIKE '%").append(statusFilterField.getText().trim()).append("%'");
        }

        try (Connection conn = DatabaseConnector.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query.toString())) {

            while (rs.next()) {
                Object[] row = new Object[11];
                for (int i = 0; i < 11; i++) row[i] = rs.getObject(i + 1);
                tableModel.addRow(row);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error during filtering: " + ex.getMessage());
        }
    }


    private void openCargoDialog(Vector<?> existingData) {
        JDialog dialog = new JDialog(this, existingData == null ? "Add Cargo" : "Edit Cargo", true);
        dialog.setSize(400, 500);
        dialog.setLayout(new GridLayout(13, 2));
        JTextField[] fields = new JTextField[11];
        for (int i = 0; i < 11; i++) fields[i] = new JTextField();

        String[] labels = {"CargoID", "VoyageID", "ProductID", "DestinationPortID", "Weight",
                "ContainerType", "ContainerSeal", "ContainerStatus", "LoadedByStaffID",
                "LoadingDate (yyyy-MM-dd)", "UnloadingDate (yyyy-MM-dd)"};
        for (int i = 0; i < labels.length; i++) {
            dialog.add(new JLabel(labels[i]));
            dialog.add(fields[i]);
        }

        if (existingData != null) {
            for (int i = 0; i < 11; i++) fields[i].setText(existingData.get(i).toString());
            fields[0].setEditable(false);
        }

        JButton saveBtn = new JButton("Save");
        dialog.add(saveBtn);
        saveBtn.addActionListener(e -> {
            if (fields[0].getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "CargoID cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnector.getConnection()) {
                String hazardCode = "";
                PreparedStatement ps = conn.prepareStatement("SELECT HazardCode FROM product WHERE ProductID = ?");
                ps.setInt(1, Integer.parseInt(fields[2].getText()));
                ResultSet rs = ps.executeQuery();
                if (rs.next()) hazardCode = rs.getString(1);

                if (hazardCode != null && !hazardCode.isEmpty() && fields[3].getText().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Hazardous cargo must have a destination port.");
                    return;
                }

                if (existingData == null) {
                    String sql = "INSERT INTO cargo VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    ps = conn.prepareStatement(sql);
                    for (int i = 0; i < 11; i++) {
                        ps.setObject(i + 1, fields[i].getText());
                    }
                } else {
                    String sql = "UPDATE cargo SET VoyageID=?, ProductID=?, DestinationPortID=?, Weight=?, ContainerType=?, " +
                            "ContainerSeal=?, ContainerStatus=?, LoadedByStaffID=?, LoadingDate=?, UnloadingDate=? " +
                            "WHERE CargoID=?";
                    ps = conn.prepareStatement(sql);
                    for (int i = 1; i <= 10; i++) {
                        ps.setObject(i, fields[i].getText());
                    }
                    ps.setObject(11, fields[0].getText());
                }

                ps.executeUpdate();
                dialog.dispose();
                fillTable();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "DB Error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }


    private void deleteSelectedCargo() {
        int row = cargoTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }
        int cargoId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM cargo WHERE CargoID = ?")) {
            ps.setInt(1, cargoId);
            ps.executeUpdate();
            fillTable();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "DB Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CargoManagementPanel().setVisible(true));
    }
}
