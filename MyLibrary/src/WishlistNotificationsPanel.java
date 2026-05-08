import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class WishlistNotificationsPanel extends JPanel {
    private MainFrame mainFrame;

    public WishlistNotificationsPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Wishlist Notifications", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        add(titleLabel, BorderLayout.NORTH);

        JTextArea messageArea = new JTextArea();
        messageArea.setEditable(false);
        messageArea.setFont(new Font("Arial", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(messageArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loadButton = new JButton("Load Wishlist Books");
        JButton upcomingButton = new JButton("Check Upcoming Releases");
        JButton backButton = new JButton("Back");

        buttonPanel.add(loadButton);
        buttonPanel.add(upcomingButton);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load all wishlist books
        loadButton.addActionListener(e -> {
            try (Connection con = DatabaseConnection.connect()) {
                if (con == null) {
                    JOptionPane.showMessageDialog(this, "Failed to connect to database.",
                            "Connection Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String query = "SELECT b.title, a.name, a.surname, b.releaseDate, b.about " +
                        "FROM books b " +
                        "INNER JOIN authors a ON b.authorId = a.authorId " +
                        "WHERE b.`read` = 3 " +
                        "ORDER BY b.releaseDate";

                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                StringBuilder sb = new StringBuilder("Your Wishlist Books:\n");
                sb.append("=".repeat(50)).append("\n\n");

                boolean found = false;
                while (rs.next()) {
                    found = true;
                    sb.append("📚 Title: ").append(rs.getString("title")).append("\n");
                    sb.append("👤 Author: ").append(rs.getString("name"))
                            .append(" ").append(rs.getString("surname")).append("\n");

                    Date releaseDate = rs.getDate("releaseDate");
                    if (releaseDate != null) {
                        sb.append("📅 Release Date: ").append(releaseDate).append("\n");
                    } else {
                        sb.append("📅 Release Date: Not specified\n");
                    }

                    String about = rs.getString("about");
                    if (about != null && !about.trim().isEmpty()) {
                        sb.append("📖 About: ").append(about).append("\n");
                    }
                    sb.append("\n").append("-".repeat(40)).append("\n\n");
                }

                if (found) {
                    messageArea.setText(sb.toString());
                } else {
                    messageArea.setText("No books in your wishlist.\n\n" +
                            "Add books to your wishlist to see them here!");
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Check upcoming releases (next 7 days)
        upcomingButton.addActionListener(e -> {
            try (Connection con = DatabaseConnection.connect()) {
                if (con == null) {
                    JOptionPane.showMessageDialog(this, "Failed to connect to database.",
                            "Connection Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Use the function from your SQL schema
                String query = "SELECT getUpcomingWishlistCount() as upcomingCount";
                PreparedStatement stmt = con.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();

                StringBuilder sb = new StringBuilder("Upcoming Wishlist Releases (Next 7 Days):\n");
                sb.append("=".repeat(50)).append("\n\n");

                if (rs.next()) {
                    int upcomingCount = rs.getInt("upcomingCount");

                    if (upcomingCount > 0) {
                        sb.append("🎉 You have ").append(upcomingCount)
                                .append(" book(s) releasing in the next 7 days!\n\n");

                        // Get details of upcoming books
                        String detailQuery = "SELECT b.title, a.name, a.surname, b.releaseDate " +
                                "FROM books b " +
                                "INNER JOIN authors a ON b.authorId = a.authorId " +
                                "WHERE b.`read` = 3 AND b.releaseDate BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 7 DAY) " +
                                "ORDER BY b.releaseDate";

                        PreparedStatement detailStmt = con.prepareStatement(detailQuery);
                        ResultSet detailRs = detailStmt.executeQuery();

                        while (detailRs.next()) {
                            sb.append("📚 ").append(detailRs.getString("title")).append("\n");
                            sb.append("👤 By: ").append(detailRs.getString("name"))
                                    .append(" ").append(detailRs.getString("surname")).append("\n");
                            sb.append("📅 Release: ").append(detailRs.getDate("releaseDate")).append("\n\n");
                        }
                    } else {
                        sb.append("No upcoming releases in the next 7 days.\n\n");
                        sb.append("Check back later for new releases!");
                    }
                }

                messageArea.setText(sb.toString());

            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Database error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            if (mainFrame != null) {
                mainFrame.showPage("Menu");
            }
        });
    }
}