package com.cocs.service.google;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cocs.common.DefaultConstants;
import com.cocs.service.AbstractServiceTest;
import com.cocs.service.IServiceTest;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml"})
public class FolderTest extends AbstractServiceTest implements IServiceTest.FolderTest{
	private static Drive client;
	
	@BeforeClass
	public static void beforeClass() {
		client = getGoogleClient();
	}
	
	@Override
	@Test
	public void create() throws IOException {
		String parentId = "root";
		String title = "cocs_folder_create";
		
		File body = new File();
		body.setTitle(title);
	    body.setMimeType("application/vnd.google-apps.folder");
		body.setParents(Arrays.asList(new ParentReference().setId(parentId)));
		
    	File file = client.files().insert(body).execute();
    	LOG.debug(file.toPrettyString());
	}

	@Override
	@Test
	public void rename() throws IOException {
		String id = "0BxwX5AEkRSZLNDZaNzk0cXQzSWM";
		String newTitle = "cocs_folder_renamed";
		
		File body = new File();
		body.setTitle(newTitle);
		
		File file = client.files().patch(id, body).execute();
		LOG.debug(file.toPrettyString());
	}

	@Override
	@Test
	public void copy() {
		LOG.warn("forlder can't be copied");
	}
	
	@Override
	@Test
	public void move() throws IOException {
		String id = "0BxwX5AEkRSZLUS0tMDdLYWsyaVE";	//hi
		String targetParentId = "0BxwX5AEkRSZLcDUwd09aLW5tSFk";	//인코딩테스트
		
		String originalParentId = client.files().get(id).execute().getParents().get(0).getId();
		LOG.debug(originalParentId);
		
		client.parents().insert(id, new ParentReference().setId(targetParentId)).execute();
		
		//after new parent created successfully
		client.parents().delete(id, originalParentId).execute();
		
	}
	
	@Test
	public void getFolders() throws IOException {
		String parentId = "root";
		
		Files.List request = null;
		request = client.files().list();
		request.setQ("'" + parentId + "' in parents and mimeType = 'application/vnd.google-apps.folder'");
		FileList files = request.execute();

		for (File file : files.getItems()) {
			List<ParentReference> parents = file.getParents();
			for (ParentReference parentReference : parents) {
				System.out.println(parentReference.getParentLink());
			}
			LOG.debug(file.toPrettyString());
		}
		    
	}

}
