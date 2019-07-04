package cn.mzhong.janymq.jdbc;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class SqlExecutor {
    protected DataSource dataSource;

    public SqlExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // pass
            }
        }
    }

    protected void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // pass
            }
        }
    }

    protected void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // pass
            }
        }
    }

    protected void loadParameters(PreparedStatement statement, Object[] parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
    }

    public boolean execute(String sql, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            loadParameters(statement, parameters);
            return statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
        }
    }

    public <T> List<T> queryList(String sql, Object[] parameters, ResultSetIterator<T> iterator) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            loadParameters(statement, parameters);
            resultSet = statement.executeQuery();
            return new ResultSetReader(resultSet).readList(iterator);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
    }

    public <T> T query(String sql, Object[] parameters, ResultSetIterator<T> iterator) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            loadParameters(statement, parameters);
            resultSet = statement.executeQuery();
            return new ResultSetReader(resultSet).readOne(iterator);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
    }

    public int update(String sql, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            loadParameters(statement, parameters);
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
        }
    }
}
