package main;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseUtilities {

    public static void addSong(String name, String artistName, String genre, String album, String producer,
            String writer, String publisher, String songlength) {
    }

    public static void addArtist(String name, String email) {

    }

    public static ResultSet getSongs() {
        String query = "Select * FROM songs;";

        try {
            Connection conn = App.cp.getConnection();
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(query);

            App.cp.returnConnection(conn);
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
