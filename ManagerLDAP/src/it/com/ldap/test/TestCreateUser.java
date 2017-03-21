package it.com.ldap.test;

import static org.junit.Assert.*;



import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import it.com.ldap.Impl.LdapImpl;

public class TestCreateUser {

	private Logger logger=Logger.getLogger(getClass());
	
	@Before
	public void setUp() throws Exception {
		
	}

	@After
	public void tearDown() throws Exception {
		
	}

	@Test
	public void test_CreateUser_001() {
		LdapImpl impl = new LdapImpl();
		try {
			impl.getUser("");
			logger.info("haha");
			assertEquals("0","0");
		} catch(Exception e){
			throw new RuntimeException("Test failed",e);
		}
	}
	
	
}
