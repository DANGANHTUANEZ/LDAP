package com.ifi.ldap.test;

import javax.naming.NamingException;

import com.ifi.ldap.Impl.LdapImpl;

public class Main {
	public static void main(String[] args) {
		LdapImpl impl = new LdapImpl();
		try {
			//impl.deleteUser("TestUser");
			//impl.CreateUser();
			//impl.ModifyUser();
			//System.out.println(impl.getDnByUid("TestUser"));
			impl.addGroup("GroupTest", "addGroup GroupTest");
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
