package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/* Class makes a pool of premade connections that are handed out and returned. this prevents having
 * to create new connections whenever database needs to be interacted with */

public class ConnectionPool {

    private static ConnectionPool currentPool;
    private States state;

    private ArrayList<Connection> connPool = new ArrayList<Connection>();
    private int poolSize;

    /* private constructor for the Connection pool */

    private ConnectionPool(String url, String username, String password, int poolSize) {
        this.poolSize = poolSize;
        this.state = States.initializing;
        for (int i = 0; i < this.poolSize; i++) {
            try {
                connPool.add(createConnection(url, username, password));
            } catch (Exception e) {
                System.out.println("Error creating the connections.");
                this.shutDown();
            }
        }
        this.state = States.running;
    }

    /* Creating the pool happens throuhg this */

    public static ConnectionPool makeConnPool(String url, String userName, String password, int poolSize, int timeout) {
        DriverManager.setLoginTimeout(timeout);
        if (currentPool == null) {
            currentPool = new ConnectionPool(url, userName, password, poolSize);
        }
        return currentPool;
    }

    /* Actually makes the connections to the pool */
    private Connection createConnection(String url, String name, String password) {
        try {
            return DriverManager.getConnection(url, name, password);
        } catch (SQLException e) {
            System.err.println(e);
            throw new RuntimeException("Connection to database failed.", e);
        }
    }

    /*
     * hands out connections, if none are available, waits untill
     * returnconnection notifies the program.
     */

    public synchronized Connection getConnection() {
        System.out.println("getting connection");
        if (this.state == States.shuttingDown || this.state == States.shutDown) {
            return null;
        }

        while (connPool.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException Ignored) {
            }
        }
        return connPool.remove(0);
    }

    public synchronized void returnConnection(Connection conn) {
        System.out.println("connection returned");
        connPool.add(conn);
        notify();
    }

    /* pool shutdown mechanism */

    public synchronized void shutDown() {
        System.out.println("shutting down");
        if (this.state == States.initializing) {
            for (Connection conn : connPool) {
                try {
                    conn.close();
                    System.out.println("Connection closed");
                } catch (Exception e) {
                    System.out.println("closing failed");
                }
            }
        }

        if (this.state == States.running) {
            this.state = States.shuttingDown;

            while (connPool.size() != poolSize) {
                try {
                    System.out.println("Waiting for processes to finish.");
                    wait();
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    System.out.println("interrupted");
                    break;
                }
            }

            for (Connection connection : connPool) {
                try {
                    connection.close();
                    System.out.println("closed a connection.");
                } catch (SQLException e) {
                    System.out.println("connection failed");
                }
            }
            connPool.clear();
            this.state = States.shutDown;
            System.out.println("Connection pool shut down");
        }
    }
}
