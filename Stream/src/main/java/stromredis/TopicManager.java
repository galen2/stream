package stromredis;

import j.u.StringUtil;

import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import kafka.javaapi.consumer.ConsumerConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;

import stromredis.RedisSpout.EmitState;
import backtype.storm.Config;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.tuple.Values;
import basestormkafka.kafka.PartitionManager;
import basestormkafka.kafka.ZkState;

public class TopicManager {

    public static final Logger LOG = LoggerFactory.getLogger(PartitionManager.class);

    
    ZkState _state;
    RedisConfig _spoutConfig; 
    private  ConsumerConnector consumer;

    private  ConsumerGroupExample example = null;
    
    LinkedList<String> _waitingToEmit = new LinkedList<String>();
    
    public TopicManager(RedisConfig redisConfig,ZKStateKafka state,String topic) {
        _spoutConfig = redisConfig;
		this._state = state;
        example = new ConsumerGroupExample(redisConfig.zkConnect, redisConfig.groupId, topic);
    }

    //returns false if it's reached the end of current batch
    public EmitState next(SpoutOutputCollector collector) {
    	String content = example.consumer();
    	if(StringUtil.isNotBlank(content)){
    		String messageId = UUID.randomUUID().toString();
    		collector.emit(new Values(content),messageId);
    		return EmitState.EMITTED_MORE_LEFT;
    	}else{
    		return EmitState.EMITTED_END;
    	}
    }
}
