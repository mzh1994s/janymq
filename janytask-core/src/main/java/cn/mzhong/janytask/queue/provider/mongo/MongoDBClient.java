package cn.mzhong.janytask.queue.provider.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

public class MongoDBClient {
    protected String host;
    protected int port;
    protected String username;
    protected String database;
    protected String password;

    public MongoDBClient(String host, int port, String username, String databaseName, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.database = databaseName;
        this.password = password;
        ServerAddress serverAddress = new ServerAddress(host, port);
        MongoCredential credential = MongoCredential.createScramSha1Credential(username, databaseName, password.toCharArray());
        List<ServerAddress> addressList = new ArrayList<ServerAddress>();
        addressList.add(serverAddress);
        List<MongoCredential> credentialList = new ArrayList<MongoCredential>();
        credentialList.add(credential);
        MongoClient mongoClient = new MongoClient(addressList, credentialList);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = database.getCollection("");
        database.runCommand(new Bson() {
            public <TDocument> BsonDocument toBsonDocument(Class<TDocument> tDocumentClass, CodecRegistry codecRegistry) {
                return null;
            }
        });
    }
}
