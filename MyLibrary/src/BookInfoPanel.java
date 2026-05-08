import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class BookInfoPanel extends JPanel {
    private static String selectedTitle = null;
    private MainFrame mainFrame;
    private JTextArea infoArea;
    private JLabel coverLabel;

    public BookInfoPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Book Details", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Info area
        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoArea.setBackground(new Color(248, 249, 250));
        infoArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(infoArea);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Cover panel
        JPanel coverPanel = new JPanel(new BorderLayout());
        coverPanel.setBorder(BorderFactory.createTitledBorder("Book Cover"));
        coverPanel.setPreferredSize(new Dimension(200, 300));

        coverLabel = new JLabel("No cover loaded", SwingConstants.CENTER);
        coverLabel.setPreferredSize(new Dimension(180, 270));
        coverLabel.setBorder(BorderFactory.createLoweredBevelBorder());
        coverPanel.add(coverLabel, BorderLayout.CENTER);

        mainPanel.add(coverPanel, BorderLayout.EAST);
        add(mainPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton showCoverButton = new JButton("Load Book Cover");
        JButton refreshButton = new JButton("Refresh Details");
        JButton backButton = new JButton("Back to Search");

        // Style buttons
        styleButton(showCoverButton);
        styleButton(refreshButton);
        styleButton(backButton);

        buttonPanel.add(showCoverButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Action listeners
        showCoverButton.addActionListener(e -> loadCover());
        refreshButton.addActionListener(e -> refreshDetails());
        backButton.addActionListener(e -> mainFrame.showPage("Search"));
    }

    private void styleButton(JButton button) {
        button.setPreferredSize(new Dimension(140, 35));
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    public void refreshDetails() {
        if (selectedTitle == null) {
            infoArea.setText("No book selected.");
            return;
        }

        try (Connection con = DatabaseConnection.connect()) {
            // Get comprehensive book details with author information
            String query = """
                SELECT b.bookId, b.title, b.year, b.numberOfPages, b.cover, 
                       b.about, b.read, b.rating, b.comments, b.releaseDate,
                       a.name as authorName, a.surname as authorSurname, a.website
                FROM books b 
                JOIN authors a ON b.authorId = a.authorId 
                WHERE b.title = ?
            """;

            PreparedStatement pst = con.prepareStatement(query);
            pst.setString(1, selectedTitle);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                StringBuilder info = new StringBuilder();

                // Book basic info
                info.append("═══════════════════════════════════════\n");
                info.append("           BOOK INFORMATION\n");
                info.append("═══════════════════════════════════════\n\n");

                info.append(String.format("📚 Title: %s\n", rs.getString("title")));
                info.append(String.format("✍️  Author: %s %s\n",
                        rs.getString("authorName"), rs.getString("authorSurname")));

                int year = rs.getInt("year");
                if (year > 0) {
                    info.append(String.format("📅 Publication Year: %d\n", year));
                }

                int pages = rs.getInt("numberOfPages");
                if (pages > 0) {
                    info.append(String.format("📖 Pages: %d\n", pages));
                }

                // Read status
                int readStatus = rs.getInt("read");
                String status = getReadStatusText(readStatus);
                info.append(String.format("📊 Status: %s\n", status));

                // Rating
                int rating = rs.getInt("rating");
                if (rating > 0) {
                    info.append(String.format("⭐ Rating: %s (%d/5)\n",
                            "★".repeat(rating) + "☆".repeat(5-rating), rating));
                } else {
                    info.append("⭐ Rating: Not rated\n");
                }

                // Release date for wishlist items
                Date releaseDate = rs.getDate("releaseDate");
                if (releaseDate != null) {
                    info.append(String.format("🗓️  Release Date: %s\n", releaseDate.toString()));
                }

                // Cover info
                String cover = rs.getString("cover");
                if (cover != null && !cover.isEmpty()) {
                    info.append(String.format("🖼️  Cover: %s\n", cover));
                } else {
                    info.append("🖼️  Cover: No cover image\n");
                }

                // Author website
                String website = rs.getString("website");
                if (website != null && !website.isEmpty()) {
                    info.append(String.format("🌐 Author Website: %s\n", website));
                }

                info.append("\n═══════════════════════════════════════\n");
                info.append("              DESCRIPTION\n");
                info.append("═══════════════════════════════════════\n\n");

                // About section
                String about = rs.getString("about");
                if (about != null && !about.isEmpty()) {
                    info.append(about).append("\n\n");
                } else {
                    info.append("No description available.\n\n");
                }



                infoArea.setText(info.toString());
                infoArea.setCaretPosition(0); // Scroll to top
            } else {
                infoArea.setText("Book not found: " + selectedTitle);
            }
        } catch (SQLException e) {
            infoArea.setText("Error retrieving book information:\n" + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getReadStatusText(int readStatus) {
        return switch (readStatus) {
            case 1 -> "✅ Read";
            case 2 -> "📚 Unread";
            case 3 -> "🎯 Wishlist";
            default -> "❓ Unknown";
        };
    }

    private void loadCover() {
        if (selectedTitle == null) {
            JOptionPane.showMessageDialog(this, "No book selected.");
            return;
        }

        try (Connection con = DatabaseConnection.connect()) {
            PreparedStatement pst = con.prepareStatement("SELECT cover FROM books WHERE title = ?");
            pst.setString(1, selectedTitle);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String coverPath = rs.getString("cover");
                if (coverPath != null && !coverPath.isEmpty()) {
                    try {
                        ImageIcon icon = new ImageIcon(coverPath);
                        if (icon.getIconWidth() > 0) {
                            Image scaled = icon.getImage().getScaledInstance(180, 270, Image.SCALE_SMOOTH);
                            coverLabel.setIcon(new ImageIcon(scaled));
                            coverLabel.setText(""); // Clear text when image loads
                        } else {
                            coverLabel.setIcon(null);
                            coverLabel.setText("Image not found");
                            JOptionPane.showMessageDialog(this,
                                    "Cover image file not found: " + coverPath);
                        }
                    } catch (Exception e) {
                        coverLabel.setIcon(null);
                        coverLabel.setText("Failed to load image");
                        JOptionPane.showMessageDialog(this,
                                "Error loading cover image: " + e.getMessage());
                    }
                } else {
                    coverLabel.setIcon(null);
                    coverLabel.setText("No cover available");
                    JOptionPane.showMessageDialog(this, "No cover image available for this book.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Book not found.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setSelectedTitle(String title) {
        selectedTitle = title;
    }

    public static String getSelectedTitle() {
        return selectedTitle;
    }
}