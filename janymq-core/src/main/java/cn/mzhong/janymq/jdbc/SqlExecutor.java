package cn.mzhong.janymq.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlExecutor {
    final static Logger Log = LoggerFactory.getLogger(SqlExecutor.class);
    protected DataSource dataSource;

    public SqlExecutor(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public PreparedStatement createStatement(String sql, Object... args) {
        PreparedStatement statement = null;
        try {
            Connection connection = dataSource.getConnection();
            statement = connection.prepareStatement(sql);
        } catch (SQLException e) {
            throw new RuntimeException("数据库连接异常！", e);
        }
        try {
            loadingArgs(statement, args);
        } catch (SQLException e) {
            throw new RuntimeException("参数设置异常！", e);
        }
        return statement;
    }

    protected void loadingArgs(PreparedStatement statement, Object... parameters) throws SQLException {
        for (int i = 0; i < parameters.length; i++) {
            statement.setObject(i + 1, parameters[i]);
        }
    }

    public boolean execute(String sql, Object... parameters) {
        try {
            return createStatement(sql, parameters).execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet executeQuery(String sql, Object... parameters) {
        try {
            return createStatement(sql, parameters).executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int executeUpdate(String sql, Object... parameters) {
        try {
            return createStatement(sql, parameters).executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
