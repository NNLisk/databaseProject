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
        String query = "INSERT INTO song (songID, songName, artistID, genreName, albumName, producerName, writerName, publisherName, dateOfPublish, songlength) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
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
                } else {
                    exists = false;
                }
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

    public static void addArtist(String name, String email, String dob, Connection conn) {
        String query = "INSERT INTO artist (artistid, artistname, artistemail, artistdob) VALUES (?, ?, ?, ?)";
        boolean exists = true;
        String id = null;

        while (exists) {
            id = Integer.toString((int) (Math.random() * 10000));
            String checkQuery = "SELECT COUNT(*) FROM artist WHERE artistid = ?";

            try (PreparedStatement ps = conn.prepareStatement(checkQuery)) {
                ps.setString(1, id);
                ResultSet rs = ps.executeQuery();
                rs.next();
                if (rs.getInt(1) > 0) {
                    exists = true;
                } else {
                    exists = false;
                }
            } catch (SQLException e) {
                System.err.println(e);
                System.err.println("failed to fetch songs");
            }
        }

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, dob);

            ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Failed to add artist, " + e);
        }
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

    public static int deleteSongs(String songID, Connection conn) {
        String checkSong = "SELECT songid FROM song WHERE songid = ?;";
        String query = "DELETE FROM song WHERE songid = ?;";

        try {
            /* transaction start */

            /*
             * returns status code
             * 0 for succesful deletion
             * -1 for any error
             */
            conn.setAutoCommit(false);
            PreparedStatement psSongCheck = conn.prepareStatement(checkSong);
            psSongCheck.setString(1, songID);
            ResultSet rs = psSongCheck.executeQuery();

            if (!rs.next()) {
                return -1;
            }
        } catch (SQLException e) {
            System.err.println(e);
        }

        try {
            PreparedStatement psQuery = conn.prepareStatement(query);
            psQuery.setString(1, songID);

            int deletedRows = psQuery.executeUpdate();

            if (deletedRows > 0) {
                conn.commit();
                System.out.println("deletion successfull of song with id: " + songID);
                return 0;
            } else {
                conn.rollback();
                System.out.println("Song not deleted");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("error");
            return -1;
        }
    }
}
