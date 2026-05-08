import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel {
    private MainFrame mainFrame;
    private boolean isAdmin;

    public MenuPanel(MainFrame mainFrame, boolean isAdmin) {
        this.mainFrame = mainFrame;
        this.isAdmin = isAdmin;

        // Debug output to verify admin status
        System.out.println("MenuPanel initialized - User is admin: " + isAdmin);

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Create and style the title
        JLabel titleLabel = new JLabel("Main Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Create the main button panel
        JPanel buttonPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        buttonPanel.setBackground(new Color(245, 245, 245));

        // Add regular user buttons (available to all users)
        addNavButton(buttonPanel, "Search Books", "Search", new Color(70, 130, 180));
        addNavButton(buttonPanel, "Display Book Info", "AllBooks", new Color(70, 130, 180));
        addNavButton(buttonPanel, "Rate Book", "RateBook", new Color(70, 130, 180));
        addNavButton(buttonPanel, "Add to Wishlist", "Wishlist", new Color(70, 130, 180));
        addNavButton(buttonPanel, "Wishlist Notifications", "WishlistNotify", new Color(70, 130, 180));
        addNavButton(buttonPanel, "Unread Books", "Unread", new Color(70, 130, 180));
        addNavButton(buttonPanel, "Favorite Authors", "Authors", new Color(70, 130, 180));
        addNavButton(buttonPanel, "Favorite Books", "Favorites", new Color(70, 130, 180));

        // Add admin-only buttons (only visible to admins)
        if (isAdmin) {
            // Add separator or visual distinction for admin functions
            JLabel adminLabel = new JLabel("Admin Functions", SwingConstants.CENTER);
            adminLabel.setFont(new Font("Arial", Font.BOLD, 16));
            adminLabel.setForeground(new Color(139, 69, 19));

            // Create a panel to hold the admin label spanning both columns
            JPanel adminHeaderPanel = new JPanel(new GridLayout(1, 2));
            adminHeaderPanel.setBackground(new Color(245, 245, 245));
            adminHeaderPanel.add(adminLabel);
            adminHeaderPanel.add(new JLabel()); // Empty space for alignment
            buttonPanel.add(adminHeaderPanel);
            buttonPanel.add(new JLabel()); // Empty space for alignment

            // Add admin buttons with different color
            addNavButton(buttonPanel, "Add Book", "AddBook", new Color(139, 69, 19));
            addNavButton(buttonPanel, "Edit Book", "EditBook", new Color(139, 69, 19));
            addNavButton(buttonPanel, "Delete Book", "DeleteBook", new Color(178, 34, 34));
        }

        add(buttonPanel, BorderLayout.CENTER);

        // Create bottom panel with logout and user info
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(245, 245, 245));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        // User status label
        JLabel userStatusLabel = new JLabel(
                isAdmin ? "Logged in as: Administrator" : "Logged in as: Regular User",
                SwingConstants.LEFT
        );
        userStatusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        userStatusLabel.setForeground(new Color(102, 102, 102));

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setPreferredSize(new Dimension(100, 35));
        logoutButton.setBackground(new Color(220, 53, 69));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFont(new Font("Arial", Font.BOLD, 14));
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createRaisedBevelBorder());

        // Add hover effect
        logoutButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(200, 35, 51));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                logoutButton.setBackground(new Color(220, 53, 69));
            }
        });

        logoutButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                mainFrame.showPage("Login");
            }
        });

        bottomPanel.add(userStatusLabel, BorderLayout.WEST);
        bottomPanel.add(logoutButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void addNavButton(JPanel panel, String label, String cardName, Color backgroundColor) {
        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(180, 45));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());

        // Add hover effect
        Color originalColor = backgroundColor;
        Color hoverColor = backgroundColor.darker();

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(originalColor);
            }
        });

        button.addActionListener(e -> {
            System.out.println("Navigating to: " + cardName);
            mainFrame.showPage(cardName);
        });

        panel.add(button);
    }

    // Overloaded method for backward compatibility
    private void addNavButton(JPanel panel, String label, String cardName) {
        addNavButton(panel, label, cardName, new Color(70, 130, 180));
    }

    // Getter method to check admin status (useful for debugging)
    public boolean isAdmin() {
        return isAdmin;
    }

    // Method to refresh the panel if admin status changes
    public void refreshPanel(boolean newAdminStatus) {
        if (this.isAdmin != newAdminStatus) {
            this.isAdmin = newAdminStatus;
            // Remove all components and rebuild
            removeAll();
            // Reinitialize with new admin status
            MenuPanel newPanel = new MenuPanel(mainFrame, isAdmin);
            setLayout(newPanel.getLayout());
            for (Component comp : newPanel.getComponents()) {
                add(comp);
            }
            revalidate();
            repaint();
        }
    }
}