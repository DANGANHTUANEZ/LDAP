package it.com.ldap.common;


public class Constant {
	
	public static final String STRING_OK = "OK";
	public static final String STRING_KO = "KO";
	
	public static final String ER_001 = "1";
	public static final String ER_002 = "2";
	public static final String ER_003 = "3";
	public static final String ER_004 = "4";
	public static final String ER_005 = "5";
	public static final String ER_006 = "6";
	public static final String ER_007 = "7";
	public static final String ER_008 = "8";
	public static final String ER_009 = "9";
	public static final String ER_010 = "10";
	public static final String ER_011 = "11";
	
	public static final String UserDNNotSpecified="UserDN not specified";
	public static final String UserAlreadyExists="User already exists";
	//public static final String UserAttributeKey="Attribute [attribute key] doesn’t exist";
	//public static final String UserAttributeValue="Attribute [attribute key] has not a valid value [attribute value]  (Only If applicable)";
	public static final String UserDNValidate="UserDN is not a valid Ldap DN";
	public static final String UserDoesntExist="User doesn’t exist";
	
	
	public static final String GroupDNNotSpecified="GroupDN not specified";
	public static final String GroupAlreadyExists="Group already exists";
	//public static final String GroupAttributeKey="Attribute [attribute key] doesn’t exist";
	//public static final String GroupAttributeValue="Attribute [attribute key] has not a valid value [attribute value]  (Only If applicable)";
	public static final String GroupDNValidate="UserDN is not a valid Ldap DN";
	public static final String GroupDoesntExist="User doesn’t exist";
	
	public static final String GenericError="Generic error [internal description if available]";
	
	
	public static String attributeKey(String msg){
		String[] arrStr = msg.split(":");
		System.out.println(arrStr[1]);
		return "Attribute [attribute key:= "+arrStr[1].substring(arrStr[1].indexOf("-")+1, arrStr[1].length()).trim()+"] doesn’t exist";
	}
	
	public static String attributeValue(String msg){
		String[] arrStr = msg.split(":");
		System.out.println(arrStr[1]);
		return "Attribute [attribute key:= "+arrStr[1].substring(arrStr[1].indexOf("-")+1, arrStr[1].length()).trim()+"] has not a valid value";
	}
	
	
}
