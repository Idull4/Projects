import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UnreadBooksPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable booksTable;
    private DefaultTableModel tableModel;

    public UnreadBooksPanel(MainFrame frame) {
        this.mainFrame = frame;
        System.out.println("DEBUG: Initializing UnreadBooksPanel...");

        try {
            initializeComponents();
            System.out.println("DEBUG: Components initialized successfully");

            // Load data after GUI is ready
            SwingUtilities.invokeLater(() -> {
                System.out.println("DEBUG: Loading unread books...");
                loadUnreadBooks();
            });

        } catch (Exception e) {
            System.out.println("DEBUG: Error in constructor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Unread Books", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Table setup
        String[] columnNames = {"Book ID", "Title", "Author", "Year", "Pages"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        booksTable = new JTable(tableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setPreferredSize(new Dimension(700, 400));
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton refreshButton = new JButton("Refresh");
        JButton backButton = new JButton("Back");
        JButton markAsReadButton = new JButton("Mark as Read");

        refreshButton.addActionListener(e -> loadUnreadBooks());
        backButton.addActionListener(e -> mainFrame.showPage("Menu"));
        markAsReadButton.addActionListener(e -> markSelectedAsRead());

        buttonPanel.add(refreshButton);
        buttonPanel.add(markAsReadButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadUnreadBooks() {
        System.out.println("DEBUG: Starting loadUnreadBooks method...");

        try (Connection con = DatabaseConnection.connect()) {
            System.out.println("DEBUG: Database connection established");

            // Clear existing data
            tableModel.setRowCount(0);
            System.out.println("DEBUG: Table cleared");

            // Use the stored procedure
            try {
                System.out.println("DEBUG: Calling GetUnreadBooks stored procedure...");
                CallableStatement stmt = con.prepareCall("{CALL GetUnreadBooks()}");
                ResultSet rs = stmt.executeQuery();

                int count = 0;
                while (rs.next()) {
                    // Get additional book details for display
                    String fullAuthor = getFullAuthorName(con, rs.getInt("bookId"));
                    int year = getBookYear(con, rs.getInt("bookId"));
                    int pages = getBookPages(con, rs.getInt("bookId"));

                    Object[] row = {
                            rs.getInt("bookId"),
                            rs.getString("bookName"),
                            fullAuthor != null ? fullAuthor : rs.getString("author"),
                            year > 0 ? year : "N/A",
                            pages > 0 ? pages : "N/A"
                    };
                    tableModel.addRow(row);
                    count++;
                }

                System.out.println("DEBUG: Stored procedure worked! Found " + count + " unread books");

                // Update title with count
                updateTitleWithCount(count);

            } catch (SQLException procEx) {
                System.out.println("DEBUG: Stored procedure failed: " + procEx.getMessage());
                procEx.printStackTrace();

                // Fallback to direct SQL query
                loadUnreadBooksDirectly(con);
            }

        } catch (SQLException ex) {
            System.out.println("DEBUG: Database error: " + ex.getMessage());
            ex.printStackTrace();

            // Show user-friendly error message
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                        "Error loading unread books: " + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            });
        }
    }

    private void loadUnreadBooksDirectly(Connection con) throws SQLException {
        System.out.println("DEBUG: Using direct SQL query as fallback...");

        String sql = """
            SELECT 
                b.bookId,
                b.title AS bookName,
                CONCAT(a.name, ' ', a.surname) AS author,
                b.year,
                b.numberOfPages
            FROM books b
            JOIN authors a ON b.authorId = a.authorId
            WHERE b.`read` = 2
            ORDER BY b.title
            """;

        PreparedStatement stmt = con.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        int count = 0;
        while (rs.next()) {
            Object[] row = {
                    rs.getInt("bookId"),
                    rs.getString("bookName"),
                    rs.getString("author"),
                    rs.getInt("year") > 0 ? rs.getInt("year") : "N/A",
                    rs.getInt("numberOfPages") > 0 ? rs.getInt("numberOfPages") : "N/A"
            };
            tableModel.addRow(row);
            count++;
        }

        System.out.println("DEBUG: Direct query worked! Found " + count + " unread books");
        updateTitleWithCount(count);
    }

    private String getFullAuthorName(Connection con, int bookId) {
        try {
            String sql = "SELECT CONCAT(a.name, ' ', a.surname) AS fullName FROM authors a " +
                    "JOIN books b ON a.authorId = b.authorId WHERE b.bookId = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getString("fullName");
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Error getting author name: " + e.getMessage());
        }
        return null;
    }

    private int getBookYear(Connection con, int bookId) {
        try {
            String sql = "SELECT year FROM books WHERE bookId = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("year");
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Error getting book year: " + e.getMessage());
        }
        return 0;
    }

    private int getBookPages(Connection con, int bookId) {
        try {
            String sql = "SELECT numberOfPages FROM books WHERE bookId = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("numberOfPages");
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Error getting book pages: " + e.getMessage());
        }
        return 0;
    }

    private void updateTitleWithCount(int count) {
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText("Unread Books (" + count + " books)");
                break;
            }
        }
    }

    private void markSelectedAsRead() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a book to mark as read.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int bookId = (Integer) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark '" + bookTitle + "' as read?",
                "Confirm Action",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection con = DatabaseConnection.connect()) {
                PreparedStatement stmt = con.prepareStatement(
                        "UPDATE books SET `read` = 1 WHERE bookId = ?"
                );
                stmt.setInt(1, bookId);
                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Book marked as read successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadUnreadBooks(); // Refresh the list
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Failed to update book status.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}