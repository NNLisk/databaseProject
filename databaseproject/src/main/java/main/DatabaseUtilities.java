package main;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DatabaseUtilities {

    /* adds songs to database, and everything else. albums, writers, i lost track. */

    public static void addSong(String name, String artistName, String genre, String album, String producer,
            String writer, String publisher, Integer songlength, Connection conn) {
        String query = "INSERT INTO song (songID, songName, artistID, genreName, albumName, producerName, writerName, publisherName, dateOfPublish, songlength) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String checkArtist = "SELECT artistID FROM artist WHERE artistName = ?";
        
        String artistID = null;

        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        String id = uniqueID("song");
        System.out.println("checking artist");
        
        
        try {
            PreparedStatement ps = conn.prepareStatement(checkArtist);
            ps.setString(1, artistName);
            
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Artist not found");
                throw new SQLException("Artist Not Found");
            }
            artistID = rs.getString("artistid");
        } catch (Exception e) {
            System.out.println("Failed to fetch artists" + e);
        }

        addAlbum(album, artistID);
        addGenre(genre);

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
            System.out.println("Adding song");
            ps.executeUpdate();
            System.out.println("Song added");
        } catch (Exception e) {
            System.out.println("Error adding the song");
        }
        App.cp.returnConnection(conn);
    }

    public static void addAlbum(String albumName, String artistID) {
        String addAlbum = "INSERT INTO album (albumID, albumName, artistID) VALUES (?, ?, ?);";
        String albumCheck = "SELECT albumName FROM album WHERE albumName = ?;";

        String albumID = uniqueID("album");
        Connection conn = App.cp.getConnection();
        System.out.println("adding album");
        
        try {
            PreparedStatement ps = conn.prepareStatement(albumCheck);
            ps.setString(1, albumName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                System.out.println("song already exists");
                return;
            }

        } catch (Exception e) {
            System.err.println(e);
        }
        try {
            PreparedStatement ps = conn.prepareStatement(addAlbum);
            ps.setString(1, albumID);
            ps.setString(2, albumName);
            ps.setString(3, artistID);
            ps.executeQuery();
            System.out.println("album added");
        } catch (Exception e) {
            System.err.println(e);
        }
        App.cp.returnConnection(conn);
    }

    public static void addGenre(String genreName) {
        String addGenre = "INSERT INTO genre (genreID, genreName) VALUES (?, ?);";
        String genreCheck = "SELECT genreName FROM genre WHERE genreName = ?;";

        String genreID = uniqueID("genre");
        Connection conn = App.cp.getConnection();
        System.out.println("adding Genre");

        try {
            PreparedStatement ps = conn.prepareStatement(genreCheck);
            ps.setString(1, genreName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()){
                System.out.println("genre already exists");
                return;
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        try {
            PreparedStatement ps = conn.prepareStatement(addGenre);
            ps.setString(1, genreID);
            ps.setString(2, genreName);
            ps.executeUpdate();
            System.out.println("Genre added");
        } catch (Exception e) {
            System.err.println(e);
        }
        App.cp.returnConnection(conn);
    }

    public static void addWriter(String writerName) {
        String addWriter = "INSERT INTO writer (writerID, writerName) VALUES (?, ?);";
        String writerCheck = "SELECT writerName FROM writer WHERE writerName = ?;";

        String writerID = uniqueID("writer");
        Connection conn = App.cp.getConnection();
        System.out.println("adding writer");

        try {
            PreparedStatement ps = conn.prepareStatement(writerCheck);
            ps.setString(1, writerName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Genre already exists");
                return;
            }
        } catch (Exception e) {
            System.err.println(e);
        }
        try {
            PreparedStatement ps = conn.prepareStatement(addWriter);
            ps.setString(1, writerID);
            ps.setString(2, writerName);
            ps.executeUpdate();
            System.out.println("writer added");
        } catch (Exception e) {
            System.err.println(e);
        }
        App.cp.returnConnection(conn);
    }

    public static void addPublisher(String publisherName) {
        String addPublisher = "INSERT INTO publisher (publisherID, publisherName) VALUES (?, ?);";
        String checkPublisher = "SELECT publisherName FROM publisher WHERE publisherName = ?;";

        String pubID = uniqueID("publisher");
        Connection conn = App.cp.getConnection();
        System.out.println("adding publisher");

        try {
            System.out.println("checking publisher existence");
            PreparedStatement ps = conn.prepareStatement(checkPublisher);
            ps.setString(1, publisherName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("publisher already exists");
                return;
            }
        } catch (Exception e) {
            System.err.println(e);
        }

        try {
            PreparedStatement ps = conn.prepareStatement(addPublisher);
            ps.setString(1, pubID);
            ps.setString(2, publisherName);
            ps.executeUpdate();
            System.out.println("publisher added");
        } catch (Exception e) {
            System.err.println();
        }
    }

    public static void addArtist(String name, String email, String dob, Connection conn) {
        String query = "INSERT INTO artist (artistid, artistname, artistemail, artistdob) VALUES (?, ?, ?, ?)";
        String id = null;
        System.out.println("adding artist");

        id = uniqueID("artist");

        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, email);
            ps.setString(4, dob);
            ps.executeUpdate();
            System.out.println("artist added");
        } catch (Exception e) {
            System.err.println("Failed to add artist, " + e);
        }
    }

    public static ResultSet getSongs(Connection conn) {
        String query = "Select * FROM song JOIN artist ON song.artistID = artist.artistID;";

        try {
            System.out.println("getting songs");
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
            System.out.println("getting songs");
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
        System.out.println("deleting songs");
        try {
            /* transaction start */

            /*
             * returns status code
             * 0 for succesful deletion
             * -1 for any error
             * -2 for song not founds
             */
            System.out.println("starting transaction");
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(checkSong);
            ps.setString(1, songID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("song doesn't exist");
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
        System.out.println("deleting artist");
        try {
            /* transaction start */

            /* same status codes as in delete song */
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(checkArtist);
            ps.setString(1, artistID);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("artist doesnt exist");
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
            System.out.println("transaction started");
            conn.setAutoCommit(false);

            PreparedStatement ps = conn.prepareStatement(checkSongExistence);
            ps.setString(1, songID);

            ResultSet rs = ps.executeQuery(checkSongExistence);

            if (!rs.next()) {
                System.out.println("song doesn't exist");
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

    public static String uniqueID(String tablename) {
        String id = null;
        Connection conn = App.cp.getConnection();
        boolean exists = true;
        System.out.println("getting an id");
        while (exists) {
            id = Integer.toString((int) (Math.random() * 10000));
            String checkQuery = "SELECT COUNT(*) FROM " + tablename + " WHERE " + tablename + "ID = ?";

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
                System.err.println("failed to fetch table");
                return null;
            }
        }
        System.out.println("returning id");
        return id;
    }
}
