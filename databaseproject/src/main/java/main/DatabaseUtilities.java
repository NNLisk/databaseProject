package main;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseUtilities {

    public static void addSong(String name, String artistName, String genre, String album, String producer,
            String writer, String publisher, Integer songlength, Connection conn) {
            String query = "INSERT INTO song (songID, songName, artistID, albumName, genreName, producerName, writerName, publisherName, dateOfPublish, songlength) VALUES (";
            
            query += "'" + name + "', '" + artistName + "', '" + album + "', '" + genre + "', '" + producer + "', '" + writer + "', '" + publisher + "', '" + "today" + "', " + songlength + ");" ;
            System.out.println(query);
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
