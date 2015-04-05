package com.cocs.service.dropbox;

import static org.junit.Assert.fail;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cocs.service.AbstractServiceTest;
import com.cocs.service.IServiceTest;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.DbxEntry.Folder;
import com.dropbox.core.DbxException;

public class FolderTest extends AbstractServiceTest implements IServiceTest.FolderTest{

	private static DbxClient client;
	
	@BeforeClass
	public static void beforeClass() {
		client = getDropboxClient();
	}
	
	@Override
	@Test
	public void create() {
		String path = "/cocs";
		
		try {
			Folder folder = client.createFolder(path);
			LOG.debug(folder.toStringMultiline());
			if(folder == null){
				LOG.debug("The name may be already exists.");
				LOG.debug("DbxException doesn't catch same name.");
				fail();
			}
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		}
	}

	@Override
	@Test
	public void rename() {
		String originalPath = "/cocs";
		String name = "cocs";
		
		String[] pathArray = originalPath.split("/");
		pathArray[pathArray.length-1] = name;
		String movePath = StringUtils.join(pathArray, "/");
		LOG.debug(movePath);
		
		try {
			DbxEntry folder = client.move(originalPath, movePath);
			LOG.debug(folder.toStringMultiline());
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		}
	}

	@Override
	@Test
	public void move() {
		String path = "/cocs_folder_create";
		String targetParentPath = "/passport.jpg";
		
		String[] pathArray = path.split("/");
		String name = pathArray[pathArray.length-1];
		
		try {
			DbxEntry file = client.move(path, targetParentPath+"/"+name);
			LOG.debug(file.toStringMultiline());
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		}
	}
	
	@Override
	@Test
	public void copy() {
		String path = "/cocs_folder_renamecocs_folder_create";
		String targetParentPath = "/cocs_folder_rename";
		
		String[] pathArray = path.split("/");
		String name = pathArray[pathArray.length-1];
		
		try {
			DbxEntry file = client.copy(path, targetParentPath+"/"+name);
			LOG.debug(file.toStringMultiline());
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		}
	}
	
}
