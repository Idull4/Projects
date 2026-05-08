import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class FavoriteAuthorsPanel extends JPanel {
    private JTable table;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;

    public FavoriteAuthorsPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        JLabel header = new JLabel("Favorite Authors (3+ Books)", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 20));
        add(header, BorderLayout.NORTH);

        // Updated table model with more informative columns
        tableModel = new DefaultTableModel(new String[]{"Author Name", "Book Count"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(25);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton loadButton = new JButton("Load Authors");
        JButton backButton = new JButton("Back");
        buttonPanel.add(loadButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadButton.addActionListener(e -> loadAuthors());
        backButton.addActionListener(e -> mainFrame.showPage("Menu"));

        // Load data automatically when panel is created
        SwingUtilities.invokeLater(this::loadAuthors);
    }

    private void loadAuthors() {
        tableModel.setRowCount(0);

        try (Connection con = DatabaseConnection.connect()) {
            System.out.println("DEBUG: Loading favorite authors...");

            // Method 1: Try stored procedure first
            try {
                System.out.println("DEBUG: Trying stored procedure...");
                CallableStatement stmt = con.prepareCall("{CALL GetFavoriteAuthors()}");
                ResultSet rs = stmt.executeQuery();

                int count = 0;
                while (rs.next()) {
                    String authorName = rs.getString("author_name");
                    int bookCount = rs.getInt("book_count");
                    tableModel.addRow(new Object[]{authorName, bookCount});
                    count++;
                }

                System.out.println("DEBUG: Stored procedure worked! Found " + count + " favorite authors");
                updateHeaderWithCount(count);
                return; // Success with stored procedure

            } catch (SQLException procEx) {
                System.out.println("DEBUG: Stored procedure failed: " + procEx.getMessage());
                // Continue to direct query
            }

            // Method 2: Direct SQL query
            System.out.println("DEBUG: Trying direct SQL query...");
            String query = """
                SELECT a.name AS author_name, COUNT(b.bookId) AS book_count
                FROM authors a
                JOIN books b ON a.authorId = b.authorId
                GROUP BY a.authorId, a.name
                HAVING COUNT(b.bookId) >= 3
                ORDER BY book_count DESC, a.name ASC
                """;

            PreparedStatement pst = con.prepareStatement(query);
            ResultSet rs = pst.executeQuery();

            int count = 0;
            while (rs.next()) {
                String authorName = rs.getString("author_name");
                int bookCount = rs.getInt("book_count");
                tableModel.addRow(new Object[]{authorName, bookCount});
                count++;
            }

            System.out.println("DEBUG: Direct query worked! Found " + count + " favorite authors");
            updateHeaderWithCount(count);

            if (count == 0) {
                JOptionPane.showMessageDialog(this,
                        "No favorite authors found.\n(Authors need 3 or more books to be considered favorites)",
                        "No Results",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (SQLException e) {
            System.out.println("DEBUG: Database error: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading favorite authors: " + e.getMessage() +
                            "\n\nPlease check:\n" +
                            "1. Database connection is working\n" +
                            "2. Authors and books tables exist\n" +
                            "3. authorId foreign key relationship is set up correctly",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateHeaderWithCount(int count) {
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JLabel) {
                ((JLabel) comp).setText("Favorite Authors (3+ Books) - " + count + " found");
                break;
            }
        }
    }
}