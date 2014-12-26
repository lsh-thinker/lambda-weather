package edu.sjtu.se.dclab.storm;

import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;
import storm.kafka.trident.OpaqueTridentKafkaSpout;
import storm.kafka.trident.TransactionalTridentKafkaSpout;
import storm.kafka.trident.TridentKafkaConfig;
import storm.trident.spout.IBatchSpout;
import storm.trident.spout.IOpaquePartitionedTridentSpout;
import storm.trident.spout.IPartitionedTridentSpout;
import backtype.storm.topology.IRichSpout;
import edu.sjtu.se.dclab.util.LambdaConfigUtil;

public class KafkaSpoutFactory implements SpoutFactory {
	
	private static final Logger LOG = LoggerFactory.getLogger(KafkaSpoutFactory.class);
	
	private Map<String, Object> config;
	
	public static KafkaSpoutFactory instance = new KafkaSpoutFactory();
	
	private  KafkaSpoutFactory(){}
	
	public static KafkaSpoutFactory newFactory(){
		return instance;
	}

	@Override
	public IRichSpout getRichSpout(SpoutOptions options) {
		KafkaSpoutOptions ksOptions = (KafkaSpoutOptions)options;
		String topic = ksOptions.getTopic();
		if (ksOptions.getTopic() == null || ksOptions.getTopic().equals(""))
			throw new IllegalArgumentException("Invalid topic " + topic);
		
		LOG.info("Create KafkaSpout with topic =" + topic);
		ZkHosts zkHosts = new ZkHosts(LambdaConfigUtil.getKafkaBrokerList(config));
		SpoutConfig spoutConfig = new SpoutConfig(
				zkHosts,
				topic, 
				LambdaConfigUtil.getKafkaZookeeperRoot(config), 
				UUID.randomUUID().toString());
		KafkaSpout spout = new KafkaSpout(spoutConfig);
		return spout;
	}

	@Override
	public IBatchSpout getBatchSpout(SpoutOptions options) {
		throw new UnsupportedOperationException();
	}
	
	
	public IOpaquePartitionedTridentSpout getOpaqueTridentSpout(SpoutOptions options){
		KafkaSpoutOptions ksOptions = (KafkaSpoutOptions)options;
		String topic = ksOptions.getTopic();
		if (ksOptions.getTopic() == null || ksOptions.getTopic().equals(""))
			throw new IllegalArgumentException("Invalid topic " + topic);
		
		LOG.info("Create Kafka OpaqueTridentSpout with topic =" + topic);
		ZkHosts zkHosts = new ZkHosts(LambdaConfigUtil.getKafkaBrokerList(config));
		TridentKafkaConfig tridentKafkaConfig = new TridentKafkaConfig(
				zkHosts,
				topic,
				UUID.randomUUID().toString());
		OpaqueTridentKafkaSpout otKafkaSpout = new OpaqueTridentKafkaSpout(tridentKafkaConfig);
		return otKafkaSpout;
	}
	
	public IPartitionedTridentSpout getTransactionalTridentSpout(SpoutOptions options){
		KafkaSpoutOptions ksOptions = (KafkaSpoutOptions)options;
		String topic = ksOptions.getTopic();
		if (ksOptions.getTopic() == null || ksOptions.getTopic().equals(""))
			throw new IllegalArgumentException("Invalid topic " + topic);
		
		LOG.info("Create TransactionalTridentKafkaSpout with topic =" + topic);
		ZkHosts zkHosts = new ZkHosts(LambdaConfigUtil.getKafkaBrokerList(config));
		TridentKafkaConfig tridentKafkaConfig = new TridentKafkaConfig(
				zkHosts,
				topic,
				UUID.randomUUID().toString());
		TransactionalTridentKafkaSpout ttks = new TransactionalTridentKafkaSpout(tridentKafkaConfig);
		return ttks;
	}

		
	
	
}
