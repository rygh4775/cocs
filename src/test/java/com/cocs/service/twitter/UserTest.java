package com.cocs.service.twitter;

import static org.junit.Assert.fail;

import org.junit.BeforeClass;
import org.junit.Test;

import twitter4j.AccountSettings;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.cocs.service.AbstractServiceTest;
import com.cocs.service.IServiceTest;

public class UserTest extends AbstractServiceTest implements IServiceTest.UserTest{
	private static Twitter client;
	
	@BeforeClass
	public static void beforeClass() {
		client = getTwitterClient();
	}
	
	@Override
	@Test
	public void getInfo(){
		try {
			AccountSettings accountSettings = client.getAccountSettings();
			LOG.debug("Screen Name : " + accountSettings.getScreenName());
			LOG.debug("Level : " + accountSettings.getAccessLevel());
			LOG.debug("Language : " + accountSettings.getLanguage());
		} catch (TwitterException e) {
			if(e instanceof TwitterException) {
				if (((TwitterException) e).getStatusCode() == 401) {
					LOG.error("Invalid Token");
					LOG.error(e.getMessage());
		        };
			}
			LOG.error(e.getMessage());
	        fail();
		}
	}

}
