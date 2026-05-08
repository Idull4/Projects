import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RateBookPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable booksTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JSpinner ratingSpinner;
    private TableRowSorter<DefaultTableModel> sorter;

    public RateBookPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        initializeComponents();
        loadBooks();
    }

    private void initializeComponents() {
        // Title
        JLabel titleLabel = new JLabel("Rate a Book", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Books:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);

        // Create main panel to hold search and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(searchPanel, BorderLayout.NORTH);

        // Books Table
        String[] columnNames = {"ID", "Title", "Author", "Year", "Pages", "Current Rating", "Read Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };

        booksTable = new JTable(tableModel);
        booksTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        booksTable.getTableHeader().setReorderingAllowed(false);

        // Set column widths
        booksTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        booksTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        booksTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Author
        booksTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Year
        booksTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Pages
        booksTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Rating
        booksTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Status

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(booksTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel for Rating Controls
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        bottomPanel.add(new JLabel("New Rating (1-5):"));
        ratingSpinner = new JSpinner(new SpinnerNumberModel(5, 1, 5, 1));
        bottomPanel.add(ratingSpinner);

        JButton rateButton = new JButton("Rate Selected Book");
        JButton refreshButton = new JButton("Refresh List");
        JButton backButton = new JButton("Back to Menu");

        bottomPanel.add(rateButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(backButton);

        add(bottomPanel, BorderLayout.SOUTH);

        // Setup table sorting and filtering
        sorter = new TableRowSorter<>(tableModel);
        booksTable.setRowSorter(sorter);

        // Event Listeners
        setupEventListeners(rateButton, refreshButton, backButton);
    }

    private void setupEventListeners(JButton rateButton, JButton refreshButton, JButton backButton) {
        // Search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTable(); }
        });

        // Rate button
        rateButton.addActionListener(e -> rateSelectedBook());

        // Refresh button
        refreshButton.addActionListener(e -> {
            loadBooks();
            JOptionPane.showMessageDialog(this, "Book list refreshed!");
        });

        // Back button
        backButton.addActionListener(e -> mainFrame.showPage("Menu"));

        // Double-click to rate
        booksTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    rateSelectedBook();
                }
            }
        });
    }

    private void filterTable() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            // Search in Title, Author columns
            RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter(
                    "(?i)" + searchText, 1, 2); // Case insensitive search in columns 1 and 2
            sorter.setRowFilter(filter);
        }
    }

    private void loadBooks() {
        // Clear existing data
        tableModel.setRowCount(0);

        String query = """
            SELECT b.bookId, b.title, CONCAT(a.name, ' ', a.surname) AS author, 
                   b.year, b.numberOfPages, b.rating,
                   CASE b.read 
                       WHEN 1 THEN 'Read' 
                       WHEN 2 THEN 'Unread' 
                       WHEN 3 THEN 'Wishlist' 
                       ELSE 'Unknown' 
                   END AS readStatus
            FROM books b
            JOIN authors a ON b.authorId = a.authorId
            ORDER BY b.title
            """;

        try (Connection con = DatabaseConnection.connect();
             PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("bookId"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("year"),
                        rs.getInt("numberOfPages"),
                        rs.getInt("rating") == 0 ? "Not Rated" : rs.getInt("rating") + "/5",
                        rs.getString("readStatus")
                };
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading books: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rateSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book to rate!",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Get the actual row index (accounting for sorting/filtering)
        int modelRow = booksTable.convertRowIndexToModel(selectedRow);
        int bookId = (Integer) tableModel.getValueAt(modelRow, 0);
        String bookTitle = (String) tableModel.getValueAt(modelRow, 1);
        int newRating = (Integer) ratingSpinner.getValue();

        // Confirm rating
        int confirm = JOptionPane.showConfirmDialog(this,
                "Rate \"" + bookTitle + "\" with " + newRating + " stars?",
                "Confirm Rating",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try (Connection con = DatabaseConnection.connect()) {
            CallableStatement stmt = con.prepareCall("{CALL RateBook(?, ?)}");
            stmt.setInt(1, bookId);
            stmt.setInt(2, newRating);
            stmt.executeUpdate();

            // Update the table display
            tableModel.setValueAt(newRating + "/5", modelRow, 5);

            JOptionPane.showMessageDialog(this,
                    "Successfully rated \"" + bookTitle + "\" with " + newRating + " stars!",
                    "Rating Submitted",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error submitting rating: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}