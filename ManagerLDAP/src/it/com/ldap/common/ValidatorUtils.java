package it.com.ldap.common;

public class ValidatorUtils {

	public static boolean checkNotSpec(String userDN) {
		if (userDN == null || userDN.isEmpty())
			return false;
		return true;
	}

	public static boolean checkValidate(String userDN) {
		String pattern = "^[A-Za-z0-9_.]+$";
		if (userDN.matches(pattern))
			return true;
		return false;
	}

	public static String attributeKey(String msg) {
		String[] arrStr = msg.split(":");
		System.out.println(arrStr[1]);
		return "Attribute [attribute key:= "
				+ arrStr[1].substring(arrStr[1].indexOf("-") + 1, arrStr[1].length()).trim() + "] doesn’t exist";
	}

	public static String attributeValue(String msg) {
		String[] arrStr = msg.split(":");
		System.out.println(arrStr[1]);
		return "Attribute [attribute key:= "
				+ arrStr[1].substring(arrStr[1].indexOf("-") + 1, arrStr[1].length()).trim()
				+ "] has not a valid value";
	}
}
