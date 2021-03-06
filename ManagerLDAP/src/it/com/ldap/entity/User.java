package it.com.ldap.entity;

import java.util.List;

public class User {
	private String userDN;
	private List<LdapAttribute> listAttr;

	public String getUserDN() {
		return userDN;
	}

	public void setUserDN(String userDN) {
		this.userDN = userDN;
	}

	public List<LdapAttribute> getListAttr() {
		return listAttr;
	}

	public void setListAttr(List<LdapAttribute> listAttr) {
		this.listAttr = listAttr;
	}

	public User() {
	}

	public User(String userDN, List<LdapAttribute> listAttr) {
		this.userDN = userDN;
		this.listAttr = listAttr;
	}

}
