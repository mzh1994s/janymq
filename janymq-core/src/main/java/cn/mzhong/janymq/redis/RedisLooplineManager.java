package cn.mzhong.janymq.redis;

import cn.mzhong.janymq.core.MQContext;
import cn.mzhong.janymq.line.LooplineInfo;
import cn.mzhong.janymq.tool.PRInvoker;
import redis.clients.jedis.Jedis;

import java.util.*;

/**
 * Key Such as JSimpleMQ:LooplineInfo:LoopLineID:[wait|done|error:lock]
 */
public class RedisLooplineManager extends RedisLineManager {

    private static String key(String rootPath, LooplineInfo loopLine) {
        RedisKeyGenerator generator = new RedisKeyGenerator(loopLine);
        return rootPath + ":Loopline:" + generator.generate();
    }

    public RedisLooplineManager(MQContext context, RedisLineManagerProvider provider, LooplineInfo loopLine) {
        super(context, provider.getConnectionFactory(), loopLine, key(provider.rootPath, loopLine));
        this.ID = loopLine.ID();
    }

    @Override
    protected LinkedList<String> idList() {
        return this.redisClient.execute(new PRInvoker<Jedis, LinkedList<String>>() {
            public LinkedList<String> invoke(Jedis jedis) throws Exception {
                LinkedList<String> list = new LinkedList<String>();
                Iterator<byte[]> iterator = jedis.hkeys(waitKey).iterator();
                while (iterator.hasNext()) {
                    list.add(new String(iterator.next()));
                }
                return list;
            }
        });
    }
}
