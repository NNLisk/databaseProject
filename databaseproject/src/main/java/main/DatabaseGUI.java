package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;

/* AI note: AI in this project is used for implementing some of the gui
 * and creating the exe files. Database functionality is with few
 * exceptions self made
 */

public class DatabaseGUI extends JFrame {
    private JTextField songIDField, cngSongIdField, newSongNameField, deleteArtistIDField;
    private JTable songTable, artistTable, genreTable, albumTable;
    private DefaultTableModel songTableModel, artistTableModel, genreTableModel, albumTableModel;

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
        JPanel deleteArtistPanel = new JPanel(new BorderLayout());

        String[] addSongTitles = { "Name", "Artist", "Genre", "Album", "Producer", "Writer", "Publisher", "Length" };
        String[] addArtistTitles = { "Artist name", "Email", "Date of Birth" };

        JPanel songInputs = new JPanel(new GridLayout(8, 2));

        for (String title : addSongTitles) {
            songInputs.add(new JLabel(title));
            JTextField field = new JTextField();
            songInputs.add(field);
        }

        JPanel artistInputs = new JPanel(new GridLayout(3, 2));

        for (String title : addArtistTitles) {
            artistInputs.add(new JLabel(title));
            JTextField field = new JTextField();
            artistInputs.add(field);
        }

        JPanel deleteSongInputs = new JPanel(new GridLayout(1, 2));
        deleteSongInputs.add(new JLabel("Song ID: "));
        songIDField = new JTextField();
        deleteSongInputs.add(songIDField);

        JPanel deleteArtistInputs = new JPanel(new GridLayout(1, 2));
        deleteArtistInputs.add(new JLabel("Artist ID: "));
        deleteArtistIDField = new JTextField();
        deleteArtistInputs.add(deleteArtistIDField);

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
        deleteArtistPanel.add(deleteArtistInputs, BorderLayout.NORTH);

        managementTabs.addTab("Register Songs", addSongspanel);
        managementTabs.addTab("Register Artist", artistPanel);
        managementTabs.addTab("Delete Songs", deleteSongPanel);
        managementTabs.addTab("Delete Artists", deleteArtistPanel);
        managementTabs.addTab("Change Song Name", changeSongNamePanel);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Song");
        JPanel artistButtonPanel = new JPanel();
        JButton addArtistButton = new JButton("Add artist");
        JPanel deleteButtonPanel = new JPanel();
        JButton deleteButton = new JButton("Delete Song");
        JPanel cngSongButtonPanel = new JPanel();
        JButton updateName = new JButton("Update Song Name");
        JPanel deleteArtistbuttons = new JPanel();
        JButton deleteArtist = new JButton("Delete artist");

        buttonPanel.add(addButton);
        artistButtonPanel.add(addArtistButton);
        deleteButtonPanel.add(deleteButton);
        deleteArtistbuttons.add(deleteArtist);
        cngSongButtonPanel.add(updateName);

        addSongspanel.add(buttonPanel, BorderLayout.SOUTH);
        artistPanel.add(artistButtonPanel, BorderLayout.SOUTH);
        deleteSongPanel.add(deleteButtonPanel, BorderLayout.SOUTH);
        deleteArtistPanel.add(deleteArtistbuttons, BorderLayout.SOUTH);
        changeSongNamePanel.add(updateName, BorderLayout.SOUTH);

        songTableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Artist", "Genre", "Album", "Producer", "Writer", "Publisher", "Length" },
                0);
        songTable = new JTable(songTableModel);

        artistTableModel = new DefaultTableModel(new String[] { "ID", "Name", "Email", "Date Of Birth" }, 0);
        artistTable = new JTable(artistTableModel);

        genreTableModel = new DefaultTableModel(new String[] { "GenreID", "Genre Name" }, 0);
        genreTable = new JTable(genreTableModel);

        albumTableModel = new DefaultTableModel(new String[] { "Album id", "Album Name", "Artist Name" }, 0);
        albumTable = new JTable(albumTableModel);

        tableTabs.addTab("Songs", new JScrollPane(songTable));
        tableTabs.addTab("Artists", new JScrollPane(artistTable));
        tableTabs.addTab("Genres", new JScrollPane(genreTable));
        tableTabs.addTab("Albums", new JScrollPane(albumTable));

        add(managementTabs, BorderLayout.NORTH);
        add(tableTabs, BorderLayout.CENTER);

        addButton.addActionListener(e -> addSongs(songInputs));
        addArtistButton.addActionListener(e -> addArtist(artistInputs));
        deleteButton.addActionListener(e -> deleteSong());
        deleteArtist.addActionListener(e -> deleteArtist());
        updateName.addActionListener(e -> updateSongName());

        loadSongs();
        loadArtists();
        loadGenres();
        loadAlbums();
        setVisible(true);
    }

    /* BELOW IS ALL THE LOADER METHODS FOR TABLES */
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
                                rs.getString("artistName"),
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

    private void loadArtists() {
        Connection conn = App.cp.getConnection();
        artistTableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseUtilities.getArtists(conn);

            SwingUtilities.invokeLater(() -> {
                try {
                    while (rs.next()) {
                        artistTableModel.addRow(new Object[] {
                                rs.getString("artistID"),
                                rs.getString("artistName"),
                                rs.getString("artistEmail"),
                                rs.getString("artistDoB")
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "ErrorLoadingArtists1" + e.getMessage());
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading Artists2: " + e.getMessage());
        } finally {
            App.cp.returnConnection(conn);
        }
    }

    private void loadGenres() {
        Connection conn = App.cp.getConnection();

        genreTableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseUtilities.getGenres(conn);

            SwingUtilities.invokeLater(() -> {
                try {
                    while (rs.next()) {
                        genreTableModel.addRow(new Object[] {
                                rs.getString("genreID"),
                                rs.getString("genrename"),
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "ErrorLoadingGenres" + e.getMessage());
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading Genres: " + e.getMessage());
        } finally {
            App.cp.returnConnection(conn);
        }
    }

    private void loadAlbums() {
        Connection conn = App.cp.getConnection();

        albumTableModel.setRowCount(0);
        try {
            ResultSet rs = DatabaseUtilities.getAlbums(conn);

            SwingUtilities.invokeLater(() -> {
                try {
                    while (rs.next()) {
                        albumTableModel.addRow(new Object[] {
                                rs.getString("albumID"),
                                rs.getString("albumName"),
                                rs.getString("artistName")
                        });
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "ErrorLoadingAlbums" + e.getMessage());
                }
            });
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading Albums: " + e.getMessage());
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

        try {
            DatabaseUtilities.addSong(songname, songArtist, songGenre, songAlbum, songProducer, songWriter,
                    songPublisher,
                    length, conn);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Artist does not exist, please register artist first", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        loadSongs();
        loadArtists();
        loadGenres();
        loadAlbums();
        JOptionPane.showMessageDialog(null, "Song added", "Error", JOptionPane.INFORMATION_MESSAGE);
        App.cp.returnConnection(conn);
    }

    private void addArtist(JPanel artistinfo) {

        Connection conn = App.cp.getConnection();

        ArrayList<String> artistInfo = new ArrayList<>();

        for (Component component : artistinfo.getComponents()) {
            if (component instanceof JTextField) {
                artistInfo.add(((JTextField) component).getText());
                ((JTextField) component).setText("");
            }
        }

        String artistName = artistInfo.get(0);
        String email = artistInfo.get(1);
        String dob = artistInfo.get(2);

        DatabaseUtilities.addArtist(artistName, email, dob, conn);
        loadArtists();
        App.cp.returnConnection(conn);
    }

    private void deleteSong() {
        Connection conn = App.cp.getConnection();

        String songID = songIDField.getText();

        int status = DatabaseUtilities.deleteSongs(songID, conn);
        loadSongs();

        switch (status) {
            case 0:
                JOptionPane.showMessageDialog(null, "Song Removed", "Update succesful",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            case -1:
                JOptionPane.showMessageDialog(null, "Connection Error", "Error", JOptionPane.ERROR_MESSAGE);
            case -2:
                JOptionPane.showMessageDialog(null, "Song with id not found", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    private void deleteArtist() {
        Connection conn = App.cp.getConnection();

        String artistID = deleteArtistIDField.getText();

        int status = DatabaseUtilities.deleteArtist(artistID, conn);
        loadArtists();
        loadSongs();

        switch (status) {
            case 0:
                JOptionPane.showMessageDialog(null, "Artist Removed", "Update succesful",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            case -1:
                JOptionPane.showMessageDialog(null, "Connection Error", "Error", JOptionPane.ERROR_MESSAGE);
            case -2:
                JOptionPane.showMessageDialog(null, "Artist with id not found", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
    }

    public void updateSongName() {
        Connection conn = App.cp.getConnection();
        String songID = cngSongIdField.getText();
        String newName = newSongNameField.getText();

        int status = DatabaseUtilities.updateSongName(conn, songID, newName);

        switch (status) {
            case 0:
                JOptionPane.showMessageDialog(null, "Song Updated", "Update succesful",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            case -1:
                JOptionPane.showMessageDialog(null, "Connection Error", "Error", JOptionPane.ERROR_MESSAGE);
            case -2:
                JOptionPane.showMessageDialog(null, "Song with id not found", "Error", JOptionPane.ERROR_MESSAGE);
                break;
        }
        loadSongs();
    }
}
