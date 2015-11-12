import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import stormcanal.ZKCanal;
import stormcanal.ZKStateCanal;
import backtype.storm.Config;


public class ZKStateTest {
	
	public    final String ZK_ROOT = "/otter/canal";
	
	public   final String ZK_CLUSTER = ZK_ROOT+"/cluster";
	
	public   final String ZK_DESTINATIONS = ZK_ROOT+"/destinations";
	
	ZKStateCanal zk =null;
	@Before
	public void createZKState(){
		Map stateConf = new HashMap();
		List<String> zkServers = new ArrayList<>();
		zkServers.add("192.168.33.14");
        Integer zkPort = 2181;
        stateConf.put(Config.TRANSACTIONAL_ZOOKEEPER_SERVERS, zkServers);
        stateConf.put(Config.TRANSACTIONAL_ZOOKEEPER_PORT, zkPort);
        stateConf.put(Config.TRANSACTIONAL_ZOOKEEPER_ROOT, ZKCanal.ZK_ROOT);
        stateConf.put(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT, 20000);
        stateConf.put(Config.STORM_ZOOKEEPER_RETRY_TIMES, 5);
        stateConf.put(Config.STORM_ZOOKEEPER_RETRY_INTERVAL, 1000);
        zk = new ZKStateCanal(stateConf);
	}
	
	@Test
	public void readJSONTest(){
		Map<Object, Object> readJSON = zk.readJSON("/otter/canal/destinations/example/running");
		System.out.println(readJSON);
	}
	@Test
	public void readChildrenTest(){
		String path = "/otter/canal/destinations";
		List<String> result = zk.forPath(path);
		System.out.println(result);
	}
	
	@Test
	public void tt(){
		Long time = System.currentTimeMillis();
		System.out.println(time);
	}
}
