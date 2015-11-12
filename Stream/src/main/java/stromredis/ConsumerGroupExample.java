package stromredis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import scala.collection.mutable.Stack;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
 
/**
 * 高级消费者API
 * @author Administrator
 *
 */
public class ConsumerGroupExample {
	
    public static final Logger LOG = LoggerFactory.getLogger(ConsumerGroupExample.class);
    
    private final ConsumerConnector consumer;
    private final String topic;
    private  ExecutorService executor;
 
    public  Stack<String> _contenStack  = new Stack<String>();
    
    public int MIN_SIZE = 0; 
    		
    public ConsumerGroupExample(String a_zookeeper, String a_groupId, String a_topic) {
        consumer = kafka.consumer.Consumer.createJavaConsumerConnector(
                createConsumerConfig(a_zookeeper, a_groupId));
        this.topic = a_topic;
    }
 
    public void shutdown() {
        if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
        try {
            if (!executor.awaitTermination(5000, TimeUnit.MILLISECONDS)) {
                System.out.println("Timed out waiting for consumer threads to shut down, exiting uncleanly");
            }
        } catch (InterruptedException e) {
            System.out.println("Interrupted during shutdown, exiting uncleanly");
        }
   }
 
    public void run(int a_numThreads) {
    	   Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
           topicCountMap.put(topic, new Integer(a_numThreads));
           Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
           List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
    
           // now launch all the threads
           //
           executor = Executors.newFixedThreadPool(a_numThreads);
    
           // now create an object to consume the messages
           //
           int threadNumber = 0;
           for (final KafkaStream stream : streams) {
               executor.submit(new Consumer(stream, threadNumber,_contenStack));
               threadNumber++;
           }
    }
 
    private static ConsumerConfig createConsumerConfig(String a_zookeeper, String a_groupId) {
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");
        props.put("auto.offset.reset", "smallest");
        return new ConsumerConfig(props);
    }
    
    
    
    
    /**
     * 消费一个消息内容
     * @return
     */
    public String  consumer(){
    	synchronized (_contenStack) {
			return _contenStack.pop();
		}
    } 
    
 
    public static void main(String[] args) {
        String zooKeeper = "192.168.33.14:2181/kafka";
        String groupId = "test-consumer-group-2";
        String topic = "my-replicated-topic";
        int threads = 1;
// 	   String[] params ={"2","my-replicated-topic","1","192.168.33.14","9092"};

        ConsumerGroupExample example = new ConsumerGroupExample(zooKeeper, groupId, topic);
        example.run(threads);
 
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ie) {
 
        }
//        example.shutdown();
    }
}
