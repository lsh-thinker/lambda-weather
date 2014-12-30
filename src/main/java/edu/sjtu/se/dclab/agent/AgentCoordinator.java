package edu.sjtu.se.dclab.agent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.util.Utils;

public class AgentCoordinator<T1, T2> implements Coordinator<T1,T2>{
	public static final Logger LOG = LoggerFactory.getLogger(AgentCoordinator.class);

	static final ExecutorService executorService = Executors.newFixedThreadPool(3);
	static final ScheduledExecutorService scheduleService =  Executors.newScheduledThreadPool(3);;

	private List<RichFetcher<T1>> fetcherList;
	private List<RichProcessor<T1,T2>> processorList;
	private List<RichSender<T2>> senderList;

	public AgentCoordinator() {
		fetcherList = new ArrayList<RichFetcher<T1>>();
		processorList = new ArrayList<RichProcessor<T1, T2>>();
		senderList = new ArrayList<RichSender<T2>>();
	}
	
	private void init(){
		Map<String,Object> config = loadConf();
		
		BlockingQueue<T1> queue1 = new LinkedBlockingQueue<T1>();
		BlockingQueue<T2> queue2 = new LinkedBlockingQueue<T2>(); 
		
		for(RichFetcher<T1> fetcher : fetcherList){
			fetcher.setOutputQueue(queue1);
			fetcher.setConfig(config);
			fetcher.init();
		}
		for(RichProcessor<T1, T2> processor: processorList){
			processor.setInputQueue(queue1);
			processor.setOutputQueue(queue2);
			processor.setConfig(config);
			processor.init();
		}
		for(RichSender<T2> sender : senderList){
			sender.setInputQueue(queue2);
			sender.setConfig(config);
			sender.init();
		}
	}
	
	public void launch() {
		init();
		service();
	}
	
	private Map<String,Object> loadConf(){
		return Utils.readLambdaConfig();
	}

	private void service() {
		for(RichFetcher<T1> fetcher : fetcherList){
			scheduleService.scheduleAtFixedRate(
					fetcher, 10, 10, TimeUnit.SECONDS);
		}
		for(RichProcessor<T1, T2> processor: processorList){
			executorService.execute(processor);
		}
		for(RichSender<T2> sender : senderList){
			executorService.execute(sender);
		}
	}
	
	public void addFetcher(RichFetcher<T1> fetcher){
		fetcherList.add(fetcher);
	}
	
	public void addProcessor(RichProcessor<T1,T2> processor){
		processorList.add(processor);
	}
	
	public void addSender(RichSender<T2> sender){
		senderList.add(sender);
	}

}
