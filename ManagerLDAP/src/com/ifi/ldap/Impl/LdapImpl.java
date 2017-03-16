package com.ifi.ldap.Impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.InvalidAttributeIdentifierException;
import javax.naming.directory.InvalidAttributeValueException;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import com.ifi.ldap.common.ConfigInfo;
import com.ifi.ldap.common.Constant;
import com.ifi.ldap.common.ValidateUtils;
import com.ifi.ldap.entity.LdapAttribute;
import com.ifi.ldap.input.CreateUserInput;
import com.ifi.ldap.output.CreateUserOutput;

import org.apache.log4j.Logger;

public class LdapImpl {
	
	private static final Logger LOG = Logger.getLogger(LdapImpl.class);
	
	public DirContext getInitialContext(String hostname, int port, String username, String password)
			throws NamingException {
		LOG.info("START--getInitialContext");
		String providerURL = new StringBuffer("ldap://").append(hostname).append(":").append(port).toString();

		Properties props = new Properties();
		props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		props.put(Context.PROVIDER_URL, providerURL);

		if ((username != null) && (!username.equals(""))) {
			props.put(Context.SECURITY_AUTHENTICATION, "simple");
			props.put(Context.SECURITY_PRINCIPAL, username);
			props.put(Context.SECURITY_CREDENTIALS, ((password == null) ? "" : password));
		}
		LOG.info("END--getInitialContext");
		return new InitialDirContext(props);
	}

	public DirContext ldapContext() throws Exception {
		
		LOG.info("START--ldapContext");
		Hashtable<Object, Object> env = new Hashtable<Object, Object>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ConfigInfo.getInstance().getProperty("LDAPURL"));
		env.put(Context.SECURITY_AUTHENTICATION, ConfigInfo.getInstance().getProperty("CONNTYPE"));
		env.put(Context.SECURITY_PRINCIPAL, ConfigInfo.getInstance().getProperty("ADMINDN"));
		env.put(Context.SECURITY_CREDENTIALS, ConfigInfo.getInstance().getProperty("PASSWORDAD"));
		LOG.info("END--ldapContext");
		return new InitialDirContext(env);
		
	}
	public CreateUserOutput CreateUser(CreateUserInput userInput) throws NamingException {
		DirContext dctx = null;
		String name=null;
		List<LdapAttribute> listAttr = new ArrayList<LdapAttribute>();
		try {
			dctx = ldapContext();
			// Create a container set of attributes
			Attributes container = new BasicAttributes();
			listAttr= userInput.getListAttr();
			for(LdapAttribute at : listAttr ){
				if("cn".equals(at.getKey())){
					name=at.getValues().get(0);
					if(!ValidateUtils.userDNNotSpec(name)){
						LOG.error("CreateUser--UserDN not specified: UserDN ="+name);
						return new CreateUserOutput(Constant.STRING_KO,Constant.ER_001,Constant.UserDNNotSpecified);
					}
					if(!ValidateUtils.userDNValidate(name)){
						LOG.error("Attribute [UserDN = "+name+" is not a valid Ldap DN] doesn’t exist");
						return new CreateUserOutput(Constant.STRING_KO,Constant.ER_009,Constant.UserDNValidate);
					}
				}
				Attribute attr = new BasicAttribute(at.getKey(), at.getValues());
				container.put(attr);
			}
			// Create the entry
			dctx.createSubcontext(getUserDN(name), container);
			return new CreateUserOutput(Constant.STRING_OK,null,null);
		} catch (NameAlreadyBoundException e) {
			LOG.error("User already exists"+e);
			e.printStackTrace();
			return new CreateUserOutput(Constant.STRING_KO,Constant.ER_002,Constant.UserAlreadyExists);
		} catch (InvalidAttributeValueException e) {
			LOG.error("Attribute [attribute key] has not a valid value [attribute value]  "+e);
			e.printStackTrace();
			return new CreateUserOutput(Constant.STRING_KO,Constant.ER_008,Constant.AttributeValue);
		} catch (InvalidAttributeIdentifierException e) {
			LOG.error("Attribute [attribute key] doesn’t exist"+e);
			System.out.println("UserDN not specified "+e);
			return new CreateUserOutput(Constant.STRING_KO,Constant.ER_007,Constant.AttributeKey);
		} catch (Exception e) {
			LOG.error("Generic error"+e);
			e.printStackTrace();
			return new CreateUserOutput(Constant.STRING_KO,Constant.ER_011,Constant.UserDNNotSpecified);
		} finally {
			if (null!= dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("Error in closing ldap " + e);
				}
			}
		}
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
			Attribute commonName = new BasicAttribute("cn", "Test1");
			Attribute email = new BasicAttribute("mail", "TestUser@gmail.com");
			Attribute givenName = new BasicAttribute("givenName", "haha");
			Attribute uid = new BasicAttribute("uid", "TestUser4");
			Attribute surName = new BasicAttribute("sn", "test3");
			//Attribute img = new BasicAttribute("jpegPhoto", "456");
		//	Attribute photo = new BasicAttribute("photo", "123");

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
			//container.put(img);
			//container.put(photo);

			// Create the entry
			dctx.createSubcontext(getUserDN("Test1"), container);
		} catch (NameAlreadyBoundException e) {
			LOG.error("User already exists"+e);
			e.printStackTrace();
		} catch (InvalidAttributeValueException e) {
			LOG.error("Attribute [attribute key] has not a valid value [attribute value]  "+e);
			e.printStackTrace();
		} catch (InvalidAttributeIdentifierException e) {
			LOG.error("Attribute [attribute key] doesn’t exist"+e);
		} catch (Exception e) {
			// TODO Auto-generated catch block
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
	
	public CreateUserOutput ModifyUser(CreateUserInput userInput) throws NamingException {
		DirContext dctx = null;
		String name=null;
		List<LdapAttribute> listAttr = new ArrayList<LdapAttribute>();
		try {
			dctx = ldapContext();
			name = "cn=TestUser,OU=people,dc=maxcrc,dc=com";
			// Create a container set of attributes
			final Attributes container = new BasicAttributes();

			listAttr= userInput.getListAttr();
			for(LdapAttribute at : listAttr ){
				if("cn".equals(at.getKey())){
					name=at.getValues().get(0);
					if(!ValidateUtils.userDNNotSpec(name)){
						LOG.error("CreateUser--UserDN not specified: UserDN ="+name);
						return new CreateUserOutput(Constant.STRING_KO,Constant.ER_001,Constant.UserDNNotSpecified);
					}
					if(getUserDN(name)==null){
						LOG.error("CreateUser--UserDN not specified: UserDN ="+name);
						return new CreateUserOutput(Constant.STRING_KO,Constant.ER_001,Constant.UserDNNotSpecified);
					}
					if(!ValidateUtils.userDNValidate(name)){
						LOG.error("Attribute [UserDN = "+name+" is not a valid Ldap DN] doesn’t exist");
						return new CreateUserOutput(Constant.STRING_KO,Constant.ER_009,Constant.UserDNValidate);
					}
				}
				Attribute attr = new BasicAttribute(at.getKey(), at.getValues());
				container.put(attr);
			}
			
			// Create the objectclass to add
			Attribute email = new BasicAttribute("mail", "geisel@wizards.com");
			ModificationItem[] mods = new ModificationItem[1];
			mods[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, email);
			// Add
			// Remove
			// modify the entry
			dctx.modifyAttributes(name, mods);
			return new CreateUserOutput(Constant.STRING_OK,null,null);
		} catch (InvalidAttributeValueException e) {
			LOG.error("Attribute [attribute key] has not a valid value [attribute value]  "+e);
			e.printStackTrace();
			return new CreateUserOutput(Constant.STRING_KO,Constant.ER_008,Constant.AttributeValue);
		} catch (InvalidAttributeIdentifierException e) {
			LOG.error("Attribute [attribute key] doesn’t exist"+e);
			System.out.println("UserDN not specified "+e);
			return new CreateUserOutput(Constant.STRING_KO,Constant.ER_007,Constant.AttributeKey);
		} catch (Exception e) {
			LOG.error("Generic error"+e);
			e.printStackTrace();
			return new CreateUserOutput(Constant.STRING_KO,Constant.ER_011,Constant.UserDNNotSpecified);
		} finally {
			if (null!= dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("Error in closing ldap " + e);
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
		String userDN = new StringBuffer().append("cn=").append(userName).append(ConfigInfo.getInstance().getProperty("USERS_OU"))
				.toString();
		System.out.println(userDN);
		return userDN;
	}

	public CreateUserOutput deleteUser(String username) throws NamingException {
		DirContext dctx = null;
		try {
			if(!ValidateUtils.userDNNotSpec(username)){
				LOG.error("CreateUser--UserDN not specified: UserDN ="+username);
				return new CreateUserOutput(Constant.STRING_KO,Constant.ER_001,Constant.UserDNNotSpecified);
			}
			if(getDnByUserDN(username)==null){
				LOG.error("User doesn’t exist");
				return new CreateUserOutput(Constant.STRING_KO,Constant.ER_003,Constant.UserDoesntExist);
			}
			if(!ValidateUtils.userDNValidate(username)){
				LOG.error("Attribute [UserDN = "+username+" is not a valid Ldap DN] doesn’t exist");
				return new CreateUserOutput(Constant.STRING_KO,Constant.ER_009,Constant.UserDNValidate);
			}
			dctx = ldapContext();
			dctx.destroySubcontext(getUserDN(username));
			return new CreateUserOutput(Constant.STRING_OK,null,null);
		} catch (Exception e) {
			LOG.error("Generic error"+e);
			e.printStackTrace();
			return new CreateUserOutput(Constant.STRING_KO,Constant.ER_011,Constant.UserDNNotSpecified);
		}finally {
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("Error in closing ldap " + e);
				}
			}
		}
	}
	
	public String getDnByUserDN1(String userName) throws Exception {
		DirContext dctx = null;
		String dn;
		dctx = ldapContext();
		String filter = "(cn=" + userName + ")";
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

	public String getDnByUserDN(String userName) throws Exception {
		DirContext dctx = null;
		String dn;
		dctx = ldapContext();
		String filter = "(cn=" + userName + ")";
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
		
		return new StringBuffer().append("cn=").append(name).append(",").append("OU=groups,dc=maxcrc,dc=com").toString();
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
