package cn.mzhong.janytask.queue.provider.jdbc;

import cn.mzhong.janytask.tool.PRInvoker;

import javax.sql.DataSource;
import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * SQL执行辅助类
 */
public class DataSourceHelper {
    protected DataSource dataSource;

    public DataSourceHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    protected void loadParameters(PreparedStatement statement, Object[] parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
    }

    public <T> T openConnection(PRInvoker<Connection, T> invoker) {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return invoker.invoke(connection);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    // pass
                }
            }
        }
    }

    public <T> T createStatement(final String sql, final PRInvoker<PreparedStatement, T> invoker) {
        return this.openConnection(new PRInvoker<Connection, T>() {
            public T invoke(Connection connection) throws Exception {
                PreparedStatement statement = connection.prepareStatement(sql);
                try {
                    return invoker.invoke(statement);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    statement.close();
                }
            }
        });
    }

    public <T> T query(final String sql, final Object[] parameters, final PRInvoker<ResultSet, T> invoker) {
        return this.createStatement(sql, new PRInvoker<PreparedStatement, T>() {
            public T invoke(PreparedStatement statement) throws Exception {
                loadParameters(statement, parameters);
                ResultSet resultSet = statement.executeQuery();
                try {
                    if (resultSet.next()) {
                        return invoker.invoke(resultSet);
                    }
                    return null;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    resultSet.close();
                }
            }
        });
    }

    public <T> LinkedList<T> queryList(String sql, final Object[] parameters, final PRInvoker<ResultSet, T> invoker) {
        return this.createStatement(sql, new PRInvoker<PreparedStatement, LinkedList<T>>() {
            public LinkedList<T> invoke(PreparedStatement statement) throws Exception {
                loadParameters(statement, parameters);
                ResultSet resultSet = statement.executeQuery();
                LinkedList<T> list = new LinkedList<T>();
                try {
                    while (resultSet.next()) {
                        list.add(invoker.invoke(resultSet));
                    }
                    return list;
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public boolean execute(String sql, final Object... parameters) {
        return this.createStatement(sql, new PRInvoker<PreparedStatement, Boolean>() {
            public Boolean invoke(PreparedStatement statement) throws Exception {
                loadParameters(statement, parameters);
                return statement.execute();
            }
        });
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

    public int update(String sql, final Object... parameters) {
        return this.createStatement(sql, new PRInvoker<PreparedStatement, Integer>() {
            public Integer invoke(PreparedStatement statement) throws Exception {
                loadParameters(statement, parameters);
                return statement.executeUpdate();
            }
        });
    }
}
