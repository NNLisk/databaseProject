package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

/* This is a connectionpool class for maintaining connections
 * 
 * How it works:
 * From main program call ConnectionPool.makeConnPool() to make a pool
 * --> use pool.getConnection() to get a connection to use
 * --> when done with the operation, pool.returnConnection to return it to the list
 * --> end of program pool.shutdown
 */

public class ConnectionPool {
    /*
     * this is the singleton instance of the pool
     * no other variable or method should be static
     * in this class
     */
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

    /*
     * making a new singleton instance of the connection pool.
     * This ensures there is only one connection pool and the
     * constructor is private this way.
     */
    public static ConnectionPool makeConnPool(String url, String userName, String password, int poolSize) {
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
            throw new RuntimeException("Connection to database failed.", e);
        }
    }

    /*
     * these two methods handle the list of connections;
     * if connection pool is empty: it waits until there is a free one
     * if its full it removes it from the list and returns to caller.
     * once the caller is done the connection will be freed and returned
     */

    public synchronized Connection getConnection() {
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
        connPool.add(conn);
        notify();
    }

    /*
     * shuts down the instance:
     * closes connection if errors happen in pool initialization
     * or for a natural shutdown
     * 
     * for natural shutdonw, should wait until all connections
     * are returned
     */
    public synchronized void shutDown() {
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
