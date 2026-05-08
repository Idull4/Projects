import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddBookPanel extends JPanel {
    private MainFrame mainFrame;

    public AddBookPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Add New Book", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Create form panel with proper spacing
        JPanel formPanel = new JPanel(new GridLayout(10, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField titleField = new JTextField();
        JTextField authorIdField = new JTextField();
        JTextField yearField = new JTextField();
        JTextField pagesField = new JTextField();
        JTextArea descArea = new JTextArea(3, 20);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);

        // Status options: 1 = Read, 2 = Currently Reading, 3 = Wishlist
        JComboBox<String> statusBox = new JComboBox<>(new String[]{
                "Read",
                "Currently Reading",
                "Wishlist"
        });

        JSpinner ratingSpinner = new JSpinner(new SpinnerNumberModel(1, 0, 5, 1));
        JTextField commentsField = new JTextField();
        JTextField coverPathField = new JTextField();
        JTextField releaseDateField = new JTextField();

        // Add placeholder text hint
        releaseDateField.setToolTipText("Enter date in format: YYYY-MM-DD, DD/MM/YYYY, MM/DD/YYYY, or DD-MM-YYYY");
        authorIdField.setToolTipText("Enter the numeric Author ID");

        // Add components to form
        formPanel.add(new JLabel("Title:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Author ID:"));
        formPanel.add(authorIdField);
        formPanel.add(new JLabel("Year:"));
        formPanel.add(yearField);
        formPanel.add(new JLabel("Pages:"));
        formPanel.add(pagesField);
        formPanel.add(new JLabel("Description:"));
        formPanel.add(new JScrollPane(descArea));
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusBox);
        formPanel.add(new JLabel("Rating (0-5):"));
        formPanel.add(ratingSpinner);
        formPanel.add(new JLabel("Comments:"));
        formPanel.add(commentsField);
        formPanel.add(new JLabel("Cover Path:"));
        formPanel.add(coverPathField);
        formPanel.add(new JLabel("Release Date:"));
        formPanel.add(releaseDateField);

        add(formPanel, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton addButton = new JButton("Add Book");
        JButton clearButton = new JButton("Clear All");
        JButton backButton = new JButton("Back");

        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add button action
        addButton.addActionListener(e -> addBook(
                titleField, authorIdField, yearField, pagesField,
                descArea, statusBox, ratingSpinner, commentsField, coverPathField, releaseDateField
        ));

        // Clear button action
        clearButton.addActionListener(e -> clearAllFields(
                titleField, authorIdField, yearField, pagesField,
                descArea, statusBox, ratingSpinner, commentsField, coverPathField, releaseDateField
        ));

        backButton.addActionListener(e -> mainFrame.showPage("Menu"));
    }

    private void addBook(JTextField titleField, JTextField authorIdField, JTextField yearField,
                         JTextField pagesField, JTextArea descArea, JComboBox<String> statusBox,
                         JSpinner ratingSpinner, JTextField commentsField, JTextField coverPathField,
                         JTextField releaseDateField) {

        // Validate required fields
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a book title.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (authorIdField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an author ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection con = DatabaseConnection.connect()) {
            // Parse and validate author ID
            int authorId = parseIntSafely(authorIdField.getText().trim(), "Author ID");

            // Insert book directly
            String insertQuery = """
                INSERT INTO books (title, authorId, year, numberOfPages, about, `read`, rating, comments, cover, releaseDate) 
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            PreparedStatement pst = con.prepareStatement(insertQuery);
            pst.setString(1, titleField.getText().trim());
            pst.setInt(2, authorId);
            pst.setInt(3, parseIntSafely(yearField.getText().trim(), "Year"));
            pst.setInt(4, parseIntSafely(pagesField.getText().trim(), "Pages"));
            pst.setString(5, descArea.getText().trim());

            // Map status: 1=Read, 2=Currently Reading, 3=Wishlist
            int statusValue = statusBox.getSelectedIndex() + 1;
            pst.setInt(6, statusValue);
            pst.setInt(7, (Integer) ratingSpinner.getValue());
            pst.setString(8, commentsField.getText().trim());
            pst.setString(9, coverPathField.getText().trim());

            // Handle release date
            if (releaseDateField.getText().trim().isEmpty()) {
                pst.setNull(10, java.sql.Types.DATE);
            } else {
                java.sql.Date releaseDate = parseDate(releaseDateField.getText().trim());
                pst.setDate(10, releaseDate);
            }

            int rowsAffected = pst.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearAllFields(titleField, authorIdField, yearField, pagesField, descArea, statusBox, ratingSpinner, commentsField, coverPathField, releaseDateField);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add book. No rows were affected.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException numEx) {
            JOptionPane.showMessageDialog(this, numEx.getMessage(), "Input Error", JOptionPane.WARNING_MESSAGE);
        } catch (IllegalArgumentException dateEx) {
            JOptionPane.showMessageDialog(this, dateEx.getMessage(), "Date Error", JOptionPane.WARNING_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                    "Error adding book: " + ex.getMessage() +
                            "\n\nPlease check:\n" +
                            "1. Database connection is working\n" +
                            "2. Author ID exists in the authors table\n" +
                            "3. All required fields are filled correctly\n" +
                            "4. Year and Pages are valid numbers\n" +
                            "5. Release date is in correct format",
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private java.sql.Date parseDate(String dateStr) throws IllegalArgumentException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }

        // Try different date formats
        String[] formats = {"yyyy-MM-dd", "dd/MM/yyyy", "MM/dd/yyyy", "dd-MM-yyyy"};

        for (String format : formats) {
            try {
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat(format);
                sdf.setLenient(false); // Strict parsing
                java.util.Date utilDate = sdf.parse(dateStr.trim());
                return new java.sql.Date(utilDate.getTime());
            } catch (java.text.ParseException e) {
                // Try next format
            }
        }

        throw new IllegalArgumentException("Invalid date format. Please use: YYYY-MM-DD, DD/MM/YYYY, MM/DD/YYYY, or DD-MM-YYYY. You entered: " + dateStr);
    }

    private int parseIntSafely(String value, String fieldName) throws NumberFormatException {
        if (value.isEmpty()) {
            throw new NumberFormatException(fieldName + " cannot be empty.");
        }
        try {
            int result = Integer.parseInt(value);
            if (result <= 0) {
                throw new NumberFormatException(fieldName + " must be a positive number.");
            }
            return result;
        } catch (NumberFormatException e) {
            throw new NumberFormatException(fieldName + " must be a valid positive number. You entered: " + value);
        }
    }

    private void clearAllFields(JTextField titleField, JTextField authorIdField, JTextField yearField,
                                JTextField pagesField, JTextArea descArea, JComboBox<String> statusBox,
                                JSpinner ratingSpinner, JTextField commentsField, JTextField coverPathField,
                                JTextField releaseDateField) {
        titleField.setText("");
        authorIdField.setText("");
        yearField.setText("");
        pagesField.setText("");
        descArea.setText("");
        statusBox.setSelectedIndex(0);
        ratingSpinner.setValue(1);
        commentsField.setText("");
        coverPathField.setText("");
        releaseDateField.setText("");
    }
}