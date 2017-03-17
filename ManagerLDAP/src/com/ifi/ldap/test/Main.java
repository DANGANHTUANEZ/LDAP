package com.ifi.ldap.test;



import com.ifi.ldap.Impl.LdapImpl;

public class Main {
	public static void main(String[] args) {
		LdapImpl impl = new LdapImpl();
		try {
			//impl.deleteUser("Test1");
			//impl.CreateUser();
			//impl.ModifyUser();
			System.out.println(new String(impl.checkDnByUserDN("Vu Nghia")));
			//impl.addGroup("TestUser", "addGroup GroupTest");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
