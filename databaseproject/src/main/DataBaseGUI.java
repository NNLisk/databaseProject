import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class DatabaseGUI extends JFrame {
    private JTextField nameField, artistField, genreField, albumField, producerField, writerField, publisherField, lengthField;
    private JTable table;
    private DefaultTableModel tableModel;

    public DatabaseGUI() {
        setTitle("Song Database GUI");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(8, 2));
        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Artist:"));
        artistField = new JTextField();
        inputPanel.add(artistField);

        inputPanel.add(new JLabel("Genre:"));
        genreField = new JTextField();
        inputPanel.add(genreField);

        inputPanel.add(new JLabel("Album:"));
        albumField = new JTextField();
        inputPanel.add(albumField);

        inputPanel.add(new JLabel("Producer:"));
        producerField = new JTextField();
        inputPanel.add(producerField);

        inputPanel.add(new JLabel("Writer:"));
        writerField = new JTextField();
        inputPanel.add(writerField);

        inputPanel.add(new JLabel("Publisher:"));
        publisherField = new JTextField();
        inputPanel.add(publisherField);

        inputPanel.add(new JLabel("Length:"));
        lengthField = new JTextField();
        inputPanel.add(lengthField);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Song");
        JButton refreshButton = new JButton("Refresh Songs");

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Artist", "Genre", "Album", "Producer", "Writer", "Publisher", "Length"}, 0);
        table = new JTable(tableModel);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addSong());
        refreshButton.addActionListener(e -> loadSongs());

        loadSongs();

        setVisible(true);
    }

    private void loadSongs() {
        tableModel.setRowCount(0);
        try (Connection conn = App.cp.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM songs")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("artist_name"),
                        rs.getString("genre"),
                        rs.getString("album"),
                        rs.getString("producer"),
                        rs.getString("writer"),
                        rs.getString("publisher"),
                        rs.getString("song_length")
                });
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading songs: " + e.getMessage());
        }
    }

    private void addSong() {
        try (Connection conn = App.cp.getConnection();
             PreparedStatement ps = conn.prepareStatement("INSERT INTO songs (name, artist_name, genre, album, producer, writer, publisher, song_length) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {

            ps.setString(1, nameField.getText());
            ps.setString(2, artistField.getText());
            ps.setString(3, genreField.getText());
            ps.setString(4, albumField.getText());
            ps.setString(5, producerField.getText());
            ps.setString(6, writerField.getText());
            ps.setString(7, publisherField.getText());
            ps.setString(8, lengthField.getText());

            ps.executeUpdate();
            loadSongs();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error adding song: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DatabaseGUI::new);
    }
}
