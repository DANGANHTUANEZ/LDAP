package com.ifi.ldap.entity;

import java.util.List;

public class LdapAttribute {
	private String key;
	private List<String> values;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public List<String> getValues() {
		return values;
	}

	public void setValues(List<String> values) {
		this.values = values;
	}

	public LdapAttribute() {
	}

	public LdapAttribute(String key, List<String> values) {
		this.key = key;
		this.values = values;
	}

}
