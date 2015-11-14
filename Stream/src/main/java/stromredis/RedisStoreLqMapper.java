package stromredis;

import org.apache.storm.redis.common.mapper.TupleMapper;
import org.apache.storm.redis.common.mapper.RedisDataTypeDescription.RedisDataType;

import backtype.storm.tuple.Tuple;

public interface RedisStoreLqMapper  extends TupleMapper{
	
	 public RedisDataType getDataType(Tuple input);
	 
	 public String getAdditionalKey(Tuple input);
	 
}
