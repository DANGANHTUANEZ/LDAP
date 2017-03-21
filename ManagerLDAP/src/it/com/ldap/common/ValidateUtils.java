package it.com.ldap.common;

public class ValidateUtils {

	public static boolean checkNotSpec(String userDN) {
		if(userDN==null||userDN.isEmpty()) return false;
		return true;
	}

	public static boolean checkValidate(String userDN) {
		return true;
	}
}
