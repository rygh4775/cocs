package com.cocs.service.google;

import static org.junit.Assert.fail;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cocs.service.AbstractServiceTest;
import com.cocs.service.IServiceTest;
import com.google.api.client.http.HttpResponseException;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.About;

public class UserTest extends AbstractServiceTest implements IServiceTest.UserTest{
	private static Drive client;
	
	@BeforeClass
	public static void beforeClass() {
		client = getGoogleClient();
	}
	
	@Override
	@Test
	public void getInfo(){
	    About about;
		try {
			about = client.about().get().execute();
			LOG.debug(about.toPrettyString());
		} catch (IOException e) {
			if(e instanceof HttpResponseException) {
				if (((HttpResponseException) e).getStatusCode() == 401) {
					LOG.error("Invalid Token");
					LOG.error(e.getMessage());
		        };
			}
			LOG.error(e.getMessage());
	        fail();
		}
	}

}
