import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class DashboardPanel extends JFrame {

    public DashboardPanel() {
        setTitle("PortShip Management - Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);

        // 🔹 Başlık
        JLabel titleLabel = new JLabel("Portship Management - Main Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        // 🔹 Buton Paneli
        JPanel buttonPanel = new JPanel(new GridLayout(5, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 30, 40));

        // 🔹 Tüm Modül Butonları
        JButton cargoBtn = new JButton("Manage Cargo");
        JButton voyageBtn = new JButton("Manage Voyages");
        JButton shipBtn = new JButton("Manage Ships");
        JButton captainBtn = new JButton("Manage Captains");
        JButton crewBtn = new JButton("Manage Crew");
        JButton inspectionBtn = new JButton("Manage Inspections");
        JButton StaffBtn = new JButton("Manage Staff");
        JButton PortBtn = new JButton("Manage Port");
        JButton DockBtn = new JButton("Manage Dock");
        JButton ProdBtn = new JButton("Manage Product");
        JButton companyBtn = new JButton("Manage Companies");
        JButton reportsBtn = new JButton("Reports & Views");
        JButton logoutBtn = new JButton("Logout");

        // 🔹 Event Binding
        cargoBtn.addActionListener(e -> openPanel("Cargo"));
        voyageBtn.addActionListener(e -> openPanel("Voyage"));
        shipBtn.addActionListener(e -> openPanel("Ship"));
        captainBtn.addActionListener(e -> openPanel("Captain"));
        crewBtn.addActionListener(e -> openPanel("Crew"));
        inspectionBtn.addActionListener(e -> openPanel("Inspection"));
        StaffBtn.addActionListener(e -> openPanel("Staff"));
        PortBtn.addActionListener(e -> openPanel("Port"));
        DockBtn.addActionListener(e -> openPanel("Dock"));
        ProdBtn.addActionListener(e -> openPanel("Product"));
        companyBtn.addActionListener(e -> openPanel("Company"));
        reportsBtn.addActionListener(e -> openPanel("Reports"));
        logoutBtn.addActionListener(this::handleLogout);

        // 🔹 Butonları ekle
        buttonPanel.add(cargoBtn);
        buttonPanel.add(voyageBtn);
        buttonPanel.add(shipBtn);
        buttonPanel.add(captainBtn);
        buttonPanel.add(crewBtn);
        buttonPanel.add(inspectionBtn);
        buttonPanel.add(StaffBtn);
        buttonPanel.add(PortBtn);
        buttonPanel.add(DockBtn);
        buttonPanel.add(ProdBtn);
        buttonPanel.add(companyBtn);
        buttonPanel.add(reportsBtn);
        buttonPanel.add(logoutBtn);

        // 🔹 Yerleşim
        setLayout(new BorderLayout());
        add(titleLabel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);


    }

    private void openPanel(String panelName) {
        dispose();

        switch (panelName) {
            case "Cargo" -> new CargoManagementPanel().setVisible(true);
            case "Voyage" -> new VoyageManagementPanel().setVisible(true);
            case "Ship" -> new ShipManagementPanel().setVisible(true);
            case "Captain" -> new CaptainManagementPanel().setVisible(true);
            case "Crew" -> new CrewManagementPanel().setVisible(true);
            case "Inspection" -> new InspectionManagementPanel().setVisible(true);
            case "Staff" -> new PortStaffManagementPanel().setVisible(true); //only staff
            case "Port" -> new PortManagementPanel().setVisible(true);
            case "Dock" -> new DockManagementPanel().setVisible(true);
            case "Product" -> new ProductManagementPanel().setVisible(true);
            case "Company" -> new CompanyManagementPanel().setVisible(true);
            case "Reports" -> new ReportsPanel().setVisible(true);
            default -> JOptionPane.showMessageDialog(this,
                    "Unknown panel: " + panelName,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLogout(ActionEvent e) {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginPanel();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DashboardPanel::new);
    }
}
