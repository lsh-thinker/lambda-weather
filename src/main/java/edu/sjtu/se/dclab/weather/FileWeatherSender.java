package edu.sjtu.se.dclab.weather;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.agent.AgentCoordinator;
import edu.sjtu.se.dclab.entity.WeatherInfo;
import edu.sjtu.se.dclab.util.FileUtil;

public class FileWeatherSender implements Runnable, WeatherSender {
	
	private Map<String, PrintWriter> writerMap = new ConcurrentHashMap<String, PrintWriter>();
	
	private final long PERIOD;
	private final TimeUnit TIME_UNIT; 

	private static final Logger LOG = LoggerFactory
			.getLogger(FileWeatherSender.class);

	private final static DateTimeFormatter DATE_FORMAT = DateTimeFormat
			.forPattern("yyyyMMddHHmm");

	private BlockingQueue<WeatherInfo> weatherInfoQueue;

	private String path;
	private Lock lock = new ReentrantLock();

	public FileWeatherSender(String path, BlockingQueue<WeatherInfo> weatherInfoQueue, long period, TimeUnit timeUnit) {
		this.path = path;
		this.weatherInfoQueue = weatherInfoQueue;
		this.PERIOD = period;
		this.TIME_UNIT = timeUnit;
		//Coordinator.scheduleService.scheduleAtFixedRate(
		//		new CleanRunnable(writerMap), period, period, timeUnit);
		
	}
	
	private class CleanRunnable implements Runnable{
		private Map<String, PrintWriter> writerMap;
		
		public CleanRunnable(Map<String, PrintWriter> writerMap){
			this.writerMap = writerMap;
		}

		@Override
		public void run() {
			for(Map.Entry<String, PrintWriter> entry: writerMap.entrySet()){
				String fileName = entry.getKey();
				DateTime date = DATE_FORMAT.parseDateTime(fileName);
				if (date.plus(TIME_UNIT.toMillis(PERIOD)).isBeforeNow()){
					PrintWriter writer = null;
					try{
						lock.lock();
						writer = writerMap.get(fileName);
						writerMap.put(fileName, null);
					}finally{
						lock.unlock();
					}
					synchronized (writer) {
						writer.close();
					}
				}
			}
		}
		
	}

	@Override
	public void send(WeatherInfo info) {
		if (info == null)
			return;
		DateTime currentDate = info.getDateTime();
		String currentFileName = DATE_FORMAT.print(currentDate);
		PrintWriter writer = getOrCreateWriter(currentFileName);
		synchronized (writer) {
			//if( writer == null ){
			//	writer = getOrCreateWriter(currentFileName);
			//}
			LOG.info("Write to file :" + path + currentFileName);	
			writer.println(info.toString());
		}
	}
	
	private PrintWriter getOrCreateWriter(String currentFileName){
		PrintWriter writer = writerMap.get(currentFileName);
		if (writer == null){
			try{
				lock.lock();
				File f = FileUtil.getOrCreateFile(path, currentFileName);
				try {
					writer = new PrintWriter(new BufferedWriter(new FileWriter(f,true)),true);
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (writerMap.get(currentFileName) == null){
					writerMap.put(currentFileName, writer);
				}else{
					writer.close();
					writer = writerMap.get(currentFileName);
				}
			}finally{
				lock.unlock();
			}
		}
		return writer;
	}

	@Override
	public void run() {
		while (true) {
			WeatherInfo info = null;
			try {
				info = weatherInfoQueue.take();
				send(info);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				LOG.error(e.getMessage());
			}
		}
	}

}
