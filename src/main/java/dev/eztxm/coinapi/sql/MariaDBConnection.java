package dev.eztxm.coinapi.sql;

import java.sql.*;

public class MariaDBConnection {
    private final Connection connection;

    public MariaDBConnection(String url, int port, String database, String username, String password) {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://" + url + ":" + port + "/" + database + "?user=" + username + "&password=" + password);
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
            switch (object) {
                case String: { preparedStatement.setString(i + 1, (String) object); break; }
                case Integer: { preparedStatement.setInt(i + 1, (Integer) object); break; }
                case Date: { preparedStatement.setDate(i + 1, (Date) object); break; }
                case Timestamp: { preparedStatement.setTimestamp(i + 1, (Timestamp) object); break; }
                case Boolean: { preparedStatement.setBoolean(i + 1, (Boolean) object); break; }
                case Float: { preparedStatement.setFloat(i + 1, (Float) object); break; }
                case Double: { preparedStatement.setDouble(i + 1, (Double) object); break; }
                case Long: { preparedStatement.setLong(i + 1, (Long) object); break; }
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
