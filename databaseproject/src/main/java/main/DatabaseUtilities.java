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
        String checkArtist = "SELECT artistID FROM artist WHERE artistName = ?";
        String artistID = null;

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

        try {
            PreparedStatement ps = conn.prepareStatement(checkArtist);
            ps.setString(1, artistName);

            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                throw new SQLException("Artist Not Found");
            }
            artistID = rs.getString("artistid");
        } catch (Exception e) {
            System.out.println("Failed to fetch artists" + e);
        }

        /* sets the parameters and then executes th e prepared statement */

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, artistID);
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

    public static ResultSet getArtists(Connection conn) {
        String query = "SELECT * FROM artist;";

        try {
            Statement s = conn.createStatement();
            ResultSet rs = s.executeQuery(query);
            return rs;
        } catch (Exception e) {
            System.out.println("Error Fetching the artists");
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
             * -2 for song not founds
             */
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(checkSong);
            ps.setString(1, songID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return -2;
            }
        } catch (SQLException e) {
            System.err.println(e);
        }

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, songID);

            int deletedRows = ps.executeUpdate();

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

    public static int deleteArtist(String artistID, Connection conn) {
        String checkArtist = "SELECT artistid FROM artist WHERE artistid = ?;";
        String query = "DELETE FROM artist WHERE artistid = ?;";

        try {
            /* transaction start */

            /* same status codes as in delete song */
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(checkArtist);
            ps.setString(1, artistID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                return -2;
            }
        } catch (SQLException e) {
            System.err.println(e);
        }

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, artistID);

            int deletedRows = ps.executeUpdate();

            if (deletedRows > 0) {
                conn.commit();
                System.out.println("deletion successfull of Artist with id: " + artistID);
                return 0;
            } else {
                conn.rollback();
                System.out.println("Artist not deleted");
                return -1;
            }
        } catch (SQLException e) {
            System.out.println("error");
            return -1;
        }
    }

    public static int updateSongName(Connection conn, String songID, String songName) {
        String checkSongExistence = "SELECT songID FROM song WHERE songID = ?;";
        String query = "UPDATE song SET songName = ? WHERE songID = ?;";

        try {
            /* transaction start */

            /* status messages same as in deleteSong */
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(checkSongExistence);
            ps.setString(1, songID);

            ResultSet rs = ps.executeQuery(checkSongExistence);

            if (!rs.next()) {
                return -2;
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, songName);
            ps.setString(2, songID);

            int changedRows = ps.executeUpdate();

            if (changedRows > 0) {
                conn.commit();
                System.out.println("Song name updated with id: " + songID);
                return 0;
            } else {
                conn.rollback();
                System.out.println("Song not found");
                return -1;
            }
        } catch (SQLException e) {
            System.err.println(e);
            return -1;
        }
    }
}
