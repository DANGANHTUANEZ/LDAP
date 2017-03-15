package com.ifi.ldap.test;

import javax.naming.NamingException;

import com.ifi.ldap.Impl.LdapImpl;

public class Main {
	public static void main(String[] args) {
		LdapImpl impl = new LdapImpl();
		try {
			//impl.deleteUser("TestUser");
			//impl.CreateUser();
			impl.ModifyUser();
			//System.out.println(impl.getDnByUid(""));
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
}
