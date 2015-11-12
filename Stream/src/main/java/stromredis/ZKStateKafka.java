package stromredis;

import java.util.List;
import java.util.Map;

import org.apache.curator.framework.api.GetChildrenBuilder;

import basestormkafka.kafka.ZkState;


public class ZKStateKafka extends ZkState{

	public ZKStateKafka(Map stateConf) {
		super(stateConf);
	}
	
	
	public GetChildrenBuilder getChildren(){
		return super.getCurator().getChildren();
	}
	
	public List<String> forPath(String path){
		try {
			return getChildren().forPath(path);
		} catch (Exception e) {
			return null;
		}
	}

}
