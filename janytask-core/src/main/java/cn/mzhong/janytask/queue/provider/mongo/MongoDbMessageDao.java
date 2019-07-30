package cn.mzhong.janytask.queue.provider.mongo;

import cn.mzhong.janytask.application.TaskContext;
import cn.mzhong.janytask.queue.LockedMessageDao;
import cn.mzhong.janytask.queue.Message;
import cn.mzhong.janytask.queue.QueueInfo;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;

import java.util.LinkedList;

public class MongoDbMessageDao extends LockedMessageDao {

    MongoCollection collection;

    public MongoDbMessageDao(TaskContext context, QueueInfo queueInfo, MongoCollection collection) {
        super(context, queueInfo);
        this.collection = collection;
    }

    protected LinkedList<String> queueIdList() {
        MongoCursor iterator = this.collection.find().projection(new Bson() {
            public <TDocument> BsonDocument toBsonDocument(Class<TDocument> tDocumentClass, CodecRegistry codecRegistry) {
                BsonDocument bsonDocument = new BsonDocument();
                bsonDocument.append("id", null);
                return bsonDocument;
            }
        }).returnKey(true).iterator();
        LinkedList<String> cacheKeys = new LinkedList<String>();
        while (iterator.hasNext()) {
            Message message = (Message) iterator.next();
            if (message != null) {
                cacheKeys.add(message.getId());
            }
        }
        return cacheKeys;
    }

    protected Message get(final String id) {
        MongoCursor iterator = this.collection.find(new Bson() {
            public <TDocument> BsonDocument toBsonDocument(Class<TDocument> tDocumentClass, CodecRegistry codecRegistry) {
                BsonDocument bsonDocument = new BsonDocument();
                bsonDocument.append("id", new BsonString(id));
                return bsonDocument;
            }
        }).iterator();
        while (iterator.hasNext()) {
            return (Message) iterator.next();
        }
        return null;
    }

    protected boolean lock(final String id) {
        UpdateResult updateResult = this.collection.updateOne(new Bson() {
            public <TDocument> BsonDocument toBsonDocument(Class<TDocument> tDocumentClass, CodecRegistry codecRegistry) {
                BsonDocument bsonDocument = new BsonDocument();
                bsonDocument.append("id", new BsonString(id));
                bsonDocument.append("status", new BsonString(Message.STATUS_WAIT));
                return bsonDocument;
            }
        }, new Bson() {
            public <TDocument> BsonDocument toBsonDocument(Class<TDocument> tDocumentClass, CodecRegistry codecRegistry) {
                BsonDocument bsonDocument = new BsonDocument();
                bsonDocument.append("status", new BsonString(Message.STATUS_LOCK));
                return bsonDocument;
            }
        });
        return updateResult.isModifiedCountAvailable();
    }

    protected boolean unLock(final String id) {
        UpdateResult updateResult = this.collection.updateOne(new Bson() {
            public <TDocument> BsonDocument toBsonDocument(Class<TDocument> tDocumentClass, CodecRegistry codecRegistry) {
                BsonDocument bsonDocument = new BsonDocument();
                bsonDocument.append("id", new BsonString(id));
                bsonDocument.append("status", new BsonString(Message.STATUS_LOCK));
                return bsonDocument;
            }
        }, new Bson() {
            public <TDocument> BsonDocument toBsonDocument(Class<TDocument> tDocumentClass, CodecRegistry codecRegistry) {
                BsonDocument bsonDocument = new BsonDocument();
                bsonDocument.append("status", new BsonString(Message.STATUS_WAIT));
                return bsonDocument;
            }
        });
        return updateResult.isModifiedCountAvailable();
    }

    public void push(Message message) {

    }

    public void done(Message message) {

    }

    public void error(Message message) {

    }

    public long length() {
        return this.collection.count();
    }
}
