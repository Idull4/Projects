import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class SearchPanel extends JPanel {
    private JTextField searchField;
    private JTable resultsTable;
    private DefaultTableModel tableModel;
    private MainFrame mainFrame;

    public SearchPanel(MainFrame frame) {
        this.mainFrame = frame;
        setLayout(new BorderLayout());

        // Üst arama barı
        JPanel topPanel = new JPanel();
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // Orta tablo
        tableModel = new DefaultTableModel(new String[]{"Title"}, 0);
        resultsTable = new JTable(tableModel);
        add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // Alt butonlar
        JPanel bottomPanel = new JPanel();
        JButton infoButton = new JButton("Show Book Info");
        JButton backButton = new JButton("Back");
        bottomPanel.add(infoButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Search fonksiyonunu çağır
        searchButton.addActionListener(e -> performSearch());

        // Kitap bilgisi göster
        infoButton.addActionListener(e -> {
            int row = resultsTable.getSelectedRow();
            if (row >= 0) {
                String selectedTitle = (String) tableModel.getValueAt(row, 0);
                BookInfoPanel.setSelectedTitle(selectedTitle); // static setter ile gönder
                mainFrame.showPage("BookInfo");
            } else {
                JOptionPane.showMessageDialog(this, "Please select a book.");
            }
        });

        // Geri dön
        backButton.addActionListener(e -> mainFrame.showPage("Menu"));
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) return;

        tableModel.setRowCount(0); // önce tabloyu temizle

        try (Connection con = DatabaseConnection.connect()) {
            // ✅ SearchBooks fonksiyonu kullanılıyor
            PreparedStatement pst = con.prepareStatement("SELECT SearchBooks(?) AS titles");
            pst.setString(1, keyword);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                String results = rs.getString("titles");
                if (results != null) {
                    for (String title : results.split("; ")) {
                        tableModel.addRow(new Object[]{title});
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "No matching books found.");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error during search.");
        }
    }
}
