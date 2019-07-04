package cn.mzhong.janymq.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetReader {

    final static Logger Log = LoggerFactory.getLogger(ResultSetReader.class);
    protected ResultSet resultSet;

    public ResultSetReader(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public boolean next() {
        try {
            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(String columnLabel) {
        try {
            return this.resultSet.getString(columnLabel);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String getString(int columnIndex) {
        try {
            return this.resultSet.getString(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getObject(int columnIndex) {
        try {
            return this.resultSet.getObject(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public long getLong(int columnIndex) {
        try {
            return this.resultSet.getLong(columnIndex);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}