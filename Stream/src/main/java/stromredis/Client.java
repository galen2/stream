package stromredis;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import basestormkafka.KafkaSpouts;
import basestormkafka.SpoutConfig;
import basestormkafka.ZkHosts;
import basestormkafka.trient.TridentKafkaState;


public class Client {
	    
    private static String kafkaTopic = "my-replicated-topic";

    
	    private static String zkHost = "192.168.33.14";
	    
	    private static int zkPort = 2181;
	    
	    private static String brokerList = "192.168.33.14:9032,192.168.33.14:9031";
	    
	    private static StormTopology buildTopology() {
	    	
	        CanelSpout spout =  getCanalSpout();
			TopologyBuilder builder = new TopologyBuilder();
			builder.setSpout("spout1", spout);
			builder.setBolt("2_bolt", new CanalBolt(), 1).shuffleGrouping("spout1");
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
//	        new KafkaTestBroker();
	        LocalCluster cluster = new LocalCluster();
	        cluster.submitTopology("wordCounter", conf, buildTopology());
	      /*Thread.sleep(60 * 1000);
	        cluster.killTopology("wordCounter");
	        cluster.shutdown();*/
	    }

	    private  static Config getConfig() {
	        Config conf = new Config();
	        Properties props = new Properties();
	        props.put("metadata.broker.list",brokerList);
	        props.put("request.required.acks", "1");
	        props.put("serializer.class", "kafka.serializer.StringEncoder");
	        conf.put(TridentKafkaState.KAFKA_BROKER_PROPERTIES, props);
	        conf.setDebug(true);
	        conf.setNumWorkers(1);
	        return conf;
	    }

	    /**
	     * 创建spout对象
	     * @return
	     */
	    public static KafkaSpouts getKafkaSpout(){
	    	ZkHosts zksHost = new ZkHosts(zkStr, "/kafka/brokers");
	        List<String> zkServers = new LinkedList<String>();
	        zkServers.add(zkHost);
			SpoutConfig spoutConf = new SpoutConfig(zksHost, kafkaTopic, "/kafka","1",zkServers,zkPort);
			KafkaSpouts spout = new KafkaSpouts(spoutConf);
			return spout;
	    }

}
