package stromredis;

import org.apache.storm.redis.common.mapper.RedisDataTypeDescription.RedisDataType;

import backtype.storm.tuple.ITuple;
import backtype.storm.tuple.Tuple;

public class RedisStoreLqCustomerMapper implements RedisStoreLqMapper {

	@Override
	public String getKeyFromTuple(ITuple arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValueFromTuple(ITuple arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RedisDataType getDataType(Tuple input) {
		System.out.println(input.getMessageId());
		System.out.println(input.getString(0));
		System.out.println(input.getString(0));
		
		
		return null;
	}

	@Override
	public String getAdditionalKey(Tuple input) {
		// TODO Auto-generated method stub
		return null;
	}

}
