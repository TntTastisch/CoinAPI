package dev.eztxm.coinapi.sql;

import java.io.File;
import java.sql.*;

public class SQLiteConnection {
    private final Connection connection;

    public SQLiteConnection(String databaseName) {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public SQLiteConnection(String path, String databaseName) {
        File folder = new File(path);
        if (!folder.exists()) folder.mkdir();
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:" + path + "/" + databaseName + ".db");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet query(String sql, Object... objects) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            setArguments(objects, preparedStatement);
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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
                return;
            }
            
            if (object instanceof Integer) {
                preparedStatement.setInt(i + 1, (Integer) object);
                return;
            } 

            if (object instanceof Date) {
                preparedStatement.setDate(i + 1, (Date) object);
                return;
            } 

            if (object instanceof Timestamp) {
                preparedStatement.setTimestamp(i + 1, (Timestamp) object);
                return;
            }
            
            if (object instanceof Boolean) {
                preparedStatement.setBoolean(i + 1, (Boolean) object);
                return;
            } 

            if (object instanceof Float) {
                preparedStatement.setFloat(i + 1, (Float) object);
                return;
            } 
            
            if (object instanceof Double) {
                preparedStatement.setDouble(i + 1, (Double) object);
                return;
            } 
            
            if (object instanceof Long) {
                preparedStatement.setLong(i + 1, (Long) object);
                return;
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
