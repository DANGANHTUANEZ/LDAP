package it.com.ldap.dao;

import it.com.ldap.entity.Group;
import it.com.ldap.entity.User;
import it.com.ldap.output.OutputResult;
import it.com.ldap.output.OutputResultGroup;
import it.com.ldap.output.OutputResultUser;

public interface LdapDao {

	public OutputResult createUser(User user);

	public OutputResult modifyUser(User user);

	public OutputResultUser getUser(String userName);

	public OutputResult deleteUser(String username);

	public OutputResult createGroup(Group group);

	public OutputResult modifyGroup(Group group);

	public OutputResultGroup getGroup(String groupName);

	public OutputResult deleteGroup(String groupName);

	public OutputResult assignUserToGroup(String userDN, String groupDN);

	public OutputResult removeUserFromGroup(String userDN, String groupDN);
}
