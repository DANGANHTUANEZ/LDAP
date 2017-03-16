package com.ifi.ldap.input;

import java.util.List;

import com.ifi.ldap.entity.LdapAttribute;

public class CreateUserInput {
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

	public CreateUserInput() {
	}

	public CreateUserInput(String userDN, List<LdapAttribute> listAttr) {
		this.userDN = userDN;
		this.listAttr = listAttr;
	}

}
