package main;

import java.sql.Connection;

import javax.swing.SwingUtilities;

public class App {
    public static ConnectionPool cp;

    public static void main(String[] args) {

        /*
         * connection pool, username postgres, password password, 5 connections with a
         * timeout of 2 hours
         */
        cp = ConnectionPool.makeConnPool(
                "jdbc:postgresql://localhost:5432/SongDataBase",
                "postgres", "masterchief", 5, 7200);

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
