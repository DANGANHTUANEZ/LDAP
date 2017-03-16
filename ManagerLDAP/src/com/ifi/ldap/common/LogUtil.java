package com.ifi.ldap.common;

import org.apache.log4j.Logger;

/**
 * 
 * @author vtnghia
 *
 */
public class LogUtil {

	private static String LOGGER_NAME = "LdapAdaptor";
	public static Logger ADAPTOR_LOG = null;

	static {
		ADAPTOR_LOG = Logger.getLogger(LOGGER_NAME);
	}
	
	public static void error(String message, Exception ex) {
		ADAPTOR_LOG.error(message, ex);
	}
	public static void error(Exception ex) {
		error("", ex);
	}
	
	public static void info(String msg) {
		ADAPTOR_LOG.info(msg);
	}
	
	public static void error(String msg) {
		ADAPTOR_LOG.error(msg);
	}
	

//	public static void main(String[] args) {
//		try {
//			String test = "";
//			test.substring(0, 3);
//		} catch (Exception ex) {
////			ex.printStackTrace();
//			ADAPTOR_LOG.error("Something wrong");
//			ADAPTOR_LOG.error(ex);
//		}finally{
//			System.out.println("File name = "+ ADAPTOR_LOG);
//		}
//	}

}
