package com.ifi.ldap.input;

import java.util.Map;

public class CreateUserInput {
	private String userDN;
	private Map<String, String> userAttributes;

	public String getUserDN() {
		return userDN;
	}

	public void setUserDN(String userDN) {
		this.userDN = userDN;
	}

	public Map<String, String> getUserAttributes() {
		return userAttributes;
	}

	public void setUserAttributes(Map<String, String> userAttributes) {
		this.userAttributes = userAttributes;
	}

	public CreateUserInput() {
	}

	public CreateUserInput(String userDN, Map<String, String> userAttributes) {
		this.userDN = userDN;
		this.userAttributes = userAttributes;
	}

}
