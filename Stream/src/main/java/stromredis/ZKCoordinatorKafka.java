package stromredis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ZKCoordinatorKafka {
	
    public static final Logger LOG = LoggerFactory.getLogger(ZKCoordinatorKafka.class);

    Map<String, TopicManager> _managers = new HashMap();

	public RedisConfig canalConfig = null;
	
	public ZKStateKafka _state = null;
	
    int _refreshFreqMs;
    
    Long _lastRefreshTime = null;

    List<TopicManager> _cachedList;
	
	public ZKCoordinatorKafka(RedisConfig canalConfig,ZKStateKafka state){
		this.canalConfig = canalConfig; 
		this._state = state;
        _refreshFreqMs = canalConfig.refreshFreqSecs * 1000;
	}

    public List<TopicManager> getMyManagedIndestance() {
        if (_lastRefreshTime == null || (System.currentTimeMillis() - _lastRefreshTime) > _refreshFreqMs) {
            refresh();
            _lastRefreshTime = System.currentTimeMillis();
        }
        return _cachedList;
    }
    
    public void refresh() {
        try {
            LOG.info("Refreshing canal manager connections");
            
            List<String> mine = _state.forPath(ZKKafka.ZK_TOPIC);
            
            Set<String> curr = _managers.keySet();
            Set<String> newDestions = new HashSet<String>(mine);
            newDestions.removeAll(curr);
            
            
            Set<String> deletedDestions = new HashSet<String>(curr);
            deletedDestions.removeAll(mine);
            
            
            LOG.info("Deleted destinations managers: " + deletedDestions.toString());

            for(String dest:deletedDestions){
            	TopicManager man = _managers.remove(dest);
            }
            
            
            LOG.info("New destination managers: " + newDestions.toString());
            
            for(String topic:newDestions){
            	TopicManager man = new TopicManager(canalConfig, _state, topic);
                _managers.put(topic, man);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        _cachedList = new ArrayList<TopicManager>(_managers.values());
        LOG.info("Finished refreshing");
    }

    
    public TopicManager getManager(String destination) {
        return _managers.get(destination);
    }
}
