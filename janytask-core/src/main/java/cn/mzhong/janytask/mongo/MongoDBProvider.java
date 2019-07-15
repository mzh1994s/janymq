package cn.mzhong.janytask.mongo;

import cn.mzhong.janytask.core.TaskContext;
import cn.mzhong.janytask.queue.MessageDao;
import cn.mzhong.janytask.queue.QueueInfo;
import cn.mzhong.janytask.queue.QueueProvider;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

import java.util.ArrayList;
import java.util.List;

public class MongoDBProvider implements QueueProvider {

    protected String host;
    protected int port;
    protected String username;
    protected String database;
    protected String password;
    protected MongoDatabase mongoDatabase;
    protected TaskContext context;

    public MessageDao createMessageDao(QueueInfo queueInfo) {
        return new MongoDbMessageDao(context, queueInfo, mongoDatabase.getCollection(queueInfo.ID()));
    }

    public void init(TaskContext context) {
        this.context = context;
        ServerAddress serverAddress = new ServerAddress(host, port);
        MongoCredential credential = MongoCredential.createScramSha1Credential(username, database, password.toCharArray());
        List<ServerAddress> addressList = new ArrayList<ServerAddress>();
        addressList.add(serverAddress);
        List<MongoCredential> credentialList = new ArrayList<MongoCredential>();
        credentialList.add(credential);
        MongoClient mongoClient = new MongoClient(addressList, credentialList);
        this.mongoDatabase = mongoClient.getDatabase(database);
    }
}
