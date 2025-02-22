package main;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatabaseUtilities {

    public static void addSong(String name, String artistName, String genre, String album, String producer,
            String writer, String publisher, Integer songlength, Connection conn) {
            String query = "INSERT INTO song (songID, songName, artistID, albumName, genreName, producerName, writerName, publisherName, dateOfPublish, songlength) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            boolean exists = true;
            String id = null;
            /* make a unique and nonexisting id */

            /* checks whether id exists with prepared statement */

            LocalDate today = LocalDate.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    
            while (exists) {
                id = Integer.toString((int) (Math.random() * 10000));
                String checkQuery = "SELECT COUNT(*) FROM song WHERE songID = ?";
                
                try (PreparedStatement ps = conn.prepareStatement(checkQuery)) {
                    ps.setString(1, id);
                    ResultSet rs = ps.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        exists = true;
                    }else {exists = false;}  
                } catch (SQLException e) {
                    System.err.println(e);
                    System.err.println("failed to fetch songs");
                }
            }

            /* sets the parameters and then executes th e prepared statement */

            try {
                PreparedStatement ps = conn.prepareStatement(query);
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, artistName);
                ps.setString(4, genre);
                ps.setString(5, album);
                ps.setString(6, producer);
                ps.setString(7, writer);
                ps.setString(8, publisher);
                ps.setString(9, today.format(formatter));
                ps.setInt(10, songlength);
                ps.executeUpdate();
                System.out.println("Song added");
            } catch (Exception e) {
                System.out.println("Error adding the song");
            }
            App.cp.returnConnection(conn);
        }

    public static void addArtist(String name, String email) {

    }

    public static ResultSet getSongs(Connection conn) {
        String query = "Select * FROM song;";

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(query);
            return rs;

        } catch (SQLException e) {
            System.out.println("Error fetching the songs.");
        }
        return null;
    }

    public static void addArtist(String ID, String Name, String email, String dob) {

        StringBuilder query = new StringBuilder(
                "INSERT INTO artist (artistID, artistName, artistEmail, artistDOB) VALUES (");

        try {
            Connection conn = App.cp.getConnection();

            App.cp.returnConnection(conn);
        } catch (Exception e) {

        }
    }

    public static void addAlbum() {

        try {
            Connection conn = App.cp.getConnection();

            App.cp.returnConnection(conn);
        } catch (Exception e) {

        }
    }

    public static void addWriter() {

        try {
            Connection conn = App.cp.getConnection();

            App.cp.returnConnection(conn);
        } catch (Exception e) {

        }
    }

    public static void addPublisher() {

        try {
            Connection conn = App.cp.getConnection();

            App.cp.returnConnection(conn);
        } catch (Exception e) {

        }
    }

    public static void addUser() {

        try {
            Connection conn = App.cp.getConnection();

            App.cp.returnConnection(conn);
        } catch (Exception e) {

        }
    }
}
