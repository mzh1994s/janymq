package cn.mzhong.janymq.jdbc;

import cn.mzhong.janymq.util.PRInvoker;

import java.sql.ResultSet;
import java.util.LinkedList;

public class ResultSetParser {

    protected ResultSet resultSet;

    public ResultSetParser(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public <T> LinkedList<T> parseList(PRInvoker<ResultSet, T> iterator) {
        LinkedList<T> list = new LinkedList<T>();
        try {
            while (resultSet.next()) {
                list.add(iterator.invoke(resultSet));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T parseOne(PRInvoker<ResultSet, T> parser) {
        try {
            if (resultSet.next()) {
                return parser.invoke(resultSet);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}