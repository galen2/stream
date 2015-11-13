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
package basestormkafka.trient;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


import basestormkafka.Broker;
import basestormkafka.Partition;

import com.google.common.base.Objects;


//public class GlobalPartitionInformation implements Iterable<Partition>, Serializable {
public class GlobalPartitionInformation implements  Serializable {

    private Map<PartitioinKey, Broker> partitionMap;

    public GlobalPartitionInformation() {
        partitionMap = new HashMap<PartitioinKey, Broker>();
    }

    public void addPartition(PartitioinKey partitionId, Broker broker) {
        partitionMap.put(partitionId, broker);
    }

    @Override
    public String toString() {
        return "GlobalPartitionInformation{" +
                "partitionMap=" + partitionMap +
                '}';
    }

    public Broker getBrokerFor(PartitioinKey partitionId) {
        return partitionMap.get(partitionId);
    }

    public boolean empty(){
    	return partitionMap.size()==0;
    }
    public List<Partition> getOrderedPartitions() {
        List<Partition> partitions = new LinkedList<Partition>();
        for (Map.Entry<PartitioinKey, Broker> partition : partitionMap.entrySet()) {
            partitions.add(new Partition(partition.getValue(),partition.getKey()._topic, partition.getKey()._partitionId));
        }
        return partitions;
    }

  /*  @Override
    public Iterator<Partition> iterator() {
        final Iterator<Map.Entry<PartitioinKey, Broker>> iterator = partitionMap.entrySet().iterator();

        return new Iterator<Partition>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Partition next() {
                Map.Entry<PartitioinKey, Broker> next = iterator.next();
                return new Partition(next.getValue(), next.getKey()._topic,next.getKey()._partitionId);
            }

            @Override
            public void remove() {
                iterator.remove();
            }
        };
    }*/
  
    @Override
    public int hashCode() {
        return Objects.hashCode(partitionMap);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final GlobalPartitionInformation other = (GlobalPartitionInformation) obj;
        return Objects.equal(this.partitionMap, other.partitionMap);
    }
    
    
    
    public static class PartitioinKey{
    	public Integer _partitionId;
    	public String _topic;
    	public PartitioinKey(Integer partition,String topic){
    		this._partitionId = partition;
    		this._topic = topic;
    	}
   }
    
    
}
