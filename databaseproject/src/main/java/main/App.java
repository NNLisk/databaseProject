package main;

import java.sql.Connection;

import javax.swing.SwingUtilities;

/* AI note: AI in this project is used for implementing some of the gui
 * and creating the exe files. Main database functionality is with few
 * exceptions self made
 */

public class App {
    public static ConnectionPool cp;

    public static void main(String[] args) {

        /*
         * connection pool, username postgres, password password, 5 connections with a
         * timeout of 2 hours
         */
        cp = ConnectionPool.makeConnPool(
                "jdbc:postgresql://localhost:5432/SongDataBase",
                "postgres", "password", 5, 7200);

        Connection test = cp.getConnection();

        if (test != null) {
            System.out.println("Connections made successfully!");
            cp.returnConnection(test);
        } else {
            System.out.println("Creating connections failed.");
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new DatabaseGUI();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
