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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import kafka.javaapi.consumer.SimpleConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basestormkafka.trient.GlobalPartitionInformation.PartitioinKey;
import basestormkafka.trient.IBrokerReader;


public class DynamicPartitionConnections {

    public static final Logger LOG = LoggerFactory.getLogger(DynamicPartitionConnections.class);

    static class ConnectionInfo {
        SimpleConsumer consumer;
        Set<Integer> partitions = new HashSet();

        public ConnectionInfo(SimpleConsumer consumer) {
            this.consumer = consumer;
        }
    }

    Map<Broker, ConnectionInfo> _connections = new HashMap();
    KafkaConfig _config;
    IBrokerReader _reader;

    public DynamicPartitionConnections(KafkaConfig config, IBrokerReader brokerReader) {
        _config = config;
        _reader = brokerReader;
    }

    public SimpleConsumer register(Partition partition) {
    	 Broker broker = partition.host;
//        Broker broker = _reader.getCurrentBrokers().getBrokerFor(new PartitioinKey( partition.partition,partition.topic));
        return register(broker, partition.partition);
    }
    

    public SimpleConsumer register(Broker host, int partition) {
    	String clientId = "333333";
        if (!_connections.containsKey(host)) {
            _connections.put(host, new ConnectionInfo(new SimpleConsumer(host.host, host.port, _config.socketTimeoutMs, _config.bufferSizeBytes, _config.clientId)));
        }
        ConnectionInfo info = _connections.get(host);
        info.partitions.add(partition);
        return info.consumer;
    }

    public SimpleConsumer getConnection(Partition partition) {
        ConnectionInfo info = _connections.get(partition.host);
        if (info != null) {
            return info.consumer;
        }
        return null;
    }

    public void unregister(Broker port, int partition) {
        ConnectionInfo info = _connections.get(port);
        info.partitions.remove(partition);
        if (info.partitions.isEmpty()) {
            info.consumer.close();
            _connections.remove(port);
        }
    }

    public void unregister(Partition partition) {
        unregister(partition.host, partition.partition);
    }

    public void clear() {
        for (ConnectionInfo info : _connections.values()) {
            info.consumer.close();
        }
        _connections.clear();
    }
}
