package stromredis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import scala.collection.mutable.Stack;
 
public class Consumer implements Runnable {
    public static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    private KafkaStream m_stream;
    private int m_threadNumber;
    
    Stack<String> _contenStack = null;

    public int MAX_SIZE = 50000;
    public Consumer(KafkaStream a_stream, int a_threadNumber,Stack<String> contentStack) {
        m_threadNumber = a_threadNumber;
        m_stream = a_stream;
        this._contenStack = contentStack;
    }
 
    public void run() {
    	ConsumerIterator<byte[], byte[]> it = m_stream.iterator();
    		while (it.hasNext()){
    			synchronized (_contenStack) {
    				while(_contenStack.size() > MAX_SIZE){
    					try {
							_contenStack.wait();
						} catch (InterruptedException e) {
							LOG.warn("Thread is deisrupted", e);
						}
    				}
				}
    			
    			String content = new String(it.next().message());
    			_contenStack.push(content);
    			LOG.info("produce message ".concat(content));
    		}
    }
}