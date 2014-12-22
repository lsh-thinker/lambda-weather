package edu.sjtu.se.dclab.weather;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.entity.City;

public class FileCityLoader implements CityLoader{
	public static final Logger LOG = LoggerFactory.getLogger(FileCityLoader.class);
	
	private Map<String, City> cityMap;
	private String filePath;
	
	public FileCityLoader(String filePath){
		cityMap = new HashMap<String, City>();
		this.filePath = filePath;
	}
	
	@Override
	public Map<String, City> load(){
		if (cityMap == null) cityMap = new HashMap<String, City>();
		if (!cityMap.isEmpty())cityMap.clear();
		
		File file = new File(filePath);
		if (!file.exists()) 
			throw new IllegalArgumentException("File not exist " + filePath);
		if (file.isDirectory())
			throw new IllegalArgumentException(filePath + " is a directory!");
		
		
		BufferedReader reader = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			reader = new BufferedReader(new InputStreamReader(fis));
			while(true){
				String line = reader.readLine();
				if (line == null || line.equals("")) break;
				String[] array = line.split(",");
				City city = new City();
				city.setCityId(array[0]);
				city.setCityName(array[1]);
				city.setSecondLevelName(array[3]);
				city.setFirstLevelName(array[2]);
				cityMap.put(array[0], city);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if (reader!= null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return cityMap;
	}
	
}
