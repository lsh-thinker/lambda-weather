package edu.sjtu.se.dclab.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class Utils {
	
	public static List<URL> findResources(String name) {
		try {
			Enumeration<URL> resources = Thread.currentThread()
					.getContextClassLoader().getResources(name);
			List<URL> ret = new ArrayList<URL>();
			while (resources.hasMoreElements()) {
				ret.add(resources.nextElement());
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static Map<String,Object> readLambdaConfig() {
		Map<String,Object> ret = readDefaultConfig();
		String confFile = System.getProperty("lambda.conf.file");
		Map<String,Object> lambdaConfig;
		if (confFile == null || confFile.equals("")) {
			lambdaConfig = findAndReadConfigFile("lambda.yaml", false);
		} else {
			lambdaConfig = findAndReadConfigFile(confFile, true);
		}
		ret.putAll(lambdaConfig);
//		ret.putAll(readCommandLineOpts());
//		replaceLocalDir(ret);
		return ret;
	}
	
	public static Map<String,Object> readDefaultConfig() {
		return findAndReadConfigFile("system.yaml", true);
	}
	
	public static Map<String,Object> findAndReadConfigFile(String name, boolean mustExist) {
		try {
			HashSet<URL> resources = new HashSet<URL>(findResources(name));
			if (resources.isEmpty()) {
				if (mustExist)
					throw new RuntimeException(
							"Could not find config file on classpath " + name);
				else
					return new HashMap<String,Object>();
			}
			if (resources.size() > 1) {
				throw new RuntimeException(
						"Found multiple "
								+ name
								+ " resources."
								+ resources);
			}
			URL resource = resources.iterator().next();
			Yaml yaml = new Yaml();
			Map<String,Object> ret = (Map<String,Object>) yaml.load(new InputStreamReader(resource.openStream()));
			if (ret == null)
				ret = new HashMap<String,Object>();
			return new HashMap<String,Object>(ret);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	

}
