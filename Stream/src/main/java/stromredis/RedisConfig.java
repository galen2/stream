package stromredis;

import java.io.Serializable;
import java.util.List;

public class RedisConfig implements Serializable {
	

	
    public int refreshFreqSecs = 60;
    public long retryInitialDelayMs = 0;
    public double retryDelayMultiplier = 1.0;
    public long retryDelayMaxMs = 60 * 1000;
    
    public long stateUpdateIntervalMs = 2000;

    
    public int batchSize = 5 * 1024;//canale batch size

	public Integer zkPort = null;
	
	public String redisServer = "";
	
	public Integer redisPort = null;
	
	public 	String zkConnect = "";
	
	public String groupId = "";
	
	List<String> zkServersList;
	
	
    public RedisConfig(String zkConnect,Integer zkPort,List<String> zkServersList,String redisServer,Integer redisPort,String groupId) {
    	this.zkConnect = zkConnect;
    	this.redisServer = redisServer;
    	this.redisPort = redisPort;
    	this.groupId = groupId;
    	this.zkPort = zkPort;
    	this.zkServersList = zkServersList;
    }
    
    
}
