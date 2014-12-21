package edu.sjtu.se.dclab.agent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpFetcher extends RichFetcher<String> {


	private URLGenerator generator;

	public HttpFetcher(URLGenerator generator) {
		this.generator = generator;
	}

	private static final Logger LOG = LoggerFactory
			.getLogger(HttpFetcher.class);

	@Override
	public void init() {

	}

	@Override
	public String fetch() {
		URL url = generator.getUrl();
		if (url == null)
			throw new NullPointerException("URL is null");
		try {
			URLConnection connection = url.openConnection();
			HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
			httpUrlConnection.setRequestMethod("GET");
			httpUrlConnection.connect();

			InputStream is = httpUrlConnection.getInputStream();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(is));

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
			
		} catch (IOException e) {
			LOG.error(e.getMessage());
		}
		
		return null;
	}

}
