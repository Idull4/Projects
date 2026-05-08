import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class CompanyManagementPanel extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public CompanyManagementPanel() {
        setTitle("Company Management Panel");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Company Management Panel", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        model = new DefaultTableModel(new String[]{
                "CompanyID", "Name", "HeadquarterCountry", "EstablishedYear"
        }, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton viewShipsBtn = new JButton("View Ships");
        JButton refreshBtn = new JButton("Refresh");

        buttonPanel.add(addBtn);
        buttonPanel.add(updateBtn);
        buttonPanel.add(deleteBtn);
        buttonPanel.add(viewShipsBtn);
        buttonPanel.add(refreshBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshBtn.addActionListener(e -> loadCompanies());
        addBtn.addActionListener(e -> openForm("Add", null));
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a company to update.");
                return;
            }
            openForm("Update", row);
        });
        deleteBtn.addActionListener(e -> deleteCompany());
        viewShipsBtn.addActionListener(e -> viewShips());

        loadCompanies();

        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        buttonPanel.add(backToMenuBtn);
    }

    private void loadCompanies() {
        model.setRowCount(0);
        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM company")) {
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("CompanyID"),
                        rs.getString("Name"),
                        rs.getString("HeadquarterCountry"),
                        rs.getInt("EstablishedYear")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load error: " + e.getMessage());
        }
    }

    private void openForm(String mode, Integer row) {
        JDialog dialog = new JDialog(this, mode + " Company", true);
        dialog.setLayout(new GridLayout(5, 2));
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField countryField = new JTextField();
        JTextField yearField = new JTextField();

        dialog.add(new JLabel("CompanyID:"));
        dialog.add(idField);
        dialog.add(new JLabel("Name:"));
        dialog.add(nameField);
        dialog.add(new JLabel("Headquarter Country:"));
        dialog.add(countryField);
        dialog.add(new JLabel("Established Year:"));
        dialog.add(yearField);

        if (mode.equals("Update") && row != null) {
            idField.setText(model.getValueAt(row, 0).toString());
            idField.setEnabled(false);
            nameField.setText(model.getValueAt(row, 1).toString());
            countryField.setText(model.getValueAt(row, 2).toString());
            yearField.setText(model.getValueAt(row, 3).toString());
        }

        JButton saveBtn = new JButton("Save");
        dialog.add(new JLabel());
        dialog.add(saveBtn);

        saveBtn.addActionListener(e -> {
            if (idField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "CompanyID cannot be empty!", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try (Connection conn = DatabaseConnector.getConnection()) {
                PreparedStatement ps;
                if (mode.equals("Add")) {
                    ps = conn.prepareStatement("INSERT INTO company VALUES (?, ?, ?, ?)");
                    ps.setInt(1, Integer.parseInt(idField.getText()));
                } else {
                    ps = conn.prepareStatement("UPDATE company SET Name=?, HeadquarterCountry=?, EstablishedYear=? WHERE CompanyID=?");
                    ps.setInt(4, Integer.parseInt(idField.getText()));
                }
                ps.setString(mode.equals("Add") ? 2 : 1, nameField.getText());
                ps.setString(mode.equals("Add") ? 3 : 2, countryField.getText());
                ps.setInt(mode.equals("Add") ? 4 : 3, Integer.parseInt(yearField.getText()));
                ps.executeUpdate();
                dialog.dispose();
                loadCompanies();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "Save error: " + ex.getMessage());
            }
        });


        dialog.setVisible(true);
    }

    private void deleteCompany() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a company to delete.");
            return;
        }
        int id = Integer.parseInt(model.getValueAt(row, 0).toString());
        try (Connection conn = DatabaseConnector.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM company WHERE CompanyID=?");
            ps.setInt(1, id);
            ps.executeUpdate();
            loadCompanies();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Delete error: " + e.getMessage());
        }
    }

    private void viewShips() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a company to view ships.");
            return;
        }
        int companyId = Integer.parseInt(model.getValueAt(row, 0).toString());

        DefaultTableModel shipModel = new DefaultTableModel(new String[]{
                "ShipID", "Name", "Type", "OperatingCompanyID"
        }, 0);
        JTable shipTable = new JTable(shipModel);

        try (Connection conn = DatabaseConnector.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT * FROM ship WHERE OperatingCompanyID = ?")) {
            ps.setInt(1, companyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                shipModel.addRow(new Object[]{
                        rs.getInt("ShipID"),
                        rs.getString("Name"),
                        rs.getString("Type"),
                        rs.getInt("OperatingCompanyID")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Load error: " + e.getMessage());
            return;
        }

        JDialog dialog = new JDialog(this, "Ships of Selected Company", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 300);
        dialog.setLocationRelativeTo(this);
        dialog.add(new JScrollPane(shipTable), BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CompanyManagementPanel().setVisible(true));
    }
}
