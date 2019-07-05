package cn.mzhong.janymq.jdbc;

import java.sql.ResultSet;

public interface ResultSetRowParser<T> {

    T parse(ResultSet resultSet) throws Exception;
}
