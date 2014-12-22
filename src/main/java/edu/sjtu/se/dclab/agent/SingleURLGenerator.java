package edu.sjtu.se.dclab.agent;

import java.net.MalformedURLException;
import java.net.URL;

public class SingleURLGenerator implements URLGenerator{
	
	private URL url;
	
	public SingleURLGenerator(String urlString){
		try {
			url =  new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	@Override
	public URL getUrl() {
		return url;
	}
	
}
