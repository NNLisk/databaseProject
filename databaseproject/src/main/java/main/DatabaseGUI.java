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

        tableModel = new DefaultTableModel(
                new String[] { "ID", "Name", "Artist", "Genre", "Album", "Producer", "Writer", "Publisher", "Length" },
                0);
        table = new JTable(tableModel);

        add(inputPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> loadSongs());

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
}
