package basestormkafka;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import basestormkafka.trient.TridentKafkaState;


public class TridentKafkaTopologyLocal {
	
    private static String kafkaTopic = "my-replicated-topic";
//    private static int kafkaPartion = 1;
    
    private static String zkHost = "192.168.33.14";
    private static int zkPort = 2181;
    
    private static String zkStr  = zkHost.concat(":").concat(zkPort+"");
    
    private static String kafkaHost = "192.168.33.14";
    private static int kafkaPort = 9092;
//    
    private static String kafkaHostConnection = kafkaHost.concat(":").concat(kafkaPort+"");
    
    private static StormTopology buildTopology() {
        KafkaSpouts spout =  getKafkaSpout();
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout1", spout);
//		builder.setBolt("2_bolt", new KafkaBolt(), 1).shuffleGrouping("spout1");
		Config conf = new Config();
        conf.setDebug(true);
        conf.setNumWorkers(1);
        return builder.createTopology();
    }

    /**
     * To run this topology ensure you have a kafka broker running and provide connection string to broker as argument.
     * Create a topic test with command line,
     * kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partition 1 --topic test
     *
     * run this program and run the kafka consumer:
     * kafka-console-consumer.sh --zookeeper localhost:2181 --topic test --from-beginning
     *
     * you should see the messages flowing through.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Config conf = getConfig();
        
//        new KafkaTestBroker();
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("wordCounter", conf, buildTopology());
      /*Thread.sleep(60 * 1000);
        cluster.killTopology("wordCounter");
        cluster.shutdown();*/
    }

    /**
     * 保证kafkabolt生产消息使用消费使用
     * @return
     */
    private  static Config getConfig() {
        Config conf = new Config();
        Properties props = new Properties();
        props.put("metadata.broker.list",kafkaHostConnection);
        props.put("request.required.acks", "1");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        conf.put(TridentKafkaState.KAFKA_BROKER_PROPERTIES, props);
        
        conf.put("topic", kafkaTopic);
        conf.setDebug(true);
        conf.setNumWorkers(1);
        return conf;
    }

    /**
     * 创建spout对象
     * @return
     */
    public static KafkaSpouts getKafkaSpout(){
//		GlobalPartitionInformation globalPartitionInformation = new GlobalPartitionInformation();
//	    globalPartitionInformation.addPartition(kafkaPartion, Broker.fromString(kafkaHostConnection));
//        BrokerHosts brokerHosts = new StaticHosts(globalPartitionInformation);
    	ZkHosts zksHost = new ZkHosts(zkStr, "/kafka/brokers");
        List<String> zkServers = new LinkedList<String>();
        zkServers.add(zkHost);
		SpoutConfig spoutConf = new SpoutConfig(zksHost, kafkaTopic, "/kafka","1",zkServers,zkPort);
		KafkaSpouts spout = new KafkaSpouts(spoutConf);
		return spout;
    }
}
