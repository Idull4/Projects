import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FavoritesPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;

    public FavoritesPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Favorite Books (Rated 4 or 5)", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        // Updated table model with book title, author, and rating columns
        tableModel = new DefaultTableModel(new String[]{"Book Title", "Author", "Rating"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);

        // Set column widths for better display
        table.getColumnModel().getColumn(0).setPreferredWidth(300); // Book Title
        table.getColumnModel().getColumn(1).setPreferredWidth(200); // Author
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Rating

        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Load Favorites");
        JButton backButton = new JButton("Back");
        buttonPanel.add(loadButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadFavorites());
        backButton.addActionListener(e -> mainFrame.showPage("Menu"));

        // Load data automatically when panel is created
        SwingUtilities.invokeLater(this::loadFavorites);
    }

    private void loadFavorites() {
        tableModel.setRowCount(0);

        try (Connection con = DatabaseConnection.connect()) {
            System.out.println("DEBUG: Loading favorite books...");

            // Method 1: Try stored procedure first
            try {
                System.out.println("DEBUG: Trying stored procedure...");
                CallableStatement stmt = con.prepareCall("{CALL GetFavoriteBooks()}");
                ResultSet rs = stmt.executeQuery();

                int count = 0;
                while (rs.next()) {
                    String bookTitle = rs.getString("book_title");
                    String authorName = rs.getString("author_name");
                    int rating = rs.getInt("rating");
                    tableModel.addRow(new Object[]{bookTitle, authorName, rating});
                    count++;
                }

                System.out.println("DEBUG: Stored procedure worked! Found " + count + " favorite books");
                updateHeaderWithCount(count);
                return; // Success with stored procedure

            } catch (SQLException procEx) {
                System.out.println("DEBUG: Stored procedure failed: " + procEx.getMessage());
                // Continue to direct query
            }

            // Method 2: Direct SQL query
            System.out.println("DEBUG: Trying direct SQL query...");
            String query = """
                SELECT 
                    b.title AS book_title,
                    a.name AS author_name,
                    b.rating
                FROM books b
                JOIN authors a ON b.authorId = a.authorId
                WHERE b.rating >= 4 AND b.rating <= 5
                ORDER BY b.rating DESC, b.title ASC
                """;

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            int count = 0;
            while (rs.next()) {
                String bookTitle = rs.getString("book_title");
                String authorName = rs.getString("author_name");
                int rating = rs.getInt("rating");
                tableModel.addRow(new Object[]{bookTitle, authorName, rating});
                count++;
            }

            System.out.println("DEBUG: Direct query worked! Found " + count + " favorite books");
            updateHeaderWithCount(count);

            if (count == 0) {
                JOptionPane.showMessageDialog(this,
                        "No favorite books found.\n(Books need rating of 4 or 5 to be considered favorites)",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            System.out.println("DEBUG: Database error: " + e.getMessage());
            e.printStackTrace();

            // Try alternative column name for authors table
            try (Connection con = DatabaseConnection.connect()) {
                System.out.println("DEBUG: Trying with 'authorName' column...");
                String altQuery = """
                    SELECT 
                        b.title AS book_title,
                        a.authorName AS author_name,
                        b.rating
                    FROM books b
                    JOIN authors a ON b.authorId = a.authorId
                    WHERE b.rating >= 4 AND b.rating <= 5
                    ORDER BY b.rating DESC, b.title ASC
                    """;

                PreparedStatement pst = con.prepareStatement(altQuery);
                ResultSet rs = pst.executeQuery();

                int count = 0;
                while (rs.next()) {
                    String bookTitle = rs.getString("book_title");
                    String authorName = rs.getString("author_name");
                    int rating = rs.getInt("rating");
                    tableModel.addRow(new Object[]{bookTitle, authorName, rating});
                    count++;
                }

                System.out.println("DEBUG: Alternative query worked! Found " + count + " favorite books");
                updateHeaderWithCount(count);

            } catch (SQLException altEx) {
                altEx.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Error loading favorite books: " + altEx.getMessage() +
                                "\n\nPlease check:\n" +
                                "1. Database connection is working\n" +
                                "2. Authors and books tables exist\n" +
                                "3. Books table has 'rating' column\n" +
                                "4. Authors table has 'name' or 'authorName' column\n" +
                                "5. authorId foreign key relationship is set up correctly",
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateHeaderWithCount(int count) {
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText("Favorite Books (Rated 4 or 5) - " + count + " found");
                break;
            }
        }
    }
}