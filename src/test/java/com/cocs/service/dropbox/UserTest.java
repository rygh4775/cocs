package com.cocs.service.dropbox;

import static org.junit.Assert.fail;

import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cocs.service.AbstractServiceTest;
import com.cocs.service.IServiceTest;
import com.dropbox.core.DbxAccountInfo;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;

public class UserTest extends AbstractServiceTest implements IServiceTest.UserTest{
	private static DbxClient client;
	
	@BeforeClass
	public static void beforeClass() {
		client = getDropboxClient();
	}
	
	@Override
	@Test
	public void getInfo() {
		try {
			DbxAccountInfo accountInfo = client.getAccountInfo();
			LOG.debug(accountInfo.toStringMultiline());
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		}
	}

}
