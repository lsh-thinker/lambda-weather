package edu.sjtu.se.dclab.agent;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RichProcessor<T1, T2> implements Processor<T1, T2> {

	private static final Logger LOG = LoggerFactory
			.getLogger(RichProcessor.class);

	protected Map<String,Object> config;
	private BlockingQueue<T1> inputQueue;
	private BlockingQueue<T2> outputQueue;
	
	@Override
	public void run() {
		T1 t1;
		while (true) {
			try {
				t1 = inputQueue.take();
				if (t1 == null)
					continue;
				T2 t2 = process(t1);
				outputQueue.put(t2);
			} catch (InterruptedException e) {
				e.printStackTrace();
				LOG.info(e.getMessage());
				continue;
			}
		}
	}
	
	void setInputQueue(BlockingQueue<T1> inputQueue) {
		this.inputQueue = inputQueue;
	}

	void setOutputQueue(BlockingQueue<T2> outputQueue) {
		this.outputQueue = outputQueue;
	}

	void setConfig(final Map<String,Object> config) {
		this.config = config;
	}
	
	

}
