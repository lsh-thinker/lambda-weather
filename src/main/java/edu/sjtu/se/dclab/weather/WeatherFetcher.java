package edu.sjtu.se.dclab.weather;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.entity.WeatherInfo;

public class WeatherFetcher implements Runnable {
	private static final Logger LOG = LoggerFactory
			.getLogger(WeatherFetcher.class);

	private String host;
	private String protocol;
	private BlockingQueue<String> jsonInfoQueue;

	public WeatherFetcher(String host, String protocol,
			BlockingQueue<String> jsonInfoQueue) {
		this.host = host;
		this.protocol = protocol;
		this.jsonInfoQueue = jsonInfoQueue;
		LOG.info("Start Whether fetcher...");
	}

	public String getWhetherInfo(String cityId) throws IOException {
		StringBuilder builder = new StringBuilder("/data/sk/");
		builder.append(cityId);
		builder.append(".html");

		URL url = new URL(protocol, host, builder.toString());
		LOG.info("Get " + url.getPath());

		URLConnection connection = url.openConnection();
		HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
		httpUrlConnection.setRequestMethod("GET");
		httpUrlConnection.connect();

		InputStream is = httpUrlConnection.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		StringBuilder response = new StringBuilder();
		String temp;
		try {
			while ((temp = reader.readLine()) != null) {
				response.append(temp);
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
			if (httpUrlConnection != null) {
				httpUrlConnection.disconnect();
			}
		}

		LOG.info("Response:" + response.toString());
		return response.toString();
	}

	public Map<String, WeatherInfo> getWhetherInfos(List<String> cityIds) {
		Map<String, WeatherInfo> whetherInfos = new HashMap<String, WeatherInfo>();
		if (cityIds == null || cityIds.size() == 0)
			return null;
		for (String cityId : cityIds) {
			String response = null;
			try {
				response = getWhetherInfo(cityId);
				JSONObject obj = (JSONObject) JSONValue.parse(response);
				WeatherInfo info = new WeatherInfo(obj);
				whetherInfos.put(cityId, info);
			} catch (IOException e) {
				e.printStackTrace();
				LOG.debug("Error for requiring whether of city id:" + cityId);
			}
		}
		return whetherInfos;
	}

	@Override
	public void run() {
		List<String> cityIdList = new ArrayList<String>();
		cityIdList.add("101281501");
		cityIdList.add("101010100");
		for(String cityId: cityIdList){
			try {
				LOG.info("Fetching WeatherInfo for city " + cityId);
				String weatherInfo = getWhetherInfo(cityId);
				jsonInfoQueue.put(weatherInfo);
			} catch (IOException e) {
				e.printStackTrace();
				LOG.debug("Error for requiring weather of city id:" + cityId);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}
}
