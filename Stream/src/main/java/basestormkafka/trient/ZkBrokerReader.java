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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import basestormkafka.DynamicBrokersReader;
import basestormkafka.ZkHosts;



public class ZkBrokerReader implements IBrokerReader {

	public static final Logger LOG = LoggerFactory.getLogger(ZkBrokerReader.class);

	GlobalPartitionInformation cachedBrokers;
	DynamicBrokersReader reader;
	long lastRefreshTimeMs;
	String _topic;

	long refreshMillis;

	public ZkBrokerReader(Map conf, String topic, ZkHosts hosts) {
		try {
			this._topic = topic;
			reader = new DynamicBrokersReader(conf, hosts.brokerZkStr, hosts.brokerZkPath);
			cachedBrokers = reader.getBrokerInfo(topic);
			lastRefreshTimeMs = System.currentTimeMillis();
			refreshMillis = hosts.refreshFreqSecs * 1000L;
		} catch (java.net.SocketTimeoutException e) {
			LOG.warn("Failed to update brokers", e);
		}

	}

	@Override
	public GlobalPartitionInformation getCurrentBrokers() {
		long currTime = System.currentTimeMillis();
		if (currTime > lastRefreshTimeMs + refreshMillis) {
			try {
				LOG.info("brokers need refreshing because " + refreshMillis + "ms have expired");
				cachedBrokers = reader.getBrokerInfo(_topic);
				lastRefreshTimeMs = currTime;
			} catch (java.net.SocketTimeoutException e) {
				LOG.warn("Failed to update brokers", e);
			}
		}
		return cachedBrokers;
	}

	@Override
	public void close() {
		reader.close();
	}
}
