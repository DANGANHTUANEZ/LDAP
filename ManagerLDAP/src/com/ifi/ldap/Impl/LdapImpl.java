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
import com.ifi.ldap.common.ConverUtils;
import com.ifi.ldap.common.ValidateUtils;
import com.ifi.ldap.entity.LdapAttribute;
import com.ifi.ldap.input.User;
import com.ifi.ldap.output.OutputResult;
import com.ifi.ldap.output.UserOutput;

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
	public UserOutput CreateUser(User user) throws NamingException {
		DirContext dctx = null;
		List<LdapAttribute> listAttr = new ArrayList<LdapAttribute>();
		try {
			dctx = ldapContext();
			// Validate UserDN
			if(!ValidateUtils.userDNNotSpec(user.getUserDN())){
				LOG.error("CreateUser--UserDN not specified: UserDN ="+user.getUserDN());
				return new UserOutput(Constant.STRING_KO,Constant.ER_001,Constant.UserDNNotSpecified);
			}
			if(!ValidateUtils.userDNValidate(user.getUserDN())){
				LOG.error("CreateUser--Attribute [UserDN = "+user.getUserDN()+" is not a valid Ldap DN] doesn’t exist");
				return new UserOutput(Constant.STRING_KO,Constant.ER_009,Constant.UserDNValidate);
			}
			// Create a container set of attributes
			Attributes container = new BasicAttributes();
			listAttr= user.getListAttr();
			for(LdapAttribute at : listAttr ){
				Attribute attr = new BasicAttribute(at.getKey(), at.getValues());
				container.put(attr);
			}
			// Create the entry
			dctx.createSubcontext(getUserDN(user.getUserDN()), container);
			return new UserOutput(Constant.STRING_OK,null,null);
		} catch (NameAlreadyBoundException e) {
			LOG.error("CreateUser--User already exists"+e);
			e.printStackTrace();
			return new UserOutput(Constant.STRING_KO,Constant.ER_002,Constant.UserAlreadyExists);
		} catch (InvalidAttributeValueException e) {
			LOG.error("CreateUser--Attribute [attribute key] has not a valid value [attribute value]  "+e);
			e.printStackTrace();
			return new UserOutput(Constant.STRING_KO,Constant.ER_008,Constant.AttributeValue);
		} catch (InvalidAttributeIdentifierException e) {
			LOG.error("CreateUser--Attribute [attribute key] doesn’t exist"+e);
			System.out.println("UserDN not specified "+e);
			return new UserOutput(Constant.STRING_KO,Constant.ER_007,Constant.AttributeKey);
		} catch (Exception e) {
			LOG.error("CreateUser--Generic error"+e);
			e.printStackTrace();
			return new UserOutput(Constant.STRING_KO,Constant.ER_011,Constant.UserDNNotSpecified);
		} finally {
			if (null!= dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("CreateUser--Error in closing ldap " + e);
				}
			}
		}
	}
	/*public void CreateUser() throws NamingException {
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
	}*/
	
	public UserOutput ModifyUser(User user) throws NamingException {
		DirContext dctx = null;
		List<LdapAttribute> listAttr = new ArrayList<LdapAttribute>();
		try {
			dctx = ldapContext();
			//String name = "cn=TestUser,OU=people,dc=maxcrc,dc=com";
			
			if(!ValidateUtils.userDNNotSpec(user.getUserDN())){
				LOG.error("ModifyUser--UserDN not specified: UserDN ="+user.getUserDN());
				return new UserOutput(Constant.STRING_KO,Constant.ER_001,Constant.UserDNNotSpecified);
			}
			if(checkDnByUserDN(user.getUserDN())==null){
				LOG.error("ModifyUser--User doesn’t exist: UserDN ="+user.getUserDN());
				return new UserOutput(Constant.STRING_KO,Constant.ER_003,Constant.UserDoesntExist);
			}
			if(!ValidateUtils.userDNValidate(user.getUserDN())){
				LOG.error("ModifyUser--Attribute [UserDN = "+user.getUserDN()+" is not a valid Ldap DN] doesn’t exist");
				return new UserOutput(Constant.STRING_KO,Constant.ER_009,Constant.UserDNValidate);
			}
			// Create a container set of attributes
			Attributes container = new BasicAttributes();

			listAttr= user.getListAttr();
			for(LdapAttribute at : listAttr ){
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
			dctx.modifyAttributes("cn="+user.getUserDN()+ConfigInfo.getInstance().getProperty("USERS_OU"), mods);
			return new UserOutput(Constant.STRING_OK,null,null);
		} catch (InvalidAttributeValueException e) {
			LOG.error("ModifyUser--Attribute [attribute key] has not a valid value [attribute value]  "+e);
			e.printStackTrace();
			return new UserOutput(Constant.STRING_KO,Constant.ER_008,Constant.AttributeValue);
		} catch (InvalidAttributeIdentifierException e) {
			LOG.error("ModifyUser--Attribute [attribute key] doesn’t exist"+e);
			System.out.println("UserDN not specified "+e);
			return new UserOutput(Constant.STRING_KO,Constant.ER_007,Constant.AttributeKey);
		} catch (Exception e) {
			LOG.error("ModifyUser--Generic error"+e);
			e.printStackTrace();
			return new UserOutput(Constant.STRING_KO,Constant.ER_011,Constant.UserDNNotSpecified);
		} finally {
			if (null!= dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("ModifyUser--Error in closing ldap " + e);
				}
			}
		}
	}
	
	/*public void ModifyUser() throws NamingException {
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
	}*/

	public String getUserDN(final String userName) {
		String userDN = new StringBuffer().append("cn=").append(userName).append(ConfigInfo.getInstance().getProperty("USERS_OU"))
				.toString();
		System.out.println(userDN);
		return userDN;
	}

	public UserOutput deleteUser(String username) throws NamingException {
		DirContext dctx = null;
		try {
			if(!ValidateUtils.userDNNotSpec(username)){
				LOG.error("CreateUser--UserDN not specified: UserDN ="+username);
				return new UserOutput(Constant.STRING_KO,Constant.ER_001,Constant.UserDNNotSpecified);
			}
			if(checkDnByUserDN(username)==null){
				LOG.error("User doesn’t exist");
				return new UserOutput(Constant.STRING_KO,Constant.ER_003,Constant.UserDoesntExist);
			}
			if(!ValidateUtils.userDNValidate(username)){
				LOG.error("Attribute [UserDN = "+username+" is not a valid Ldap DN] doesn’t exist");
				return new UserOutput(Constant.STRING_KO,Constant.ER_009,Constant.UserDNValidate);
			}
			dctx = ldapContext();
			dctx.destroySubcontext(getUserDN(username));
			return new UserOutput(Constant.STRING_OK,null,null);
		} catch (Exception e) {
			LOG.error("Generic error"+e);
			e.printStackTrace();
			return new UserOutput(Constant.STRING_KO,Constant.ER_011,Constant.UserDNNotSpecified);
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
	
	@SuppressWarnings("rawtypes")
	public OutputResult getUser(String userName) throws Exception {
		DirContext dctx = null;
		OutputResult output=null;
		User user = null;
		try{
			if(!ValidateUtils.userDNNotSpec(userName)){
				LOG.error("CreateUser--UserDN not specified: UserDN ="+userName);
				return new OutputResult(null,Constant.STRING_KO,Constant.ER_001,Constant.UserDNNotSpecified);
			}
			if(!ValidateUtils.userDNValidate(userName)){
				LOG.error("Attribute [UserDN = "+userName+" is not a valid Ldap DN] doesn’t exist");
				return new OutputResult(null,Constant.STRING_KO,Constant.ER_009,Constant.UserDNValidate);
			}
			dctx = ldapContext();
			String filter = "(cn=" + userName + ")";
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
			NamingEnumeration answer = dctx.search(ConfigInfo.getInstance().getProperty("DC"), filter, ctrl);
			if (answer.hasMore()) {
				user = new User();
				user.setUserDN(userName);
				List<LdapAttribute> listAttr= new ArrayList<LdapAttribute>();
				SearchResult result = (SearchResult) answer.next();
				Attributes attributes = result.getAttributes();
				NamingEnumeration namingEnumeration = attributes.getAll();
				
				LdapAttribute ldapAttr = new LdapAttribute();
				ldapAttr.setKey("userPassword");
				String pass=new String((byte[])attributes.get("userPassword").get());
				List<String> listPass = new  ArrayList<String>();
				listPass.add(pass);
				ldapAttr.setValues(listPass);
				listAttr.add(ldapAttr);
				while(namingEnumeration.hasMoreElements()){
					String[]elementsAttr=String.valueOf(namingEnumeration.next()).split(":");
					if(!elementsAttr[0].equalsIgnoreCase("userPassword")){
						LdapAttribute ldapAttribute=new LdapAttribute();
						List<String> listString = new ArrayList<String>();
						ldapAttribute.setKey(elementsAttr[0]);
						System.out.println(elementsAttr[0]);
						String[] subElementAttr=elementsAttr[1].split(",");
						for (int i = 0; i < subElementAttr.length; i++) {
							listString.add(subElementAttr[i]);
						}
						ldapAttribute.setValues(listString);
						listAttr.add(ldapAttribute);
					}
				}
				user.setListAttr(listAttr);
				output= new OutputResult(user,Constant.STRING_OK,null,null);
			} else {
				output= new OutputResult(null,Constant.STRING_KO,Constant.ER_003,Constant.UserDoesntExist);
			}
			answer.close();
			return output;
		} catch (Exception e) {
			LOG.error("Generic error"+e);
			e.printStackTrace();
			return new OutputResult(null,Constant.STRING_KO,Constant.ER_011,Constant.GenericError);
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
	
	@SuppressWarnings("rawtypes")
	public String checkDnByUserDN(String userName) throws Exception {
		DirContext dctx = null;
		String dn;
		dctx = ldapContext();
		String filter = "(cn=" + userName + ")";
		SearchControls ctrl = new SearchControls();
		ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
		NamingEnumeration answer = dctx.search(ConfigInfo.getInstance().getProperty("DC"), filter, ctrl);
		if (answer.hasMore()) {
			SearchResult result = (SearchResult) answer.next();
			Attributes attributes = result.getAttributes();
		/*	NamingEnumeration namingEnumeration = attributes.getAll();
			List<LdapAttribute> listAttr= new ArrayList<LdapAttribute>();
			LdapAttribute ldapAttr = new LdapAttribute();
			ldapAttr.setKey("userPassword");
			String pass=new String((byte[])attributes.get("userPassword").get());
			List<String> listPass = new  ArrayList<String>();
			listPass.add(pass);
			ldapAttr.setValues(listPass);
			listAttr.add(ldapAttr);
			while(namingEnumeration.hasMoreElements()){
				String[]elementsAttr=String.valueOf(namingEnumeration.next()).split(":");
				if(!elementsAttr[0].equalsIgnoreCase("userPassword")){
					LdapAttribute ldapAttribute=new LdapAttribute();
					List<String> listString = new ArrayList<String>();
					ldapAttribute.setKey(elementsAttr[0]);
					System.out.println(elementsAttr[0]);
					String[] subElementAttr=elementsAttr[1].split(",");
					for (int i = 0; i < subElementAttr.length; i++) {
						listString.add(subElementAttr[i]);
					}
					ldapAttribute.setValues(listString);
					listAttr.add(ldapAttribute);
				}
			
			}*/
			dn = result.getNameInNamespace();
		} else {
			dn = null;
		}
		answer.close();
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
		
		return new StringBuffer().append("cn=").append(name).append(",").append(ConfigInfo.getInstance().getProperty("GROUPS_OU")).toString();
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
