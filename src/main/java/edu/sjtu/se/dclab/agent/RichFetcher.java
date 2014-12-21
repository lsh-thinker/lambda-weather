package edu.sjtu.se.dclab.agent;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RichFetcher<T> implements Fetcher<T>{
	private static final Logger LOG = LoggerFactory
			.getLogger(RichFetcher.class);
	
	private BlockingQueue<T> outputQueue;
	protected Map<String,Object> config;
	
	@Override
	public void run() {
		T t = fetch();
		if (t != null){
			try {
				outputQueue.put(t);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	protected void setConfig(final Map<String,Object> config) {
		this.config = config;
	}
	
	public void setOutputQueue(final BlockingQueue<T> outputQueue) {
		this.outputQueue = outputQueue;
	}
	
}
