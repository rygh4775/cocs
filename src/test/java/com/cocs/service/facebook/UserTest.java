package com.cocs.service.facebook;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cocs.service.AbstractServiceTest;
import com.cocs.service.IServiceTest;

import facebook4j.Account;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.ResponseList;
import facebook4j.User;

public class UserTest extends AbstractServiceTest implements IServiceTest.UserTest{
	private static Facebook client;
	
	@BeforeClass
	public static void beforeClass() {
		client = getFacebookClient();
	}
	
	@Override
	@Test
	public void getInfo(){
		try {
			User user = client.getMe();
			LOG.debug("Name : " + user.getName());
			LOG.debug("Id : " + user.getId());
			LOG.debug("Email : " + user.getEmail());
			LOG.debug("UserName : " + user.getUsername());
			LOG.debug("Locale : " + user.getLocale());
		} catch (FacebookException e) {
				if (e.getStatusCode() == 401) {
					LOG.error("Invalid Token");
					LOG.error(e.getMessage());
		        };
			LOG.error(e.getMessage());
	        fail();
		}
	}

}
