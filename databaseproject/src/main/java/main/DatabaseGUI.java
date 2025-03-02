package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

/* AI note: AI in this project is used for implementing some of the gui
 * and creating the exe files. Main database functionality is with few
 * exceptions self made
 */

public class DatabaseGUI extends JFrame {
    /* Here all textinputs, tab specifically */
    private JTextField nameField, artistField, genreField, albumField, producerField, writerField, publisherField,
            lengthField;
    private JTextField artistNameField, emailfield, dobfield;
    private JTextField songIDField;
    private JTextField cngSongIdField, newSongNameField;
    private JTable songTable;
    private JTable artistTable;
    private DefaultTableModel songTableModel;
    private DefaultTableModel artistTableModel;

    public DatabaseGUI() {
        /* makes the panel and input fields */
        setTitle("Song Database GUI");
        setSize(1400, 1000);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JTabbedPane managementTabs = new JTabbedPane();
        JTabbedPane tableTabs = new JTabbedPane();

        JPanel addSongspanel = new JPanel(new BorderLayout());
        JPanel artistPanel = new JPanel(new BorderLayout());
        JPanel deleteSongPanel = new JPanel(new BorderLayout());
        JPanel changeSongNamePanel = new JPanel(new BorderLayout());

        String[] addSongTitles = { "Name", "Artist", "Genre", "Album", "Producer", "Writer", "Publisher", "Length" };

        JPanel songInputs = new JPanel(new GridLayout(8, 2));

        for (String title : addSongTitles) {
            songInputs.add(new JLabel(title));
            JTextField field = new JTextField();
            songInputs.add(field);
        }

        JPanel artistInputs = new JPanel(new GridLayout(3, 2));
        artistInputs.add(new JLabel("Artist name:"));
        artistNameField = new JTextField();
        artistInputs.add(artistNameField);

        artistInputs.add(new JLabel("email"));
        emailfield = new JTextField();
        artistInputs.add(emailfield);

        artistInputs.add(new JLabel("Date of Birth:"));
        dobfield = new JTextField();
        artistInputs.add(dobfield);

        JPanel deleteSongInputs = new JPanel(new GridLayout(1, 2));
        deleteSongInputs.add(new JLabel("SongID: "));
        songIDField = new JTextField();
        deleteSongInputs.add(songIDField);

        JPanel changeSongInputs = new JPanel(new GridLayout(2, 2));
        changeSongInputs.add(new JLabel("SongID: "));
        cngSongIdField = new JTextField();
        changeSongInputs.add(cngSongIdField);

        changeSongInputs.add(new JLabel("New Song Name"));
        newSongNameField = new JTextField();
        changeSongInputs.add(newSongNameField);

        artistPanel.add(artistInputs, BorderLayout.NORTH);
        addSongspanel.add(songInputs, BorderLayout.NORTH);
        deleteSongPanel.add(deleteSongInputs, BorderLayout.NORTH);
        changeSongNamePanel.add(changeSongInputs, BorderLayout.NORTH);

        managementTabs.addTab("Register Songs", addSongspanel);
        managementTabs.addTab("Register Artist", artistPanel);
        managementTabs.addTab("Delete Songs", deleteSongPanel);
        managementTabs.addTab("Change Song Name", changeSongNamePanel);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Song");
        JButton refreshButton = new JButton("Refresh Songs");
        JPanel artistButtonPanel = new JPanel();
        JButton addArtistButton = new JButton("Add artist");
        JPanel deleteButtonPanel = new JPanel();
        JButton deleteButton = new JButton("Delete Song");
        JPanel cngSongButtonPanel = new JPanel();
        JButton updateName = new JButton("Update Song Name");

        buttonPanel.add(addButton);
        buttonPanel.add(refreshButton);
        artistButtonPanel.add(addArtistButton);
        deleteButtonPanel.add(deleteButton);
        cngSongButtonPanel.add(updateName);

        addSongspanel.add(buttonPanel, BorderLayout.SOUTH);
        artistPanel.add(artistButtonPanel, BorderLayout.SOUTH);
        deleteSongPanel.add(deleteButtonPanel, BorderLayout.SOUTH);
        changeSongNamePanel.add(updateName, BorderLayout.SOUTH);

        songTableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Artist", "Genre", "Album", "Producer", "Writer", "Publisher", "Length" },
                0);
        songTable = new JTable(songTableModel);

        artistTableModel = new DefaultTableModel(new String[] { "ID", "Name", "Email", "Date Of Birth" }, 0);
        artistTable = new JTable(artistTableModel);

        tableTabs.addTab("Songs", new JScrollPane(songTable));
        tableTabs.addTab("Artists", new JScrollPane(artistTable));

        add(managementTabs, BorderLayout.NORTH);
        add(tableTabs, BorderLayout.CENTER);

        refreshButton.addActionListener(e -> loadSongs());
        addButton.addActionListener(e -> addSongs(songInputs));
        addArtistButton.addActionListener(e -> addArtist());
        deleteButton.addActionListener(e -> deleteSong());

        loadSongs();
        setVisible(true);
    }

    private void loadSongs() {
        Connection conn = App.cp.getConnection();
        songTableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseUtilities.getSongs(conn);

            SwingUtilities.invokeLater(() -> {
                try {
                    while (rs.next()) {
                        songTableModel.addRow(new Object[] {
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

    private void addSongs(JPanel songinfo) {
        Connection conn = App.cp.getConnection();

        ArrayList<String> songInfo = new ArrayList<>();

        for (Component component : songinfo.getComponents()) {
            if (component instanceof JTextField) {
                songInfo.add(((JTextField) component).getText());
                ((JTextField) component).setText("");
            }
        }

        String songname = songInfo.get(0);
        String songArtist = songInfo.get(1);
        String songGenre = songInfo.get(2);
        String songAlbum = songInfo.get(3);
        String songProducer = songInfo.get(4);
        String songWriter = songInfo.get(5);
        String songPublisher = songInfo.get(6);
        Integer length = Integer.parseInt(songInfo.get(7));

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
