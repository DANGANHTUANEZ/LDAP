package com.ifi.ldap.Impl;

import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LdapImpl {

	public DirContext getInitialContext(String hostname, int port, String username, String password)
			throws NamingException {

		String providerURL = new StringBuffer("ldap://").append(hostname).append(":").append(port).toString();

		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		props.put(Context.PROVIDER_URL, providerURL);

		if ((username != null) && (!username.equals(""))) {
			props.put(Context.SECURITY_AUTHENTICATION, "simple");
			props.put(Context.SECURITY_PRINCIPAL, username);
			props.put(Context.SECURITY_CREDENTIALS, ((password == null) ? "" : password));
		}

		return new InitialDirContext(props);
	}

	public DirContext ldapContext() throws Exception {

		Hashtable<Object, Object> env = new Hashtable<Object, Object>();

		String url = "ldap://10.225.3.63:389";
		String conntype = "simple";
		String AdminDn = "cn=Manager,dc=maxcrc,dc=com";
		String password = "secret";

		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_AUTHENTICATION, conntype);
		env.put(Context.SECURITY_PRINCIPAL, AdminDn);
		env.put(Context.SECURITY_CREDENTIALS, password);
		return new InitialDirContext(env);
	}

	public void CreateUser() throws NamingException {
		DirContext dctx = null;
		try {
			dctx = ldapContext();
			// Create a container set of attributes
			Attributes container = new BasicAttributes();

			// Create the objectclass to add
			Attribute objClasses = new BasicAttribute("objectClass");
			objClasses.add("inetOrgPerson");

			// Assign the username, first name, and last name
			Attribute commonName = new BasicAttribute("cn", "TestUser");
			Attribute email = new BasicAttribute("mail", "TestUser");
			Attribute givenName = new BasicAttribute("givenName", "test1");
			Attribute uid = new BasicAttribute("uid", "TestUser");
			Attribute surName = new BasicAttribute("sn", "test2");

			// Add password
			Attribute userPassword = new BasicAttribute("userpassword", "test1");

			// Add these to the container
			container.put(objClasses);
			container.put(commonName);
			container.put(givenName);
			container.put(email);
			container.put(uid);
			container.put(surName);
			container.put(userPassword);

			// Create the entry
			dctx.createSubcontext(getUserDN("TestUser"), container);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					System.out.println("Error in closing ldap " + e);
				}
			}
		}
	}

	public void ModifyUser() throws NamingException {
		DirContext dctx = null;
		try {
			dctx = ldapContext();
			String name = "cn=TestUser,OU=people,dc=maxcrc,dc=com";
			// Create a container set of attributes
			final Attributes container = new BasicAttributes();

			// Create the objectclass to add
			Attribute email = new BasicAttribute("mail", "geisel@wizards.com");
			ModificationItem[] mods = new ModificationItem[1];
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, email);
			// Add
			// Remove
			// modify the entry
			dctx.modifyAttributes(name, mods);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					System.out.println("Error in closing ldap " + e);
				}
			}
		}
	}

	public String getUserDN(final String userName) {
		String userDN = new StringBuffer().append("cn=").append(userName).append(",OU=people,dc=maxcrc,dc=com")
				.toString();
		System.out.println(userDN);
		return userDN;
	}

	public void deleteUser(String username) throws NamingException {
		DirContext dctx = null;
		try {
			dctx = ldapContext();
			dctx.destroySubcontext(getUserDN(username));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getDnByUid(String user) throws Exception {
		DirContext dctx = null;
		String dn;
		dctx = ldapContext();
		String filter = "(uid=" + user + ")";
		SearchControls ctrl = new SearchControls();
		ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration answer = dctx.search("dc=maxcrc,dc=com", filter, ctrl);

		if (answer.hasMore()) {
			SearchResult result = (SearchResult) answer.next();
			Attributes attributes = result.getAttributes();
			System.out.println("attributes:=" + attributes.toString());
			System.out.println("	description:=" + attributes.get("description"));
			System.out.println("	userPassword:=" + attributes.get("userPassword"));
			System.out.println("	deCode Password >>> userPassword:= "
					+ new String((byte[]) attributes.get("userPassword").get()));
			dn = result.getNameInNamespace();
		} else {
			dn = null;
		}
		answer.close();
		System.out.println("getDnByUid()>>>>>>>>dn::::" + dn);
		return dn;
	}

	public void addGroup(String name, String description) throws NamingException {
		DirContext dcxt = null;
		try {
			dcxt = ldapContext();
			// Create a container set of attributes
			Attributes container = new BasicAttributes();

			// Create the objectclass to add
			Attribute objClasses = new BasicAttribute("objectClass");
			objClasses.add("top");
			objClasses.add("groupOfUniqueNames");
			objClasses.add("groupOfForethoughtNames");

			// Assign the name and description to the group
			Attribute cn = new BasicAttribute("cn", name);
			Attribute desc = new BasicAttribute("description", description);

			// Add these to the container
			container.put(objClasses);
			container.put(cn);
			container.put(desc);

			// Create the entry
			dcxt.createSubcontext(getGroupDN(name), container);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getGroupDN(String name) {
		
		return new StringBuffer().append("cn=").append(name).append(",").append("OU=people,dc=maxcrc,dc=com").toString();
	}

	public boolean isValidUser(String username, String password) {
		try {
			DirContext context = getInitialContext("10.225.3.63", 389, getUserDN("TestUser"), "test1");
			return true;
		} catch (javax.naming.NameNotFoundException e) {
			// throw new UserNotFoundException(username);
			e.printStackTrace();
			return false;
		} catch (NamingException e) {
			e.printStackTrace();
			// Any other error indicates couldn't log user in
			return false;
		}
	}

}
