import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPanel extends JPanel {
    private MainFrame mainFrame;

    public LoginPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Title
        JLabel titleLabel = new JLabel("Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 10, 20, 10);
        add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 1;
        add(new JLabel("Username:"), gbc);
        JTextField usernameField = new JTextField(15);
        gbc.gridx = 1;
        add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2;
        add(new JLabel("Password:"), gbc);
        JPasswordField passwordField = new JPasswordField(15);
        gbc.gridx = 1;
        add(passwordField, gbc);

        // Login button
        JButton loginButton = new JButton("Login");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        add(loginButton, gbc);

        // Action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill in all fields!");
                return;
            }

            try (Connection con = DatabaseConnection.connect()) {
                PreparedStatement stmt = con.prepareStatement("SELECT CheckLogin(?, ?) AS result");
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int result = rs.getInt("result");

                    if (result == 1 || result == 2) {
                        // FIXED: usertype1 (result == 1) should be admin, usertype2 (result == 2) should be regular user
                        boolean isAdmin = (result == 1);  // Changed from (result == 2) to (result == 1)

                        // Debug output to verify the login result
                        System.out.println("Login result: " + result + " - User is admin: " + isAdmin);

                        mainFrame.getMainPanel().add(new MenuPanel(mainFrame, isAdmin), "Menu");
                        mainFrame.showPage("Menu");
                    } else {
                        JOptionPane.showMessageDialog(this, "Login failed. Try again.");
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Database error!");
            }
        });
    }
}