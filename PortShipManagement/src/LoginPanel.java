import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Map;

public class LoginPanel extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    private static final Map<String, String> validUsers = Map.of(
            "melis", "melis123",
            "zeynep", "zeynep123",
            "batikan", "batikan123",
            "mert", "mert123"
    );

    public LoginPanel() {
        setTitle("Portship Management - Login Screen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 270);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());


        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        JLabel titleLabel = new JLabel("Welcome to Portship Management App!", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        JLabel subtitleLabel = new JLabel("Please login:", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);


        JPanel formPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 10, 40));

        JLabel userLabel = new JLabel("Username:");
        usernameField = new JTextField();
        usernameField.setPreferredSize(new Dimension(150, 24));

        JLabel passLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(150, 24));

        formPanel.add(userLabel);
        formPanel.add(usernameField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);


        JPanel buttonPanel = new JPanel();
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(100, 28)); // isteğe bağlı
        loginButton.addActionListener(this::handleLogin);
        buttonPanel.add(loginButton);


        add(titlePanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText();
        String password = String.valueOf(passwordField.getPassword());

        if (validUsers.containsKey(username) && validUsers.get(username).equals(password)) {
            JOptionPane.showMessageDialog(this, "Login successful!", "Successful", JOptionPane.INFORMATION_MESSAGE);
            dispose();
            new DashboardPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Wrong username or password!", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginPanel::new);
    }
}
