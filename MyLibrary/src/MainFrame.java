import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private boolean currentUserIsAdmin = false;
    private MenuPanel menuPanel;

    public MainFrame() {
        setTitle("MyLibrary");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Initialize all panels
        initializePanels();

        add(mainPanel);

        // Start with login screen
        cardLayout.show(mainPanel, "Login");
    }

    private void initializePanels() {
        // Add login panel first
        mainPanel.add(new LoginPanel(this), "Login");

        // Initialize menu panel with default user role (will be updated after login)
        menuPanel = new MenuPanel(this, currentUserIsAdmin);
        mainPanel.add(menuPanel, "Menu");

        // Add all other panels
        mainPanel.add(new SearchPanel(this), "Search");
        mainPanel.add(new BookInfoPanel(this), "BookInfo");
        mainPanel.add(new AllBooksPanel(this), "AllBooks");
        mainPanel.add(new AddBookPanel(this), "AddBook");
        mainPanel.add(new EditBookPanel(this), "EditBook");
        mainPanel.add(new DeleteBookPanel(this), "DeleteBook");
        mainPanel.add(new RateBookPanel(this), "RateBook");
        mainPanel.add(new WishlistPanel(this), "Wishlist");
        mainPanel.add(new WishlistNotificationsPanel(this), "WishlistNotify");
        mainPanel.add(new UnreadBooksPanel(this), "Unread");
        mainPanel.add(new FavoriteAuthorsPanel(this), "Authors");
        mainPanel.add(new FavoritesPanel(this), "Favorites");
    }

    /**
     * Call this method after successful login to set user privileges
     * @param isAdmin true if the logged-in user is an admin
     */
    public void setUserRole(boolean isAdmin) {
        this.currentUserIsAdmin = isAdmin;

        // Remove old menu panel
        mainPanel.remove(menuPanel);

        // Create new menu panel with correct admin status
        menuPanel = new MenuPanel(this, isAdmin);
        mainPanel.add(menuPanel, "Menu");

        // Refresh the panel
        mainPanel.revalidate();
        mainPanel.repaint();

        System.out.println("User role updated - Admin: " + isAdmin);
    }

    /**
     * Navigate to menu after successful login
     */
    public void showMenuAfterLogin(boolean isAdmin) {
        setUserRole(isAdmin);
        showPage("Menu");
    }

    /**
     * Get the current user's admin status
     * @return true if current user is admin
     */
    public boolean isCurrentUserAdmin() {
        return currentUserIsAdmin;
    }

    /**
     * Show a specific page/panel
     * @param name the name of the panel to show
     */
    public void showPage(String name) {
        // Validate that admin-only pages are only accessible by admins
        if (currentUserIsAdmin && isAdminOnlyPage(name)) {
            JOptionPane.showMessageDialog(this,
                    "Access denied. Admin privileges required.",
                    "Unauthorized Access",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        cardLayout.show(mainPanel, name);
        System.out.println("Navigated to: " + name);
    }

    /**
     * Check if a page requires admin privileges
     * @param pageName the name of the page to check
     * @return true if the page requires admin privileges
     */
    private boolean isAdminOnlyPage(String pageName) {
        return pageName.equals("AddBook") ||
                pageName.equals("EditBook") ||
                pageName.equals("DeleteBook");
    }

    /**
     * Handle logout - reset user role and return to login
     */
    public void logout() {
        currentUserIsAdmin = false;

        // Reset menu panel to non-admin
        mainPanel.remove(menuPanel);
        menuPanel = new MenuPanel(this, false);
        mainPanel.add(menuPanel, "Menu");

        // Show login page
        showPage("Login");

        System.out.println("User logged out");
    }

    /**
     * Get the main panel (for compatibility with existing code)
     * @return the main panel with CardLayout
     */
    public JPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * Get the CardLayout instance
     * @return the CardLayout managing the panels
     */
    public CardLayout getCardLayout() {
        return cardLayout;
    }

    public static void main(String[] args) {
        // Set look and feel for better appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Could not set system look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}