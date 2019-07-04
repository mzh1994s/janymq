package cn.mzhong.janymq.jdbc;

import java.sql.ResultSet;

public interface ResultSetIterator<T> {

    T read(ResultSet resultSet) throws Exception;
}
