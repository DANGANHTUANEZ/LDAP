package com.ifi.ldap.common;

public class ValidateUtils {

	public static boolean userDNNotSpec(String userDN) {
		if(userDN==null||userDN.isEmpty()) return false;
		return true;
	}

	public static boolean userDNValidate(String userDN) {
		return true;
	}

	public static boolean attributeValueValidate(String attr) {
		return true;
	}

	public static boolean attributeKeyValidate(String attr) {
		return true;
	}

}
