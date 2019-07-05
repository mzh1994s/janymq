package cn.mzhong.janymq.jdbc;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ResultSetParser {

    protected ResultSet resultSet;

    public ResultSetParser(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public <T> List<T> parseList(ResultSetRowParser<T> iterator) {
        List<T> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                list.add(iterator.parse(resultSet));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T parseOne(ResultSetRowParser<T> parser) {
        try {
            if (resultSet.next()) {
                return parser.parse(resultSet);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}