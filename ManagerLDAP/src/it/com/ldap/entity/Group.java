package it.com.ldap.entity;

import java.util.List;

public class Group {
	private String groupDN;
	private List<LdapAttribute> listAttr;

	public String getGroupDN() {
		return groupDN;
	}

	public void setGroupDN(String groupDN) {
		this.groupDN = groupDN;
	}

	public List<LdapAttribute> getListAttr() {
		return listAttr;
	}

	public void setListAttr(List<LdapAttribute> listAttr) {
		this.listAttr = listAttr;
	}

	public Group() {
	}

	public Group(String groupDN, List<LdapAttribute> listAttr) {
		this.groupDN = groupDN;
		this.listAttr = listAttr;
	}

}
