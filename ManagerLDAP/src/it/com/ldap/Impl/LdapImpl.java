package it.com.ldap.Impl;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NameAlreadyBoundException;
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

import it.com.ldap.common.ConfigInfo;
import it.com.ldap.common.Constant;
import it.com.ldap.common.ValidatorUtils;
import it.com.ldap.dao.LdapDao;
import it.com.ldap.entity.Group;
import it.com.ldap.entity.LdapAttribute;
import it.com.ldap.entity.User;
import it.com.ldap.output.OutputResultUser;
import it.com.ldap.output.OutputResult;
import it.com.ldap.output.OutputResultGroup;

import org.apache.log4j.Logger;

public class LdapImpl implements LdapDao {
	
	private static final Logger LOG = Logger.getLogger(LdapImpl.class);

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
	public OutputResult createUser(User user) {
		LOG.info("START--CreateUser");
		DirContext dctx = null;
		List<LdapAttribute> listAttr = new ArrayList<LdapAttribute>();
		try {
			dctx = ldapContext();
			// Validate UserDN
			if(!ValidatorUtils.checkNotSpec(user.getUserDN())){
				LOG.error("CreateUser--UserDN not specified: UserDN:="+user.getUserDN());
				return new OutputResult(Constant.STRING_KO,Constant.ER_001,Constant.USERDN_NOT_SPECIFIED);
			}
			if(!ValidatorUtils.checkValidate(user.getUserDN())){
				LOG.error("CreateUser--UserDN:= "+user.getUserDN()+" is not a valid Ldap DN");
				return new OutputResult(Constant.STRING_KO,Constant.ER_009,Constant.USERDN_VALIDATE);
			}
			// Create a container set of attributes
			Attributes container = new BasicAttributes();
			listAttr= user.getListAttr();
			for(LdapAttribute at : listAttr ){
				Attribute attr = new BasicAttribute(at.getKey());
				for (int i = 0; i < at.getValues().size(); i++) {
					attr.add(at.getValues().get(i));
				}
				container.put(attr);
			}
			// Create the entry
			dctx.createSubcontext(getUserDN(user.getUserDN()), container);
			LOG.info("END--createUser");
			return new OutputResult(Constant.STRING_OK,null,null);
		} catch (NameAlreadyBoundException e) {
			LOG.error("CreateUser--User already exists"+e);
			System.out.println("CreateUser--User already exists"+e);
			e.printStackTrace();
			return new OutputResult(Constant.STRING_KO,Constant.ER_002,Constant.USER_ALREADY_EXISTS);
		} catch (InvalidAttributeValueException e) {
			LOG.error("CreateUser--"+ValidatorUtils.attributeValue(e.getMessage()));
			System.out.println("CreateUser--"+ValidatorUtils.attributeValue(e.getMessage()));
			e.printStackTrace();
			return new OutputResult(Constant.STRING_KO,Constant.ER_008,ValidatorUtils.attributeValue(e.getMessage()));
		} catch (InvalidAttributeIdentifierException e) {
			LOG.error("CreateUser--"+ValidatorUtils.attributeKey(e.getMessage()));
			System.out.println("CreateUser"+ValidatorUtils.attributeKey(e.getMessage()));
			return new OutputResult(Constant.STRING_KO,Constant.ER_007,ValidatorUtils.attributeKey(e.getMessage()));
		} catch (Exception e) {
			LOG.error("CreateUser--Generic error"+e.getMessage());
			System.out.println("CreateUser--Generic error"+e.getMessage());
			e.printStackTrace();
			return new OutputResult(Constant.STRING_KO,Constant.ER_011,Constant.GENERIC_ERROR+": "+e.getMessage());
		} finally {
			if (null!= dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("CreateUser--Error in closing ldap " + e.getMessage());
				}
			}
		}
	}
	
	public OutputResult modifyUser(User user) {
		LOG.info("START--ModifyUser");
		DirContext dctx = null;
		List<LdapAttribute> listAttrCurrent = new ArrayList<LdapAttribute>();
		List<LdapAttribute> listAttrOld = new ArrayList<LdapAttribute>();
		try {
			dctx = ldapContext();
			//String name = "cn=TestUser,OU=people,dc=maxcrc,dc=com";
			
			if(!ValidatorUtils.checkNotSpec(user.getUserDN())){
				LOG.error("ModifyUser--UserDN not specified: UserDN ="+user.getUserDN());
				return new OutputResult(Constant.STRING_KO,Constant.ER_001,Constant.USERDN_NOT_SPECIFIED);
			}
			if(!ValidatorUtils.checkValidate(user.getUserDN())){
				LOG.error("ModifyUser--UserDN = "+user.getUserDN()+" is not a valid Ldap DN");
				return new OutputResult(Constant.STRING_KO,Constant.ER_009,Constant.USERDN_VALIDATE);
			}
			User userOld=getUser(user.getUserDN()).getUser();
			if(userOld==null){
				LOG.error("ModifyUser--User doesn’t exist: UserDN ="+user.getUserDN());
				return new OutputResult(Constant.STRING_KO,Constant.ER_003,Constant.USER_DOESNT_EXIST);
			}
			
			listAttrCurrent= user.getListAttr();
			listAttrOld =userOld.getListAttr();
			ModificationItem[] mods = new ModificationItem[listAttrCurrent.size()];
			boolean ck;
			int count=0;
			for(LdapAttribute atCurrent : listAttrCurrent ){
				ck=true;
				for(LdapAttribute atOld : listAttrOld ){
					if(atCurrent.getKey().equalsIgnoreCase(atOld.getKey())){
						Attribute attr = new BasicAttribute(atCurrent.getKey());
						for (int i = 0; i < atCurrent.getValues().size(); i++) {
							attr.add(atCurrent.getValues().get(i));
						}
						mods[count] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
						ck=false;
						count++;
					}
				}
				if(ck){
					Attribute attr = new BasicAttribute(atCurrent.getKey());
					for (int i = 0; i < atCurrent.getValues().size(); i++) {
						attr.add(atCurrent.getValues().get(i));
					}
					mods[count] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr);
					count++;
				}
			}
			// modify the entry
			dctx.modifyAttributes("cn="+user.getUserDN()+ConfigInfo.getInstance().getProperty("USERS_OU"), mods);
			LOG.info("END--ModifyUser");
			return new OutputResult(Constant.STRING_OK,null,null);
		} catch (InvalidAttributeValueException e) {
			LOG.error("ModifyUser--"+ValidatorUtils.attributeValue(e.getMessage()));
			System.out.println("ModifyUser--"+ValidatorUtils.attributeValue(e.getMessage()));
			e.printStackTrace();
			return new OutputResult(Constant.STRING_KO,Constant.ER_008,ValidatorUtils.attributeValue(e.getMessage()));
		} catch (InvalidAttributeIdentifierException e) {
			LOG.error("ModifyUser--"+ValidatorUtils.attributeKey(e.getMessage()));
			System.out.println("ModifyUser--"+ValidatorUtils.attributeKey(e.getMessage()));
			return new OutputResult(Constant.STRING_KO,Constant.ER_007,ValidatorUtils.attributeKey(e.getMessage()));
		} catch (Exception e) {
			LOG.error("ModifyUser--Generic error"+e.getMessage());
			System.out.println("ModifyUser--Generic error"+e.getMessage());
			return new OutputResult(Constant.STRING_KO,Constant.ER_011,Constant.GENERIC_ERROR+": "+e.getMessage());
		} finally {
			if (null!= dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("ModifyUser--Error in closing ldap " + e.getMessage());
				}
			}
		}
	}
	
	public OutputResult deleteUser(String username) {
		LOG.info("START--deleteUser");
		DirContext dctx = null;
		try {
			if(!ValidatorUtils.checkNotSpec(username)){
				LOG.error("deleteUser--UserDN not specified: UserDN ="+username);
				return new OutputResult(Constant.STRING_KO,Constant.ER_001,Constant.USERDN_NOT_SPECIFIED);
			}
			if(checkExist(username)==null){
				LOG.error("deleteUser--User doesn’t exist: UserDN ="+username);
				return new OutputResult(Constant.STRING_KO,Constant.ER_003,Constant.USER_DOESNT_EXIST);
			}
			if(!ValidatorUtils.checkValidate(username)){
				LOG.error("deleteUser--UserDN = "+username+" is not a valid Ldap DN");
				return new OutputResult(Constant.STRING_KO,Constant.ER_009,Constant.USERDN_VALIDATE);
			}
			dctx = ldapContext();
			dctx.destroySubcontext(getUserDN(username));
			LOG.info("END--deleteUser");
			return new OutputResult(Constant.STRING_OK,null,null);
		} catch (Exception e) {
			LOG.error("deleteUser--Generic error"+e.getMessage());
			return new OutputResult(Constant.STRING_KO,Constant.ER_011,Constant.GENERIC_ERROR+": "+e.getMessage());
		}finally {
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("deleteUser--Error in closing ldap " + e.getMessage());
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public OutputResultUser getUser(String userName) {
		LOG.info("START--getUser");
		DirContext dctx = null;
		OutputResultUser output=null;
		NamingEnumeration answer=null;
		User user = null;
		try{
			if(!ValidatorUtils.checkNotSpec(userName)){
				LOG.error("getUser--UserDN not specified: UserDN ="+userName);
				return new OutputResultUser(null,Constant.STRING_KO,Constant.ER_001,Constant.USERDN_NOT_SPECIFIED);
			}
			if(!ValidatorUtils.checkValidate(userName)){
				LOG.error("getUser--UserDN = "+userName+" is not a valid Ldap DN");
				return new OutputResultUser(null,Constant.STRING_KO,Constant.ER_009,Constant.USERDN_VALIDATE);
			}
			dctx = ldapContext();
			String filter = "(cn=" + userName + ")";
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
			answer = dctx.search(ConfigInfo.getInstance().getProperty("DC"), filter, ctrl);
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
				listPass.add(pass.trim());
				ldapAttr.setValues(listPass);
				listAttr.add(ldapAttr);
				while(namingEnumeration.hasMoreElements()){
					String[]elementsAttr=String.valueOf(namingEnumeration.next()).trim().split(":");
					if(!elementsAttr[0].trim().equalsIgnoreCase("userPassword")){
						LdapAttribute ldapAttribute=new LdapAttribute();
						List<String> listString = new ArrayList<String>();
						ldapAttribute.setKey(elementsAttr[0].trim());
						String[] subElementAttr=elementsAttr[1].trim().split(",");
						for (int i = 0; i < subElementAttr.length; i++) {
							listString.add(subElementAttr[i].trim());
						}
						ldapAttribute.setValues(listString);
						listAttr.add(ldapAttribute);
					}
				}
				user.setListAttr(listAttr);
				output= new OutputResultUser(user,Constant.STRING_OK,null,null);
			} else {
				output= new OutputResultUser(null,Constant.STRING_KO,Constant.ER_003,Constant.USER_DOESNT_EXIST);
			}
			
			LOG.info("END--getUser");
			return output;
		} catch (Exception e) {
			LOG.error("getUser--Generic error"+e.getMessage());
			return new OutputResultUser(null,Constant.STRING_KO,Constant.ER_011,Constant.GENERIC_ERROR+": "+e.getMessage());
		}finally {
			if (null != answer) {
				try {
					answer.close();
				} catch (final NamingException e) {
					LOG.error("getUser--Error in closing ldap " + e.getMessage());
				}
			}
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("getUser--Error in closing ldap " + e.getMessage());
				}
			}
		}
	}

	public OutputResult createGroup(Group group) {
		LOG.info("START--CreateGroup");
		DirContext dcxt = null;
		List<LdapAttribute> listAttr = new ArrayList<LdapAttribute>();
		try {
			dcxt = ldapContext();
			
			if(!ValidatorUtils.checkNotSpec(group.getGroupDN())){
				LOG.error("CreateGroup--getGroupDN not specified: UserDN ="+group.getGroupDN());
				return new OutputResult(Constant.STRING_KO,Constant.ER_004,Constant.USERDN_NOT_SPECIFIED);
			}
			if(!ValidatorUtils.checkValidate(group.getGroupDN())){
				LOG.error("CreateGroup--GroupDN := "+group.getGroupDN()+" is not a valid Ldap DN");
				return new OutputResult(Constant.STRING_KO,Constant.ER_010,Constant.USERDN_VALIDATE);
			}
			// Create a container set of attributes
			Attributes container = new BasicAttributes();
			listAttr= group.getListAttr();
			for(LdapAttribute at : listAttr ){
				Attribute attr = new BasicAttribute(at.getKey());
				for (int i = 0; i < at.getValues().size(); i++) {
					attr.add(at.getValues().get(i));
				}
				container.put(attr);
			}
			// Create the group
			dcxt.createSubcontext(group.getGroupDN(), container);
			LOG.info("END--CreateGroup");
			return new OutputResult(Constant.STRING_OK,null,null);
		} catch (NameAlreadyBoundException e) {
			LOG.error("CreateGroup--GroupDN already exists "+e.getMessage());
			System.out.println("CreateGroup--GroupDN already exists "+e.getMessage());
			e.printStackTrace();
			return new OutputResult(Constant.STRING_KO,Constant.ER_005,Constant.GROUP_ALREADY_EXISTS);
		} catch (InvalidAttributeValueException e) {
			LOG.error("CreateGroup--"+ValidatorUtils.attributeValue(e.getMessage()));
			System.out.println("CreateGroup--"+ValidatorUtils.attributeValue(e.getMessage()));
			e.printStackTrace();
			return new OutputResult(Constant.STRING_KO,Constant.ER_008,ValidatorUtils.attributeValue(e.getMessage()));
		} catch (InvalidAttributeIdentifierException e) {
			LOG.error("CreateGroup--"+ValidatorUtils.attributeKey(e.getMessage()));
			System.out.println("CreateGroup--"+ValidatorUtils.attributeKey(e.getMessage()));
			return new OutputResult(Constant.STRING_KO,Constant.ER_007,ValidatorUtils.attributeKey(e.getMessage()));
		} catch (Exception e) {
			LOG.error("CreateGroup--Generic error"+e.getMessage());
			System.out.println("CreateGroup--Generic error"+e.getMessage());
			e.printStackTrace();
			return new OutputResult(Constant.STRING_KO,Constant.ER_011,Constant.GENERIC_ERROR+": "+e.getMessage());
		} finally {
			if (null!= dcxt) {
				try {
					dcxt.close();
				} catch (final NamingException e) {
					LOG.error("CreateGroup--Error in closing ldap " + e.getMessage());
				}
			}
		}
	}
	
	public OutputResult modifyGroup(Group group) {
		LOG.info("START--ModifyGroup");
		DirContext dctx = null;
		List<LdapAttribute> listAttrCurrent = new ArrayList<LdapAttribute>();
		List<LdapAttribute> listAttrOld = new ArrayList<LdapAttribute>();
		try {
			dctx = ldapContext();
			//String name = "cn=TestUser,OU=people,dc=maxcrc,dc=com";
			
			if(!ValidatorUtils.checkNotSpec(group.getGroupDN())){
				LOG.error("ModifyGroup--GroupDN not specified: GroupDN ="+group.getGroupDN());
				return new OutputResult(Constant.STRING_KO,Constant.ER_004,Constant.USERDN_NOT_SPECIFIED);
			}
			if(!ValidatorUtils.checkValidate(group.getGroupDN())){
				LOG.error("ModifyGroup-- GroupDN "+group.getGroupDN()+"is not a valid Ldap DN");
				return new OutputResult(Constant.STRING_KO,Constant.ER_010,Constant.USERDN_VALIDATE);
			}
			Group GroupOld=getGroup(group.getGroupDN()).getGroup();
			if(GroupOld==null){
				LOG.error("ModifyGroup--Group doesn’t exist");
				return new OutputResult(Constant.STRING_KO,Constant.ER_006,Constant.USER_DOESNT_EXIST);
			}
			listAttrCurrent= group.getListAttr();
			listAttrOld =GroupOld.getListAttr();
			ModificationItem[] mods = new ModificationItem[listAttrCurrent.size()];
			boolean ck;
			int count=0;
			for(LdapAttribute atCurrent : listAttrCurrent ){
				ck=true;
				for(LdapAttribute atOld : listAttrOld ){
					if(atCurrent.getKey().equalsIgnoreCase(atOld.getKey())){
						Attribute attr = new BasicAttribute(atCurrent.getKey());
						for (int i = 0; i < atCurrent.getValues().size(); i++) {
							attr.add(atCurrent.getValues().get(i));
						}
						mods[count] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
						ck=false;
						count++;
					}
				}
				if(ck){
					Attribute attr = new BasicAttribute(atCurrent.getKey());
					for (int i = 0; i < atCurrent.getValues().size(); i++) {
						attr.add(atCurrent.getValues().get(i));
					}
					mods[count] = new ModificationItem(DirContext.ADD_ATTRIBUTE, attr);
					count++;
				}
			}
			// modify the entry
			dctx.modifyAttributes("cn="+group.getGroupDN()+ConfigInfo.getInstance().getProperty("GROUPS_OU"), mods);
			LOG.info("END--ModifyGroup");
			return new OutputResult(Constant.STRING_OK,null,null);
		} catch (InvalidAttributeValueException e) {
			LOG.error("ModifyGroup--"+ValidatorUtils.attributeValue(e.getMessage()));
			System.out.println("ModifyGroup--"+ValidatorUtils.attributeValue(e.getMessage()));
			e.printStackTrace();
			return new OutputResult(Constant.STRING_KO,Constant.ER_008,ValidatorUtils.attributeValue(e.getMessage()));
		} catch (InvalidAttributeIdentifierException e) {
			LOG.error("ModifyGroup--"+ValidatorUtils.attributeKey(e.getMessage()));
			System.out.println("ModifyGroup--"+ValidatorUtils.attributeKey(e.getMessage()));
			return new OutputResult(Constant.STRING_KO,Constant.ER_007,ValidatorUtils.attributeKey(e.getMessage()));
		} catch (Exception e) {
			LOG.error("ModifyGroup--Generic error"+e.getMessage());
			System.out.println("ModifyGroup--Generic error"+e.getMessage());
			e.printStackTrace();
			return new OutputResult(Constant.STRING_KO,Constant.ER_011,Constant.GENERIC_ERROR+": "+e.getMessage());
		} finally {
			if (null!= dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("ModifyUser--Error in closing ldap " + e.getMessage());
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public OutputResultGroup getGroup(String groupName) {
		LOG.info("START--getGroup");
		DirContext dctx = null;
		OutputResultGroup output=null;
		NamingEnumeration answer = null;
		Group group = null;
		try{
			if(!ValidatorUtils.checkNotSpec(groupName)){
				LOG.error("getGroup--GroupDN not specified: GroupDN ="+groupName);
				return new OutputResultGroup(null,Constant.STRING_KO,Constant.ER_004,Constant.GROUPDN_NOT_SPECIFIED);
			}
			if(!ValidatorUtils.checkValidate(groupName)){
				LOG.error("getGroup--GroupDN: = "+groupName+"is not a valid Ldap DN");
				return new OutputResultGroup(null,Constant.STRING_KO,Constant.ER_010,Constant.GROUPDN_VALIDATE);
			}
			dctx = ldapContext();
			String filter = "(cn=" + groupName + ")";
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
			answer = dctx.search(ConfigInfo.getInstance().getProperty("DC"), filter, ctrl);
			if (answer.hasMore()) {
				group = new Group();
				group.setGroupDN(groupName);
				List<LdapAttribute> listAttr= new ArrayList<LdapAttribute>();
				SearchResult result = (SearchResult) answer.next();
				Attributes attributes = result.getAttributes();
				NamingEnumeration namingEnumeration = attributes.getAll();
				LdapAttribute ldapAttr = new LdapAttribute();
				ldapAttr.setKey("userPassword");
				String pass=new String((byte[])attributes.get("userPassword").get());
				List<String> listPass = new  ArrayList<String>();
				listPass.add(pass.trim());
				ldapAttr.setValues(listPass);
				listAttr.add(ldapAttr);
				while(namingEnumeration.hasMoreElements()){
					String[]elementsAttr=String.valueOf(namingEnumeration.next()).trim().split(":");
					if(!elementsAttr[0].trim().equalsIgnoreCase("userPassword")){
						LdapAttribute ldapAttribute=new LdapAttribute();
						List<String> listString = new ArrayList<String>();
						ldapAttribute.setKey(elementsAttr[0].trim());
						//System.out.println(elementsAttr[0]);
						String[] subElementAttr=elementsAttr[1].trim().split(",");
						for (int i = 0; i < subElementAttr.length; i++) {
							listString.add(subElementAttr[i].trim());
						}
						ldapAttribute.setValues(listString);
						listAttr.add(ldapAttribute);
					}
				}
				group.setListAttr(listAttr);
				output= new OutputResultGroup(group,Constant.STRING_OK,null,null);
			} else {
				output= new OutputResultGroup(null,Constant.STRING_KO,Constant.ER_006,Constant.GROUP_DOESNT_EXIST);
			}
			
			LOG.info("END--getGroup");
			return output;
		} catch (Exception e) {
			LOG.error("getGroup--Generic error"+e.getMessage());
			System.out.println("getGroup--Generic error"+e.getMessage());
			return new OutputResultGroup(null,Constant.STRING_KO,Constant.ER_011,Constant.GENERIC_ERROR+": "+e.getMessage());
		}finally {
			if (null != answer) {
				try {
					answer.close();
				} catch (final NamingException e) {
					LOG.error("getUser--Error in closing ldap " + e.getMessage());
				}
			}
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("getUser--Error in closing ldap " + e.getMessage());
				}
			}
		}
	}
	
	public OutputResult deleteGroup(String groupName) {
		LOG.info("START--deleteGroup");
		DirContext dctx = null;
		try {
			if(!ValidatorUtils.checkNotSpec(groupName)){
				LOG.error("deleteGroup--GroupDN not specified: GroupDN ="+groupName);
				return new OutputResult(Constant.STRING_KO,Constant.ER_004,Constant.GROUPDN_NOT_SPECIFIED);
			}
			if(checkExist(groupName)==null){
				LOG.error("deleteGroup--Group doesn’t exist");
				return new OutputResult(Constant.STRING_KO,Constant.ER_006,Constant.GROUP_DOESNT_EXIST);
			}
			if(!ValidatorUtils.checkValidate(groupName)){
				LOG.error("deleteGroup--GroupDN := "+groupName+" is not a valid Ldap DN");
				return new OutputResult(Constant.STRING_KO,Constant.ER_010,Constant.GROUPDN_VALIDATE);
			}
			dctx = ldapContext();
			dctx.destroySubcontext(getGroupDN(groupName));
			LOG.info("END--deleteGroup");
			return new OutputResult(Constant.STRING_OK,null,null);
		} catch (Exception e) {
			LOG.error("deleteGroup--Generic error"+e.getMessage());
			System.out.println("deleteGroup--Generic error"+e.getMessage());
			return new OutputResult(Constant.STRING_KO,Constant.ER_011,Constant.GENERIC_ERROR+": "+e.getMessage());
		}finally {
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("Error in closing ldap " + e.getMessage());
				}
			}
		}
	}
	
	public OutputResult assignUserToGroup(String userDN, String groupDN) {
		LOG.info("START--AssignUserToGroup");
		DirContext dctx = null;
		try {
			//validate group
			if(!ValidatorUtils.checkNotSpec(groupDN)){
				LOG.error("AssignUserToGroup--GroupDN not specified: GroupDN ="+groupDN);
				return new OutputResult(Constant.STRING_KO,Constant.ER_004,Constant.GROUPDN_NOT_SPECIFIED);
			}
			if(checkExist(groupDN)==null){
				LOG.error("AssignUserToGroup--Group doesn’t exist");
				return new OutputResult(Constant.STRING_KO,Constant.ER_006,Constant.GROUP_DOESNT_EXIST);
			}
			if(!ValidatorUtils.checkValidate(groupDN)){
				LOG.error("AssignUserToGroup--GroupDN := "+groupDN+" is not a valid Ldap DN");
				return new OutputResult(Constant.STRING_KO,Constant.ER_010,Constant.GROUPDN_VALIDATE);
			}
			//validate user
			if(!ValidatorUtils.checkNotSpec(userDN)){
				LOG.error("AssignUserToGroup--userDN not specified: GroupDN ="+userDN);
				return new OutputResult(Constant.STRING_KO,Constant.ER_001,Constant.USERDN_NOT_SPECIFIED);
			}
			if(checkExist(userDN)==null){
				LOG.error("AssignUserToGroup--userDN doesn’t exist");
				return new OutputResult(Constant.STRING_KO,Constant.ER_003,Constant.USER_DOESNT_EXIST);
			}
			if(!ValidatorUtils.checkValidate(userDN)){
				LOG.error("AssignUserToGroup--userDN := "+userDN+" is not a valid Ldap DN");
				return new OutputResult(Constant.STRING_KO,Constant.ER_009,Constant.USERDN_VALIDATE);
			}
			dctx = ldapContext();
			ModificationItem[] modsGroup = new ModificationItem[1];
            Attribute modGroup = new BasicAttribute("memberuid",getUserDN(userDN));
            modsGroup[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, modGroup);
            dctx.modifyAttributes(getGroupDN(groupDN), modsGroup);
            
            ModificationItem[] modsUser = new ModificationItem[1];
            Attribute modUser = new BasicAttribute("ou",getGroupDN(groupDN));
            modsUser[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, modUser);
            dctx.modifyAttributes(getUserDN(userDN), modsUser);
            LOG.info("END--AssignUserToGroup");
            return new OutputResult(Constant.STRING_OK,null,null);
		} catch (Exception e) {
			LOG.error("AssignUserToGroup--Generic error"+e.getMessage());
			System.out.println("AssignUserToGroup--Generic error"+e.getMessage());
			return new OutputResult(Constant.STRING_KO,Constant.ER_011,Constant.GENERIC_ERROR+": "+e.getMessage());
		}finally {
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("AssignUserToGroup--Error in closing ldap " + e.getMessage());
				}
			}
		}
	}
	
	public OutputResult removeUserFromGroup(String userDN, String groupDN) {
		LOG.info("START--RemoveUserFromGroup");
		DirContext dctx = null;
		try {
			//validate group
			if(!ValidatorUtils.checkNotSpec(groupDN)){
				LOG.error("RemoveUserFromGroup--GroupDN not specified: GroupDN ="+groupDN);
				return new OutputResult(Constant.STRING_KO,Constant.ER_004,Constant.GROUPDN_NOT_SPECIFIED);
			}
			if(checkExist(groupDN)==null){
				LOG.error("RemoveUserFromGroup--Group doesn’t exist");
				return new OutputResult(Constant.STRING_KO,Constant.ER_006,Constant.GROUP_DOESNT_EXIST);
			}
			if(!ValidatorUtils.checkValidate(groupDN)){
				LOG.error("RemoveUserFromGroup--GroupDN := "+groupDN+" is not a valid Ldap DN");
				return new OutputResult(Constant.STRING_KO,Constant.ER_010,Constant.GROUPDN_VALIDATE);
			}
			//validate user
			if(!ValidatorUtils.checkNotSpec(userDN)){
				LOG.error("RemoveUserFromGroup--userDN not specified: GroupDN ="+userDN);
				return new OutputResult(Constant.STRING_KO,Constant.ER_001,Constant.USERDN_NOT_SPECIFIED);
			}
			if(checkExist(userDN)==null){
				LOG.error("RemoveUserFromGroup--userDN doesn’t exist");
				return new OutputResult(Constant.STRING_KO,Constant.ER_003,Constant.USER_DOESNT_EXIST);
			}
			if(!ValidatorUtils.checkValidate(userDN)){
				LOG.error("RemoveUserFromGroup--userDN := "+userDN+" is not a valid Ldap DN");
				return new OutputResult(Constant.STRING_KO,Constant.ER_009,Constant.USERDN_VALIDATE);
			}
			dctx = ldapContext();
			ModificationItem[] modsGroup = new ModificationItem[1];
            Attribute modGroup = new BasicAttribute("memberuid",getUserDN(userDN));
            modsGroup[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, modGroup);
            dctx.modifyAttributes(getGroupDN(groupDN), modsGroup);
            
            ModificationItem[] modsUser = new ModificationItem[1];
            Attribute modUser = new BasicAttribute("ou",getGroupDN(groupDN));
            modsUser[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, modUser);
            dctx.modifyAttributes(getUserDN(userDN), modsUser);
	        LOG.info("END--RemoveUserFromGroup");
	        return new OutputResult(Constant.STRING_OK,null,null);
		} catch (Exception e) {
			LOG.error("RemoveUserFromGroup--Generic error"+e.getMessage());
			System.out.println("RemoveUserFromGroup--Generic error"+e.getMessage());
			return new OutputResult(Constant.STRING_KO,Constant.ER_011,Constant.GENERIC_ERROR+": "+e.getMessage());
		}finally {
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("RemoveUserFromGroup--Error in closing ldap " + e.getMessage());
				}
			}
		}
	}

	@SuppressWarnings("rawtypes")
	public String checkExist(String name) {
		LOG.info("START--checkExist");
		DirContext dctx = null;
		String dn=null;
		NamingEnumeration answer = null;
		try{
			dctx = ldapContext();
			String filter = "(cn=" + name + ")";
			SearchControls ctrl = new SearchControls();
			ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
			answer = dctx.search(ConfigInfo.getInstance().getProperty("DC"), filter, ctrl);
			if (answer.hasMore()) {
				SearchResult result = (SearchResult) answer.next();
				dn = result.getNameInNamespace();
			} else {
				dn = null;
			}
			LOG.info("END--checkExist");
			return dn;
		}catch (Exception e) {
			LOG.error("checkExist--Exception: "+e.getMessage());
			return dn;
		}finally{
			if (null != answer) {
				try {
					answer.close();
				} catch (final NamingException e) {
					LOG.error("getUser--Error in closing ldap " + e.getMessage());
				}
			}
			if (null != dctx) {
				try {
					dctx.close();
				} catch (final NamingException e) {
					LOG.error("getUser--Error in closing ldap " + e.getMessage());
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
	
	private String getGroupDN(String name) {
		String groupDN= new StringBuffer().append("cn=").append(name).append(ConfigInfo.getInstance().getProperty("GROUPS_OU")).toString();
		System.out.println(groupDN);
		return groupDN;
	}

}
