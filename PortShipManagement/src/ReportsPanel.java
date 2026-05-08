import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ReportsPanel extends JFrame {
    private JTable reportTable;
    private DefaultTableModel tableModel;

    public ReportsPanel() {
        setTitle("Reports Panel");
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Başlık
        JLabel title = new JLabel("Reports Panel", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Tablo Modeli ve Görüntüleme Alanı
        tableModel = new DefaultTableModel();
        reportTable = new JTable(tableModel);
        add(new JScrollPane(reportTable), BorderLayout.CENTER);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton btnHazardous = new JButton("Hazardous Cargo Report");
        JButton btnAssignments = new JButton("Captain Assignments View");
        JButton btnShipCompany = new JButton("Ships and Companies View");
        JButton btnRefresh = new JButton("Clear Table");

        buttonPanel.add(btnHazardous);
        buttonPanel.add(btnAssignments);
        buttonPanel.add(btnShipCompany);
        buttonPanel.add(btnRefresh);
        add(buttonPanel, BorderLayout.SOUTH);
        JButton backToMenuBtn = new JButton("Back to Main Menu");
        backToMenuBtn.addActionListener(e -> {
            dispose();
            new DashboardPanel();
        });
        buttonPanel.add(backToMenuBtn);

        // Aksiyonlar
        btnHazardous.addActionListener(e -> loadView("vw_hazardouscargoinfo"));
        btnAssignments.addActionListener(e -> loadView("vw_captainshipassignments"));
        btnShipCompany.addActionListener(e -> loadView("vw_shipswithcompany"));
        btnRefresh.addActionListener(e -> clearTable());
    }

    private void loadView(String viewName) {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        String query = "SELECT * FROM " + viewName;

        try (Connection conn = DatabaseConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            // Sütunları ekle
            for (int i = 1; i <= columnCount; i++) {
                tableModel.addColumn(meta.getColumnName(i));
            }

            // Satırları ekle
            while (rs.next()) {
                Object[] row = new Object[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getObject(i);
                }
                tableModel.addRow(row);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error loading view: " + ex.getMessage());
        }
    }

    private void clearTable() {
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
    }



    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReportsPanel().setVisible(true));
    }
}

