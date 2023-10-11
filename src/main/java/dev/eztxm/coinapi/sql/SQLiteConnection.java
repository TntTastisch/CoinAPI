package dev.eztxm.coinapi.sql;

import java.io.File;
import java.sql.*;

public class SQLiteConnection {
    private final Connection connection;

    /**
     * Initializes a new SQLite database connection using the given database name.
     *
     * @param databaseName The name of the SQLite database (without the ".db" extension).
     */
    public SQLiteConnection(String databaseName) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes a new SQLite database connection using the specified path and database name.
     *
     * @param path         The directory path where the SQLite database file should be located.
     * @param databaseName The name of the SQLite database (without the ".db" extension).
     */
    public SQLiteConnection(String path, String databaseName) {
        File folder = new File(path);
        if (!folder.exists()) folder.mkdir();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + path + "/" + databaseName + ".db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a SQL query and returns a ResultSet.
     *
     * @param sql     The SQL query to be executed.
     * @param objects An array of parameters to be used in the SQL query.
     * @return A ResultSet containing the query results.
     */
    public ResultSet query(String sql, Object... objects) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setArguments(objects, preparedStatement);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a SQL update statement.
     *
     * @param sql     The SQL update statement to be executed.
     * @param objects An array of parameters to be used in the SQL statement.
     */
    public void update(String sql, Object... objects) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setArguments(objects, preparedStatement);
            preparedStatement.execute();
            preparedStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setArguments(Object[] objects, PreparedStatement preparedStatement) throws SQLException {
        for (int i = 0; i < objects.length; i++) {
            Object object = objects[i];

            if (object instanceof String) {
                preparedStatement.setString(i + 1, (String) object);
                continue;
            }

            if (object instanceof Integer) {
                preparedStatement.setInt(i + 1, (Integer) object);
                continue;
            }

            if (object instanceof Date) {
                preparedStatement.setDate(i + 1, (Date) object);
                continue;
            }

            if (object instanceof Timestamp) {
                preparedStatement.setTimestamp(i + 1, (Timestamp) object);
                continue;
            }

            if (object instanceof Boolean) {
                preparedStatement.setBoolean(i + 1, (Boolean) object);
                continue;
            }

            if (object instanceof Float) {
                preparedStatement.setFloat(i + 1, (Float) object);
                continue;
            }

            if (object instanceof Double) {
                preparedStatement.setDouble(i + 1, (Double) object);
                continue;
            }

            if (object instanceof Long) {
                preparedStatement.setLong(i + 1, (Long) object);
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
