package com.cocs.service.google;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileCopyUtils;

import com.cocs.service.AbstractServiceTest;
import com.cocs.service.IServiceTest;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml"})
public class FileTest extends AbstractServiceTest implements IServiceTest.FileTest {
	private static Drive client;
	
	@BeforeClass
	public static void beforeClass() {
		client = getGoogleClient();
	}
	
	@Override
	@Test
	public void getFiles() throws IOException {
		String parentId = "root";
		
		Files.List request = null;
		request = client.files().list();
		request.setQ("'"+parentId+"' in parents");
		FileList files = request.execute();

		for (File file : files.getItems()) {
			if(file.getLabels().getTrashed()) {
				LOG.debug(file.toPrettyString());
			}
		}

	}
	
	@Override
	@Test
	public void retrieve() throws IOException {
		String id = "0BxwX5AEkRSZLdF9kNjJJZUExOTA";
		
		File file = client.files().get(id).execute();
		LOG.debug(file.toPrettyString());
	}
	
	@Override
	@Test
	public void copy() throws IOException {
		String id = "0BxwX5AEkRSZLcjR6WkdQQmVfeW8";	//04_html5_file_dragdrop.zip
		String parentId = "0BxwX5AEkRSZLX240Tng4WlJrbjg";	//2011강북
		
		String title = client.files().get(id).execute().getTitle();
		
		File newFile = new File();
		newFile.setTitle(title);
		newFile.setParents(Arrays.asList(new ParentReference().setId(parentId)));
		
		File file = client.files().copy(id, newFile).execute();
		LOG.debug(file.toPrettyString());
	}
	
	@Override
	@Test
	public void move() throws IOException {
		String id = "0BxwX5AEkRSZLVnhHQzdKbkt4eHM";	//04_html5_file_dragdrop.zip
		String parentId = "0BxwX5AEkRSZLX240Tng4WlJrbjg";	//2011강북
		
		String title = client.files().get(id).execute().getTitle();
		
		File newFile = new File();
		newFile.setTitle(title);
		newFile.setParents(Arrays.asList(new ParentReference().setId(parentId)));
		
		File file = client.files().copy(id, newFile).execute();
		LOG.debug(file.toPrettyString());
		
		//delete original file
		client.files().delete(id).execute();
	}
	
	@Override
	@Test
	public void upload() throws IOException {
		String parentId = "root";
	    
		ClassPathResource classPathResource = new ClassPathResource("test.txt");
		
		File body = new File();
	    body.setTitle(classPathResource.getFilename());
	    body.setParents(Arrays.asList(new ParentReference().setId(parentId)));
	    
	    FileContent mediaContent = new FileContent("text/plain", classPathResource.getFile());
		
		File file = client.files().insert(body, mediaContent).execute();
		LOG.debug(file.toPrettyString());
	}

	@Override
	@Test
	public void download() throws IOException {
		
		String fileId = "0BxwX5AEkRSZLVkExTEVmdGNQWEk";
		
		File file = client.files().get(fileId).execute();
		
		String downloadUrl = file.getDownloadUrl();
		if(StringUtils.isBlank(downloadUrl)) {
			downloadUrl = file.getExportLinks().get("application/pdf");
		}
		
		HttpResponse response = client.getRequestFactory().buildGetRequest(new GenericUrl(downloadUrl)).execute();
		
		ClassPathResource classPathResource = new ClassPathResource("test.txt");
		FileOutputStream fos = new FileOutputStream(classPathResource.getFile());
		
		FileCopyUtils.copy(response.getContent(), fos);
	}
	
	@Test
	public void search() {
		Files.List request = null;
		try {
			request = client.files().list();
			request.setQ("mimeType != 'application/vnd.google-apps.folder' and title contains 'co'"); // parentId
			FileList files = request.execute();

			for (File file : files.getItems()) {
				List<ParentReference> parents = file.getParents();
				for (ParentReference parentReference : parents) {
					System.out.println(parentReference.getParentLink());
				}
				LOG.debug("[File title : " + file.getTitle() + "]");
			}
		} catch (IOException e) {
			System.out.println("An error occurred: " + e);
			request.setPageToken(null);
		}
		    
	}
	
	@Test
	public void pagingTest() {
	    Files.List request = null;
	    String nextPageToken = null;
	    do {
	      try {
	    	request = client.files().list();
	    	request.setQ("'0BxwX5AEkRSZLTHhYZmJ5bUQ1RHc' in parents");	//parentId
	    	request.setMaxResults(2);	//list number
	    	request.setPageToken(nextPageToken);	//next list token
	        FileList files = request.execute();
	        
	        nextPageToken = files.getNextPageToken();
	        request.setPageToken(nextPageToken);
	        
	        LOG.debug("[File pageToken : " + request.getPageToken() + "]");
	        
	        for (File file : files.getItems()) {
				LOG.debug("[File title : " + file.getTitle() + "]");
			}
	      } catch (IOException e) {
	    	LOG.error(e.getMessage());
	        System.out.println("An error occurred: " + e);
	        request.setPageToken(null);
	      }
	    } while (request.getPageToken() != null && request.getPageToken().length() > 0);
	    
	}
	
	/**
	 * downloadUrl, alternateLink - 인증 필요(로그인 세션 필요)
	 * @throws IOException
	 */
	@Test
	public void getPreviewLink() throws IOException {
		String parentId = "0BxwX5AEkRSZLX240Tng4WlJrbjg";
		
		Files.List request = null;
		request = client.files().list();
		request.setQ("'"+parentId+"' in parents");
		FileList files = request.execute();
		
		for (File file : files.getItems()) {
			LOG.debug(file.getTitle());
			LOG.debug(file.getAlternateLink());
			LOG.debug(file.getDownloadUrl());
			LOG.debug(file.getEmbedLink());	
			LOG.debug(file.getExportLinks());
			LOG.debug(file.getWebContentLink());
			LOG.debug(file.getWebViewLink());
			System.out.println();
		}
		    
	}
}
