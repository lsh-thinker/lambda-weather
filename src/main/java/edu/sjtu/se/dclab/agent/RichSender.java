package edu.sjtu.se.dclab.agent;

import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RichSender<T>  implements Sender<T>{

	private static final Logger LOG = LoggerFactory.getLogger(RichFetcher.class);
	
	private BlockingQueue<T> inputQueue;
	protected Map<String,Object> config;
	
	@Override
	public void run() {
		T t = null;
		while (true) {
			try {
				t = inputQueue.take();
				if (t == null)
					continue;
				send(t);
			} catch (InterruptedException e) {
				e.printStackTrace();
				LOG.info(e.getMessage());
				continue;
			}
		}
	}

	void setInputQueue(final BlockingQueue<T> inputQueue) {
		this.inputQueue = inputQueue;
	}

	void setConfig(final Map<String,Object> config) {
		this.config = config;
	}
	
}
