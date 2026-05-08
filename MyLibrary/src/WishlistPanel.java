import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class WishlistPanel extends JPanel {
    private MainFrame mainFrame;

    public WishlistPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Add Book to Wishlist", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel();
        JTextField bookIdField = new JTextField(10);
        JButton addButton = new JButton("Add to Wishlist");
        JButton backButton = new JButton("Back");

        formPanel.add(new JLabel("Book ID:"));
        formPanel.add(bookIdField);
        formPanel.add(addButton);
        formPanel.add(backButton);

        add(formPanel, BorderLayout.CENTER);

        addButton.addActionListener(e -> {
            try {
                int bookId = Integer.parseInt(bookIdField.getText().trim());

                try (Connection con = DatabaseConnection.connect()) {
                    // FIXED: Use fully qualified column name to handle reserved keyword 'read'
                    PreparedStatement stmt = con.prepareStatement(
                            "UPDATE books SET books.`read` = 3 WHERE books.bookId = ?"
                    );
                    stmt.setInt(1, bookId);
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        JOptionPane.showMessageDialog(this, "Book added to wishlist.");
                        bookIdField.setText(""); // Clear the field after success
                    } else {
                        JOptionPane.showMessageDialog(this, "Book ID not found!");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Book ID! Please enter a valid number.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error adding to wishlist: " + ex.getMessage());
            }
        });

        backButton.addActionListener(e -> mainFrame.showPage("Menu"));
    }
}