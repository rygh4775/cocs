package com.cocs.service.twitter;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

import com.cocs.service.AbstractServiceTest;

public class PostTest extends AbstractServiceTest {
	private static Twitter client;
	
	@BeforeClass
	public static void beforeClass() {
		client = getTwitterClient();
	}
	
	@Test
	public void update() throws Throwable{
		StatusUpdate statusUpdate = new StatusUpdate("testing...2");

		File imageFile = new File("/Users/CHO/Desktop/test.png");
		statusUpdate.setMedia(imageFile);
		
//		InputStream is = getImageInputStream() // needs to implement this method by yourself
//		status.setMedia("Image Name", is);
		
		Status status = client.updateStatus(statusUpdate);
		System.out.println(status.getId());
	}
	
	@Test
	public void delete() throws Throwable{
		String statusId = "452061379864715265";
		
		StatusUpdate statusUpdate = new StatusUpdate("testing...");
		
		File imageFile = new File("/Users/CHO/Desktop/test.png");
		statusUpdate.setMedia(imageFile);
		
//		InputStream is = getImageInputStream() // needs to implement this method by yourself
//		status.setMedia("Image Name", is);
		
		Status status = client.destroyStatus(Long.parseLong(statusId) );
		status.getId();
	}

}
