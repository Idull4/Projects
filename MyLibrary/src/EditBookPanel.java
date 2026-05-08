import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EditBookPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField titleField, yearField, pagesField, commentsField, coverPathField;
    private JTextArea descArea;
    private JComboBox<String> statusBox, authorBox;
    private JSpinner ratingSpinner;
    private JButton updateButton;
    private int currentBookId = -1;

    public EditBookPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        initializeComponents();
        loadAuthors(); // Load available authors
    }

    private void initializeComponents() {
        // Title
        JLabel titleLabel = new JLabel("Edit Book", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // ID Panel
        JPanel idPanel = new JPanel(new FlowLayout());
        JTextField idField = new JTextField(10);
        JButton loadButton = new JButton("Load Book");
        idPanel.add(new JLabel("Enter Book ID:"));
        idPanel.add(idField);
        idPanel.add(loadButton);

        // Combine title and ID panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(idPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Initialize form fields
        titleField = new JTextField(20);
        authorBox = new JComboBox<>();
        authorBox.setPreferredSize(new Dimension(200, 25));
        yearField = new JTextField(10);
        pagesField = new JTextField(10);
        descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        statusBox = new JComboBox<>(new String[]{"Read", "Unread", "Wishlist"});
        ratingSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 5, 1));
        commentsField = new JTextField(20);
        coverPathField = new JTextField(20);

        // Add components to form
        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Author:"), gbc);
        gbc.gridx = 1;
        formPanel.add(authorBox, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Year:"), gbc);
        gbc.gridx = 1;
        formPanel.add(yearField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Pages:"), gbc);
        gbc.gridx = 1;
        formPanel.add(pagesField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(new JScrollPane(descArea), gbc);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.WEST;
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statusBox, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Rating (0-5):"), gbc);
        gbc.gridx = 1;
        formPanel.add(ratingSpinner, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Comments:"), gbc);
        gbc.gridx = 1;
        formPanel.add(commentsField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Cover Path:"), gbc);
        gbc.gridx = 1;
        formPanel.add(coverPathField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        updateButton = new JButton("Update Book");
        JButton backButton = new JButton("Back");
        updateButton.setEnabled(false); // Disabled until a book is loaded

        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event Listeners
        loadButton.addActionListener(e -> loadBook(idField));
        updateButton.addActionListener(e -> updateBook());
        backButton.addActionListener(e -> mainFrame.showPage("Menu"));
    }

    private void loadAuthors() {
        try (Connection con = DatabaseConnection.connect()) {
            PreparedStatement stmt = con.prepareStatement(
                    "SELECT authorId, CONCAT(name, ' ', surname) AS fullName FROM authors ORDER BY name"
            );
            ResultSet rs = stmt.executeQuery();

            authorBox.removeAllItems();
            while (rs.next()) {
                AuthorItem item = new AuthorItem(rs.getInt("authorId"), rs.getString("fullName"));
                authorBox.addItem(item.toString());
                authorBox.putClientProperty("item_" + (authorBox.getItemCount() - 1), item);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading authors: " + ex.getMessage());
        }
    }

    private void loadBook(JTextField idField) {
        int bookId;
        try {
            bookId = Integer.parseInt(idField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid book ID (number).");
            return;
        }

        try (Connection con = DatabaseConnection.connect()) {
            // Get book details with author information
            String sql = """
                SELECT 
                    b.bookId, b.title, b.year, b.numberOfPages, b.about, 
                    b.`read`, b.rating, b.comments, b.cover, b.authorId,
                    CONCAT(a.name, ' ', a.surname) AS authorName
                FROM books b
                JOIN authors a ON b.authorId = a.authorId
                WHERE b.bookId = ?
                """;

            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setInt(1, bookId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentBookId = bookId;

                // Populate form fields
                titleField.setText(rs.getString("title"));
                yearField.setText(String.valueOf(rs.getInt("year")));
                pagesField.setText(String.valueOf(rs.getInt("numberOfPages")));
                descArea.setText(rs.getString("about"));
                commentsField.setText(rs.getString("comments"));
                coverPathField.setText(rs.getString("cover"));

                // Set status (1=Read, 2=Unread, 3=Wishlist)
                int readStatus = rs.getInt("read");
                if (readStatus == 1) statusBox.setSelectedIndex(0); // Read
                else if (readStatus == 2) statusBox.setSelectedIndex(1); // Unread
                else if (readStatus == 3) statusBox.setSelectedIndex(2); // Wishlist

                ratingSpinner.setValue(rs.getInt("rating"));

                // Set author selection
                int authorId = rs.getInt("authorId");
                for (int i = 0; i < authorBox.getItemCount(); i++) {
                    AuthorItem item = (AuthorItem) authorBox.getClientProperty("item_" + i);
                    if (item != null && item.getId() == authorId) {
                        authorBox.setSelectedIndex(i);
                        break;
                    }
                }

                updateButton.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Book loaded successfully!");

            } else {
                JOptionPane.showMessageDialog(this, "Book with ID " + bookId + " not found.");
                clearForm();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading book: " + ex.getMessage());
            clearForm();
        }
    }

    private void updateBook() {
        if (currentBookId == -1) {
            JOptionPane.showMessageDialog(this, "Please load a book first.");
            return;
        }

        // Validate input
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title cannot be empty.");
            return;
        }

        try {
            Integer.parseInt(yearField.getText().trim());
            Integer.parseInt(pagesField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Year and Pages must be valid numbers.");
            return;
        }

        try (Connection con = DatabaseConnection.connect()) {
            // Get selected author ID
            int selectedIndex = authorBox.getSelectedIndex();
            AuthorItem selectedAuthor = (AuthorItem) authorBox.getClientProperty("item_" + selectedIndex);

            if (selectedAuthor == null) {
                JOptionPane.showMessageDialog(this, "Please select an author.");
                return;
            }

            // Update book using direct SQL (since we don't have the EditBookInfo procedure)
            String sql = """
                UPDATE books SET 
                    title = ?, authorId = ?, year = ?, numberOfPages = ?, 
                    about = ?, `read` = ?, rating = ?, comments = ?, cover = ?
                WHERE bookId = ?
                """;

            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, titleField.getText().trim());
            stmt.setInt(2, selectedAuthor.getId());
            stmt.setInt(3, Integer.parseInt(yearField.getText().trim()));
            stmt.setInt(4, Integer.parseInt(pagesField.getText().trim()));
            stmt.setString(5, descArea.getText().trim());

            // Convert status to read value (Read=1, Unread=2, Wishlist=3)
            int readValue = statusBox.getSelectedIndex() + 1;
            stmt.setInt(6, readValue);

            stmt.setInt(7, (Integer) ratingSpinner.getValue());
            stmt.setString(8, commentsField.getText().trim());
            stmt.setString(9, coverPathField.getText().trim());
            stmt.setInt(10, currentBookId);

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Book updated successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "No changes were made.");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating book: " + ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage());
        }
    }

    private void clearForm() {
        currentBookId = -1;
        titleField.setText("");
        yearField.setText("");
        pagesField.setText("");
        descArea.setText("");
        commentsField.setText("");
        coverPathField.setText("");
        statusBox.setSelectedIndex(0);
        ratingSpinner.setValue(0);
        authorBox.setSelectedIndex(0);
        updateButton.setEnabled(false);
    }

    // Helper class to store author information
    private static class AuthorItem {
        private final int id;
        private final String name;

        public AuthorItem(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}