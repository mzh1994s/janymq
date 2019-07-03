package cn.mzhong.janymq.jdbc;

import cn.mzhong.janymq.line.LineManager;
import cn.mzhong.janymq.line.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataSourceLineManager implements LineManager {

    final static Logger Log = LoggerFactory.getLogger(DataSourceLineManager.class);

    DataSource dataSource;
    protected String table;

    protected ResultSet query(String sql, String... args) {
        try {
            Statement statement = dataSource.getConnection().createStatement();
            PreparedStatement preparedStatement = dataSource.getConnection().prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                preparedStatement.setString(i, args[i]);
            }
            return statement.executeQuery(sql);
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            throw new RuntimeException("执行SQL出错：" + sql, e);
        }
    }

    protected List<Message> queryMessageList() {
        String sql = "select key,push_time,done_time,error_time,throwable,data from ? where status = ?";
        ResultSet resultSet = query(sql, table, "W");
        List<Message> list = new ArrayList<>();
        try {
            while (!resultSet.isLast()) {
                Message message = new Message();
                message.setKey(resultSet.getString(0));
                list.add(message);
                resultSet.next();
            }
        } catch (Exception e) {
            Log.error(e.getMessage(), e);
            throw new RuntimeException("SQL执行结果转换为Message对象出错", e);
        }
        return list;
    }

    @Override
    public String ID() {
        return null;
    }

    @Override
    public void push(Message message) {
    }

    @Override
    public Message poll() {
        return null;
    }

    @Override
    public void back(Message message) {

    }

    @Override
    public void done(Message message) {

    }

    @Override
    public void error(Message message) {

    }

    @Override
    public long length() {
        return 0;
    }
}
