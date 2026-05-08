import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DeleteBookPanel extends JPanel {
    private MainFrame mainFrame;
    private JTextField idField;

    public DeleteBookPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("Delete Book", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Form panel with better layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(10, 10, 10, 10);

        // Book ID input
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Enter Book ID:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        idField = new JTextField(15);
        formPanel.add(idField, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton deleteButton = new JButton("Delete");
        JButton backButton = new JButton("Back");

        deleteButton.setPreferredSize(new Dimension(100, 30));
        backButton.setPreferredSize(new Dimension(100, 30));

        buttonPanel.add(deleteButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Delete button action listener
        deleteButton.addActionListener(e -> deleteBook());

        // Back button action listener
        backButton.addActionListener(e -> {
            clearFields();
            mainFrame.showPage("Menu");
        });

        // Enter key support for the text field
        idField.addActionListener(e -> deleteBook());
    }

    private void deleteBook() {
        String idText = idField.getText().trim();

        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a book ID.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int bookId = Integer.parseInt(idText);

            if (bookId <= 0) {
                JOptionPane.showMessageDialog(this,
                        "Book ID must be a positive number.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Check if book exists first
            if (!bookExists(bookId)) {
                JOptionPane.showMessageDialog(this,
                        "Book with ID " + bookId + " does not exist.",
                        "Book Not Found",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete book ID " + bookId + "?",
                    "Confirm Deletion",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                performDelete(bookId);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a valid numeric book ID.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean bookExists(int bookId) {
        try (Connection connection = DatabaseConnection.connect()) {
            String query = "SELECT COUNT(*) FROM books WHERE bookId = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, bookId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException ex) {
            System.err.println("Error checking if book exists: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

    private void performDelete(int bookId) {
        try (Connection connection = DatabaseConnection.connect()) {
            // Try using stored procedure first
            try (CallableStatement callableStatement = connection.prepareCall("{CALL DeleteBook(?)}")) {
                callableStatement.setInt(1, bookId);
                int rowsAffected = callableStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this,
                            "Book deleted successfully.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    clearFields();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "No book was deleted. Please check the book ID.",
                            "Delete Failed",
                            JOptionPane.WARNING_MESSAGE);
                }

            } catch (SQLException procEx) {
                // If stored procedure fails, try direct SQL delete
                System.out.println("Stored procedure failed, trying direct SQL: " + procEx.getMessage());

                String deleteQuery = "DELETE FROM books WHERE bookId = ?";
                try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
                    preparedStatement.setInt(1, bookId);
                    int rowsAffected = preparedStatement.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this,
                                "Book deleted successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "No book was deleted. Please check the book ID.",
                                "Delete Failed",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            }

        } catch (SQLException ex) {
            System.err.println("Database error during delete: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error deleting book: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        idField.setText("");
    }
}