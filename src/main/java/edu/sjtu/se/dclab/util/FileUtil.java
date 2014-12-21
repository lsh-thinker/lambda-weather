package edu.sjtu.se.dclab.util;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.sjtu.se.dclab.weather.FileWeatherSender;

public final class FileUtil {
	
	private static final Logger LOG = LoggerFactory
			.getLogger(FileWeatherSender.class);
	
	public static File getOrCreateFile(String filePath, String fileName){
		File f = new File(filePath, fileName);
		if (f.isDirectory()){
			throw new IllegalArgumentException(filePath + fileName + " is a directory");
		}
		if (!f.exists()){
			try {
				f.createNewFile();
			} catch (IOException e) {
				LOG.info("Error creating file " + filePath + fileName);
				e.printStackTrace();
			}
		}
		return f;
	}
}
