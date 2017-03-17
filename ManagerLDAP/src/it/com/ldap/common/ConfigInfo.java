package it.com.ldap.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
/**
 * 
 * @author vtnghia
 *
 */
public class ConfigInfo {
	Map<String, String> propertyMap = null;
	
	private static ConfigInfo instance = null;
	
	synchronized public static ConfigInfo getInstance() {
		if (instance == null) {
			instance = new ConfigInfo();
		}
		return instance;
	}
	
	private ConfigInfo() {
		String[] pathToConfigFile = {"./config/adaptor.properties", "../config/adaptor.properties", "../../config/adaptor.properties"};
		String configFile=null;
		for (String path : pathToConfigFile) {
			if ((new File(path)).exists()) {
				configFile = path;
				break;
			}
		}
		try {
			Properties properties = new Properties();
			properties.load(new FileInputStream(configFile));
			Enumeration<Object> keyList = properties.keys();
			propertyMap = new HashMap<String, String>();
			String key = null;
			String value = null;
			while(keyList.hasMoreElements()){
				key = keyList.nextElement().toString();
				//System.out.println("key " + key);
				value = properties.getProperty(key.toString());
				propertyMap.put(key, value);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getProperty(String key){
		return propertyMap.get(key);
	}
}
