/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package basestormkafka;

import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import backtype.storm.Config;
import backtype.storm.utils.Utils;
import basestormkafka.trient.GlobalPartitionInformation;
import basestormkafka.trient.GlobalPartitionInformation.PartitioinKey;
/**
 * 获取zookeeper节点partition的相关信息
 * @author Administrator
 *
 */
public class DynamicBrokersReader {

    public static final Logger LOG = LoggerFactory.getLogger(DynamicBrokersReader.class);

    private CuratorFramework _curator;
    private String _zkPath;

    public DynamicBrokersReader(Map conf, String zkStr, String zkPath) {
        _zkPath = zkPath;
        try {
        	
        	_curator = CuratorFrameworkFactory.newClient(
                    zkStr,
                    Utils.getInt(conf.get(Config.STORM_ZOOKEEPER_SESSION_TIMEOUT)),
                    Utils.getInt(conf.get(Config.STORM_ZOOKEEPER_CONNECTION_TIMEOUT)),
                    new RetryNTimes(Utils.getInt(conf.get(Config.STORM_ZOOKEEPER_RETRY_TIMES)),
                            Utils.getInt(conf.get(Config.STORM_ZOOKEEPER_RETRY_INTERVAL))));
            _curator.start();
        } catch (Exception ex) {
            LOG.error("Couldn't connect to zookeeper", ex);
        }
    }

    /**
     * Get all partitions with their current leaders
     */
    public GlobalPartitionInformation getBrokerInfo(String topic) throws SocketTimeoutException {
      GlobalPartitionInformation globalPartitionInformation = new GlobalPartitionInformation();
        try {
            int numPartitionsForTopic = getNumPartitions(topic);
            String brokerInfoPath = brokerPath();
            for (int partition = 0; partition < numPartitionsForTopic; partition++) {
                int leader = getLeaderFor(partition,topic);
                String path = brokerInfoPath + "/" + leader;///kafka/brokers/ids/0
                try {
                	 byte[] brokerData = _curator.getData().forPath(path);
                     Broker hp = getBrokerHost(brokerData);
                     globalPartitionInformation.addPartition(new PartitioinKey(partition,topic),hp);
                } catch (org.apache.zookeeper.KeeperException.NoNodeException e) {
                    LOG.error("Node {} does not exist ", path);
                }
            }
        } catch (SocketTimeoutException e) {
					throw e;
        } catch (Exception e) {
        	String message = e.getMessage();
        	if(message.contains("org.apache.zookeeper.KeeperException$NoNodeException")){
        		return globalPartitionInformation;
        	}else{
        		throw new RuntimeException(e);
        	}
        }
        LOG.info("Read partition info from zookeeper: " + globalPartitionInformation);
        return globalPartitionInformation;
    }


    private int getNumPartitions(String topic) {
        try {
            String topicBrokersPath = partitionPath(topic);
            List<String> children = _curator.getChildren().forPath(topicBrokersPath);
            return children.size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String partitionPath(String topic) {
        return _zkPath + "/topics/" + topic + "/partitions";
    }

    public String brokerPath() {
        return _zkPath + "/ids";
    }

    /**
     * get /brokers/topics/distributedTopic/partitions/1/state
     * { "controller_epoch":4, "isr":[ 1, 0 ], "leader":1, "leader_epoch":1, "version":1 }
     *
     * @param partition
     * @return
     */
    private int getLeaderFor(long partition,String topic) {
        try {
            String topicBrokersPath = partitionPath(topic);
            byte[] hostPortData = _curator.getData().forPath(topicBrokersPath + "/" + partition + "/state");
            Map<Object, Object> value = (Map<Object, Object>) JSONValue.parse(new String(hostPortData, "UTF-8"));
            Integer leader = ((Number) value.get("leader")).intValue();
            if (leader == -1) {
                throw new RuntimeException("No leader found for partition " + partition);
            }
            return leader;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        _curator.close();
    }

    /**
     * [zk: localhost:2181(CONNECTED) 56] get /brokers/ids/0
     * { "host":"localhost", "jmx_port":9999, "port":9092, "version":1 }
     *
     * @param contents
     * @return
     */
    private Broker getBrokerHost(byte[] contents) {
        try {
            Map<Object, Object> value = (Map<Object, Object>) JSONValue.parse(new String(contents, "UTF-8"));
            String host = (String) value.get("host");
            Integer port = ((Long) value.get("port")).intValue();
            return new Broker(host, port);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
