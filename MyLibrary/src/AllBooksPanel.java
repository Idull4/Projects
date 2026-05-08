import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AllBooksPanel extends JPanel {
    private MainFrame mainFrame;
    private JTable table;
    private DefaultTableModel model;

    public AllBooksPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("All Books in Library", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        model = new DefaultTableModel();
        model.setColumnIdentifiers(new String[]{"ID", "Title", "Author", "Year", "Status", "Rating"});

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(120, 35));
        refreshBtn.addActionListener(e -> loadBooks());

        JButton backBtn = new JButton("Back");
        backBtn.setPreferredSize(new Dimension(120, 35));
        backBtn.addActionListener(e -> mainFrame.showPage("Menu"));

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(refreshBtn);
        bottomPanel.add(backBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        loadBooks();
    }

    private void loadBooks() {
        model.setRowCount(0);
        try (Connection con = DatabaseConnection.connect()) {
            String sql = """
                SELECT b.bookId, b.title, b.year, b.read, b.rating,
                       a.name, a.surname
                FROM books b
                JOIN authors a ON b.authorId = a.authorId
                ORDER BY b.title
            """;

            PreparedStatement pst = con.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                String title = rs.getString("title");
                String author = rs.getString("name") + " " + rs.getString("surname");
                int year = rs.getInt("year");
                String status = switch (rs.getInt("read")) {
                    case 1 -> "✅ Read";
                    case 2 -> "📚 Unread";
                    case 3 -> "🎯 Wishlist";
                    default -> "Unknown";
                };
                int rating = rs.getInt("rating");

                model.addRow(new Object[]{
                        rs.getInt("bookId"), title, author, year, status, rating
                });
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage());
        }
    }
}
