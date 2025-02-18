package main;

import java.sql.Connection;

/**
 * Hello world!
 *
 */
public class App {
    public static ConnectionPool cp;

    public static void main(String[] args) {
        cp = ConnectionPool.makeConnPool(
                "jdbc:postgresql://localhost:5432/postgres",
                "postgres", "masterchief",
                5);

        Connection test = cp.getConnection();

        if (test != null) {
            System.out.println("Connections made successfully!");
            cp.returnConnection(test);
        } else {
            System.out.println("Creating connections failed.");
        }
    }
}
