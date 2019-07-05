package cn.mzhong.janymq.jdbc;

import cn.mzhong.janymq.tool.PRInvoker;

import javax.sql.DataSource;
import java.sql.*;
import java.util.LinkedList;

/**
 * SQL执行器
 */
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
            closeStatement(statement);
            closeConnection(connection);
        }
    }

    public <T> LinkedList<T> queryList(String sql, Object[] parameters, PRInvoker<ResultSet, T> invoker) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            loadParameters(statement, parameters);
            resultSet = statement.executeQuery();
            return new ResultSetParser(resultSet).parseList(invoker);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(connection);
        }
    }

    public <T> T query(String sql, Object[] parameters, PRInvoker<ResultSet, T> invoker) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
            loadParameters(statement, parameters);
            resultSet = statement.executeQuery();
            return new ResultSetParser(resultSet).parseOne(invoker);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResultSet(resultSet);
            closeStatement(statement);
            closeConnection(connection);
        }
    }

    public long queryLong(String sql, Object[] parameters) {
        Long resultInt = this.query(sql, parameters, new PRInvoker<ResultSet, Long>() {
            public Long invoke(ResultSet resultSet) throws Exception {
                return resultSet.getLong(1);
            }
        });
        return resultInt == null ? 0 : resultInt;
    }

    public String queryString(String sql, Object[] parameters) {
        return this.query(sql, parameters, new PRInvoker<ResultSet, String>() {
            public String invoke(ResultSet resultSet) throws Exception {
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
            closeStatement(statement);
            closeConnection(connection);
        }
    }
}
