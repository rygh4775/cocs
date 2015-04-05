package com.cocs.service.facebook;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Test;

import com.cocs.service.AbstractServiceTest;

import facebook4j.Facebook;
import facebook4j.Media;
import facebook4j.PhotoUpdate;
import facebook4j.VideoUpdate;

public class PostTest extends AbstractServiceTest {
private static Facebook client;
	
	@BeforeClass
	public static void beforeClass() {
		client = getFacebookClient();
	}
	
	@Test
	public void postMessage() throws Throwable{
		String postFeed = client.postStatusMessage("just message~");
		
		System.out.println(postFeed);
	}
	
	@Test
	public void postPhoto() throws Throwable{
		File mediaFile = new File("/Users/CHO/Desktop/test.png");
		Media media = new Media(mediaFile);
		PhotoUpdate photoUpdate = new PhotoUpdate(media); 
		photoUpdate.setMessage("testing...");
		String postFeed = client.postPhoto(photoUpdate);
		
		System.out.println(postFeed);
	}
	
	@Test
	public void postVideo() throws Throwable{
		File mediaFile = new File("/Users/CHO/Desktop/test.png");
		Media media = new Media(mediaFile);
		VideoUpdate videoUpdate = new VideoUpdate(media); 
		videoUpdate.setDescription("welcome to~");
		videoUpdate.setTitle("title~~");
		String postFeed = client.postVideo(videoUpdate);
		
		System.out.println(postFeed);
	}
	
	@Test
	public void remove() throws Throwable{
		String postId = "100001104496172_672286376151506";
		boolean postFeed = client.deletePost(postId );
		
		System.out.println(postFeed);
	}
}
