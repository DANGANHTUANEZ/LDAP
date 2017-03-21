package it.com.ldap.test;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;

import it.com.ldap.Impl.LdapImpl;
import it.com.ldap.entity.LdapAttribute;
import it.com.ldap.entity.Group;
import it.com.ldap.entity.User;
import it.com.ldap.output.OutputResult;
import it.com.ldap.output.OutputResultGroup;
import it.com.ldap.output.OutputResultUser;

public class Main {
    public static void main(String[] args) {
        LdapImpl impl = new LdapImpl();
        try {
             Add();
            //impl.deleteUser("Test1");
            // impl.CreateUser();
            // impl.ModifyUser();
            // System.out.println(new String(impl.checkDnByUserDN("Vu Nghia")));
            // impl.addGroup("TestUser", "addGroup GroupTest");
            // testGetUser();
            // testModifyUser();
            // testDeleteUser();

//            testAddGroup();
//            testGetGroup();
//            testModifyGroup();
//            testAssignUserToGroup();
//            testRemoveUserFromGroup();
//            testDeleteGroup();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void Add() {
        User user = new User();
        List<LdapAttribute> list = new ArrayList<LdapAttribute>();
        LdapAttribute commonName = new LdapAttribute();
        commonName.setKey("objectClass");
        List<String> listStr = new ArrayList<String>();
        listStr.add("inetOrgPerson");
        commonName.setValues(listStr);

        LdapAttribute commonName1 = new LdapAttribute();
        commonName1.setKey("cn");
        List<String> listStr1 = new ArrayList<String>();
        listStr1.add("TestUser333");
        commonName1.setValues(listStr1);

        LdapAttribute commonName2 = new LdapAttribute();
        commonName2.setKey("mail");
        List<String> listStr2 = new ArrayList<String>();
        listStr2.add("TestUser");
        commonName2.setValues(listStr2);

        LdapAttribute commonName3 = new LdapAttribute();
        commonName3.setKey("givenName");
        List<String> listStr3 = new ArrayList<String>();
        listStr3.add("test11");
        commonName3.setValues(listStr3);

        LdapAttribute commonName4 = new LdapAttribute();
        commonName4.setKey("uid");
        List<String> listStr4 = new ArrayList<String>();
        listStr4.add("TestUser");
        commonName4.setValues(listStr4);

        LdapAttribute commonName5 = new LdapAttribute();
        commonName5.setKey("sn");
        List<String> listStr5 = new ArrayList<String>();
        listStr5.add("test1");
        commonName5.setValues(listStr5);

        LdapAttribute commonName6 = new LdapAttribute();
        commonName6.setKey("userpassword");
        List<String> listStr6 = new ArrayList<String>();
        listStr6.add("test1");
        commonName6.setValues(listStr6);

        Attribute objClasses = new BasicAttribute("objectClass");
        objClasses.add("inetOrgPerson");
        list.add(commonName);
        list.add(commonName1);
        list.add(commonName2);
        list.add(commonName3);
        list.add(commonName4);
        list.add(commonName5);
        list.add(commonName6);
        user.setUserDN("TestUser333");
        user.setListAttr(list);
        LdapImpl impl = new LdapImpl();
        impl.createUser(user);

        // Assign the username, first name, and last name
        /*
         * final Attribute commonName = new BasicAttribute("cn", "TestUser");
         * final Attribute email = new BasicAttribute("mail", "TestUser"); final
         * Attribute givenName = new BasicAttribute("givenName", "test1"); final
         * Attribute uid = new BasicAttribute("uid", "TestUser"); final
         * Attribute surName = new BasicAttribute("sn", "test2");
         * 
         * // Add password final Attribute userPassword = new
         * BasicAttribute("userpassword", "test1");
         */
    }

    public static void testGetUser() {
        LdapImpl impl = new LdapImpl();
        final String userName = "TestUser333";

        try {
            OutputResultUser result = impl.getUser(userName);
            User user = result.getUser();
            System.out.println("testGetUser sucess");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("testGetUser fail");
            e.printStackTrace();
        }
    }

    public static void testModifyUser() {
        LdapImpl impl = new LdapImpl();
        final String userName = "TestUser333";

        String mailAttrValue = "new";
        List<String> mailAttrValues = new ArrayList<String>();
        mailAttrValues.add(mailAttrValue);

        try {
            OutputResultUser result = impl.getUser(userName);
            User user = result.getUser();
//            for (int i = 0; i < user.getListAttr().size(); i++) {
//                System.out.print(user.getListAttr().get(i).getKey() + "--");
//                for (int j = 0; j < user.getListAttr().get(i).getValues().size(); j++) {
//                    System.out.print(user.getListAttr().get(i).getValues().get(j));
//                }
//                System.out.println();
//            }
            user.getListAttr().stream().filter(x -> x.getKey().equals("mail")).findFirst().get()
                    .setValues(mailAttrValues);
            impl.modifyUser(user);

            User user1 = impl.getUser(userName).getUser();
            LdapAttribute mailAttr = user1.getListAttr().stream().filter(x -> x.getKey().equals("mail")).findFirst()
                    .get();
            String mailAttrValue1 = mailAttr.getValues().stream().findFirst().get();

            if (mailAttrValue.equals(mailAttrValue1)) {
                System.out.println("testModifyUser sucess");
            } else {
                System.out.println("testModifyUser fail");
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("testModifyUser fail");
            e.printStackTrace();
        }
    }

    public static void testDeleteUser() {
        LdapImpl impl = new LdapImpl();
        final String userName = "TestUser333";

        impl.deleteUser(userName);
		System.out.println("testDeleteUser sucess");
    }

    public static void testAddGroup() {
        LdapImpl impl = new LdapImpl();
        final String groupName = "TestGroup999";
        final String groupDescription = "TestGroup999 infomation";

        try {
//            impl.createGroup(groupName, groupDescription);
            System.out.println("testAddGroup sucess");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("testAddGroup fail");
            e.printStackTrace();
        }
    }

    public static void testGetGroup() {
        LdapImpl impl = new LdapImpl();
        final String groupName = "TestGroup999";

        try {
            OutputResultGroup result = impl.getGroup(groupName);
            Group group = result.getGroup();
            System.out.println("testGetGroup sucess");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("testGetGroup fail");
            e.printStackTrace();
        }
    }

    public static void testModifyGroup() {
        LdapImpl impl = new LdapImpl();
        final String groupName = "TestGroup999";
        
        String descriptionAttrValue = "new";
        List<String> descriptionAttrValues = new ArrayList<String>();
        descriptionAttrValues.add(descriptionAttrValue);

        try {
            OutputResultGroup result = impl.getGroup(groupName);
            Group group = result.getGroup();
            
            group.getListAttr().stream()
                .filter(x -> x.getKey().equals("description"))
                .findFirst()
                .get()
                .setValues(descriptionAttrValues);
            
            impl.modifyGroup(group);
            
            Group group1 = impl.getGroup(groupName).getGroup();
            LdapAttribute descriptionAttr = group1.getListAttr().stream()
                    .filter(x -> x.getKey().equals("description"))
                    .findFirst()
                    .get();
            String descriptionAttrValue1 = descriptionAttr.getValues().stream().findFirst().get();
            
            if (descriptionAttrValue.equals(descriptionAttrValue1)) {
                System.out.println("testModifyGroup sucess");
            } else {
                System.out.println("testModifyGroup fail");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.out.println("testModifyGroup fail");
            e.printStackTrace();
        }

    }

    public static void testDeleteGroup() {
        LdapImpl impl = new LdapImpl();
        final String groupName = "TestGroup999";
        impl.deleteGroup(groupName);
		System.out.println("testDeleteGroup sucess");
    }

    public static void testAssignUserToGroup() {
        LdapImpl impl = new LdapImpl();
        final String groupName = "TestGroup999";
        final String userName = "TestUser333";

        Add();
        try {
            OutputResult result = impl.assignUserToGroup(userName, groupName);
            System.out.println("testAssignUserToGroup sucess");
        } catch (Exception e) {
            // TODO: handle exception
            System.out.println("testAssignUserToGroup fail");
            e.printStackTrace();
        }
    }

    public static void testRemoveUserFromGroup() {
        LdapImpl impl = new LdapImpl();
        final String groupName = "TestGroup999";
        final String userName = "TestUser333";
        OutputResult result = impl.removeUserFromGroup(userName, groupName);
        System.out.println("testRemoveUserFromGroup sucess");
        // TODO: handle exception
        testDeleteUser();
    }
}
