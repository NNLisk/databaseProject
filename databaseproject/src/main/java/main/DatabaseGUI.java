package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

/* AI note: AI in this project is used for implementing some of the gui
 * and creating the exe files. Main database functionality is with few
 * exceptions self made
 */

public class DatabaseGUI extends JFrame {
    private JTextField nameField, artistField, genreField, albumField, producerField, writerField, publisherField,
            lengthField;
    private JTextField artistNameField, emailfield, dobfield;
    private JTextField songIDField;
    private JTable table;
    private DefaultTableModel tableModel;

    public DatabaseGUI() {
        /* makes the panel and input fields */
        setTitle("Song Database GUI");
        setSize(1400, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        JPanel addSongspanel = new JPanel(new BorderLayout());
        JPanel artistPanel = new JPanel(new BorderLayout());
        JPanel deleteSongPanel = new JPanel(new BorderLayout());

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

        JPanel artistInputPanel = new JPanel(new GridLayout(3, 2));
        artistInputPanel.add(new JLabel("Artist name:"));
        artistNameField = new JTextField();
        artistInputPanel.add(artistNameField);

        artistInputPanel.add(new JLabel("email"));
        emailfield = new JTextField();
        artistInputPanel.add(emailfield);

        artistInputPanel.add(new JLabel("Date of Birth:"));
        dobfield = new JTextField();
        artistInputPanel.add(dobfield);

        JPanel deleteSongInputs = new JPanel(new GridLayout(1, 2));
        deleteSongInputs.add(new JLabel("SongID: "));
        songIDField = new JTextField();
        deleteSongInputs.add(songIDField);

        artistPanel.add(artistInputPanel, BorderLayout.NORTH);
        addSongspanel.add(inputPanel, BorderLayout.NORTH);
        deleteSongPanel.add(deleteSongInputs, BorderLayout.NORTH);

        tabs.addTab("Register Songs", addSongspanel);
        tabs.addTab("Register Artist", artistPanel);
        tabs.addTab("Delete Songs", deleteSongPanel);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Song");
        JButton refreshButton = new JButton("Refresh Songs");
        JPanel artistButtonPanel = new JPanel();
        JButton addArtistButton = new JButton("Add artist");
        JPanel deleteButtonPanel = new JPanel();
        JButton deleteButton = new JButton("Delete Song");

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        artistButtonPanel.add(addArtistButton);
        deleteButtonPanel.add(deleteButton);

        addSongspanel.add(buttonPanel, BorderLayout.SOUTH);
        artistPanel.add(artistButtonPanel, BorderLayout.SOUTH);
        deleteSongPanel.add(deleteButtonPanel, BorderLayout.SOUTH);

        tableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Artist", "Genre", "Album", "Producer", "Writer", "Publisher", "Length" },
                0);
        table = new JTable(tableModel);

        add(tabs, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        /* add(buttonPanel, BorderLayout.SOUTH); */

        refreshButton.addActionListener(e -> loadSongs());
        addButton.addActionListener(e -> addSongs());
        addArtistButton.addActionListener(e -> addArtist());
        deleteButton.addActionListener(e -> deleteSong());

        loadSongs();
        setVisible(true);
    }

    private void loadSongs() {
        Connection conn = App.cp.getConnection();
        tableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseUtilities.getSongs(conn);

            SwingUtilities.invokeLater(() -> {
                try {
                    while (rs.next()) {
                        tableModel.addRow(new Object[] {
                                rs.getString("songID"),
                                rs.getString("songName"),
                                rs.getString("artistID"),
                                rs.getString("genreName"),
                                rs.getString("albumName"),
                                rs.getString("producerName"),
                                rs.getString("writerName"),
                                rs.getString("publisherName"),
                                rs.getString("songLength")
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "ErrorLoadingSongs1" + e.getMessage());
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading songs2: " + e.getMessage());
        } finally {
            App.cp.returnConnection(conn);
        }
    }

    private void addSongs() {
        Connection conn = App.cp.getConnection();

        String songname = nameField.getText();
        String songArtist = artistField.getText();
        String songGenre = genreField.getText();
        String songAlbum = albumField.getText();
        String songProducer = producerField.getText();
        String songWriter = writerField.getText();
        String songPublisher = publisherField.getText();
        Integer length = Integer.parseInt(lengthField.getText());

        nameField.setText("");
        artistField.setText("");
        genreField.setText("");
        albumField.setText("");
        producerField.setText("");
        writerField.setText("");
        publisherField.setText("");
        lengthField.setText("");

        DatabaseUtilities.addSong(songname, songArtist, songGenre, songAlbum, songProducer, songWriter, songPublisher,
                length, conn);
        loadSongs();
        JOptionPane.showMessageDialog(null, "Song added", "Error", JOptionPane.INFORMATION_MESSAGE);
        App.cp.returnConnection(conn);
    }

    private void addArtist() {

        Connection conn = App.cp.getConnection();

        String artistName = artistNameField.getText();
        String email = emailfield.getText();
        String dob = dobfield.getText();

        artistNameField.setText("");
        emailfield.setText("");
        dobfield.setText("");

        DatabaseUtilities.addArtist(artistName, email, dob, conn);
        App.cp.returnConnection(conn);
    }

    private void deleteSong() {
        Connection conn = App.cp.getConnection();

        String songID = songIDField.getText();

        int status = DatabaseUtilities.deleteSongs(songID, conn);
        loadSongs();

        if (status == -1) {
            JOptionPane.showMessageDialog(null, "Song with id not found", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
