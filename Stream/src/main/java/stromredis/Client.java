package stromredis;
import java.util.LinkedList;
import java.util.List;

import org.apache.storm.redis.common.config.JedisPoolConfig;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import basestormkafka.KafkaSpouts;
import basestormkafka.SpoutConfig;
import basestormkafka.ZkHosts;


public class Client {
	
    private static String[] topics=new String[]{"my-replicated-topic","customer"};
    private static String zkHost = "192.168.33.14";
    private static int zkPort = 2181;
    private static String zkStr  = zkHost.concat(":").concat(zkPort+"");
    private static String zkRoot = "/kafka";
    private static String zkPath = "/kafka/brokers";
    private static String groupId=  "lq_stream_customer";
    
    private static final String TEST_REDIS_HOST = "192.168.32.30";
    private static final String TEST_REDIS_PORT = "6379";
    
    private static StormTopology buildTopology(String[] args) {
        KafkaSpouts spout =  getKafkaSpout();
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("spout1", spout);
		builder.setBolt("2_bolt", SetRedisStoreLqBolt(args), 1).shuffleGrouping("spout1");
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
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("wordCounter", conf, buildTopology(args));
      /*Thread.sleep(60 * 1000);
        cluster.killTopology("wordCounter");
        cluster.shutdown();*/
    }

    
    private static RedisStoreLqBolt SetRedisStoreLqBolt(String[] args) {
    	args = new String[]{TEST_REDIS_HOST,TEST_REDIS_PORT};
    	RedisStoreLqCustomerMapper storeMapper = new RedisStoreLqCustomerMapper();
        String host = args[0];
        int port = Integer.parseInt(args[1]);
    	JedisPoolConfig poolConfig = new JedisPoolConfig.Builder()
         					.setHost(host).setPort(port).build();
    	RedisStoreLqBolt bolt = new RedisStoreLqBolt(poolConfig, storeMapper);
        return bolt;
    }
    
    /**
     * 保证kafkabolt生产消息使用消费使用
     * @return
     */
    private  static Config getConfig() {
        Config conf = new Config();
     /* Properties props = new Properties();
        props.put("metadata.broker.list",kafkaHostConnection);
        props.put("request.required.acks", "1");
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        conf.put(TridentKafkaState.KAFKA_BROKER_PROPERTIES, props);
        conf.setDebug(true);*/
        conf.setNumWorkers(1);
        return conf;
    }
    /**
     * 创建spout对象
     * @return
     */
    public static KafkaSpouts getKafkaSpout(){
    	ZkHosts zksHost = new ZkHosts(zkStr, zkPath);
        List<String> zkServers = new LinkedList<String>();
        zkServers.add(zkHost);
        
		SpoutConfig spoutConf = new SpoutConfig(zksHost, topics,zkRoot,zkServers,zkPort,groupId);
		KafkaSpouts spout = new KafkaSpouts(spoutConf);
		return spout;
    }

    
}
