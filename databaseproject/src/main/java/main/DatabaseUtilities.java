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
            return rs;

        } catch (SQLException e) {
            System.out.println("Error fetching the songs.");
        }

        return null;
    }
}
