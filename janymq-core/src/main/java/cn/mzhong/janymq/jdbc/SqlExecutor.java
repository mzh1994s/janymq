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

    public <T> List<T> queryList(String sql, Object[] parameters, ResultSetRowParser<T> iterator) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            loadParameters(statement, parameters);
            resultSet = statement.executeQuery();
            return new ResultSetParser(resultSet).parseList(iterator);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
    }

    public <T> T query(String sql, Object[] parameters, ResultSetRowParser<T> iterator) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            loadParameters(statement, parameters);
            resultSet = statement.executeQuery();
            return new ResultSetParser(resultSet).parseOne(iterator);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeConnection(connection);
            closeStatement(statement);
            closeResultSet(resultSet);
        }
    }

    public long queryLong(String sql, Object[] parameters) {
        Long resultInt = this.query(sql, parameters, new ResultSetRowParser<Long>() {
            @Override
            public Long parse(ResultSet resultSet) throws Exception {
                return resultSet.getLong(1);
            }
        });
        return resultInt == null ? 0 : resultInt;
    }

    public String queryString(String sql, Object[] parameters) {
        return this.query(sql, parameters, new ResultSetRowParser<String>() {
            @Override
            public String parse(ResultSet resultSet) throws Exception {
                return resultSet.getString(1);
            }
        });
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
