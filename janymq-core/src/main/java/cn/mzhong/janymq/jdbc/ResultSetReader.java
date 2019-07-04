package cn.mzhong.janymq.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ResultSetReader {

    final static Logger Log = LoggerFactory.getLogger(ResultSetReader.class);
    protected ResultSet resultSet;

    public ResultSetReader(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    public <T> List<T> readList(ResultSetIterator<T> iterator) {
        List<T> list = new ArrayList<>();
        try {
            while (resultSet.next()) {
                list.add(iterator.read(resultSet));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T> T readOne(ResultSetIterator<T> iterator) {
        try {
            if(resultSet.next()){
                return iterator.read(resultSet);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}