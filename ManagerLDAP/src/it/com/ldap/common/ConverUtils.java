package it.com.ldap.common;

import java.util.ArrayList;
import java.util.List;

import it.com.ldap.entity.LdapAttribute;
import it.com.ldap.input.User;

public class ConverUtils {
	
	public static User converStringtoUserInput(String s){
		User user=new User();
		List<LdapAttribute> list= new ArrayList<LdapAttribute>();
		s=s.substring(1, s.length()-1);
		String[]elementsAttr=s.split("=");
		for (int i = 1; i < elementsAttr.length; i++) {
			LdapAttribute attribute =converStringtoLdapAttribute(i==elementsAttr.length-1?elementsAttr[i]:elementsAttr[i].substring(0, elementsAttr[i].lastIndexOf(",")));
			if(!"cn".equals(attribute.getKey())){
				list.add(attribute);
			}else{
				user.setUserDN(attribute.getValues().get(attribute.getValues().size()-1));
			}
		}
		user.setListAttr(list);
		return user;
	}
	
	public static LdapAttribute converStringtoLdapAttribute(String s){
		LdapAttribute ldapAttribute=new LdapAttribute();
		List<String> listString = new ArrayList<String>();
		String[]elementsAttr=s.split(":");
		ldapAttribute.setKey(elementsAttr[0]);
		String[] subElementAttr=elementsAttr[1].split(",");
		for (int i = 0; i < subElementAttr.length; i++) {
			listString.add(subElementAttr[i]);
		}
		ldapAttribute.setValues(listString);
		return ldapAttribute;
	}
}
