package com.cocs.service;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import com.cocs.common.Env;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxWriteMode;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.ParentReference;

public class CommonServiceTest extends AbstractServiceTest{
	private static DbxClient dropboxClient;
	private static Drive googleClient;
	
	@BeforeClass
	public static void beforeclass(){
		dropboxClient = getDropboxClient();
		googleClient = getGoogleClient();
	}
	
	@Test
	public void copyGoogleToDropbox() throws IOException {
		String googleId = "0BxwX5AEkRSZLMTh1aDM1NWwyMDg";
		String dropboxParentPath = "/";
		
		com.google.api.services.drive.model.File googleFile = googleClient.files().get(googleId).execute();
		
		String downloadUrl = googleFile.getDownloadUrl();
		if(StringUtils.isBlank(downloadUrl)) {
			downloadUrl = googleFile.getExportLinks().get("application/pdf");
		}
		String title = googleFile.getTitle();
		LOG.debug(title);
		
		HttpResponse response = googleClient.getRequestFactory().buildGetRequest(new GenericUrl(downloadUrl)).execute();
		InputStream inputStream = response.getContent();
		
		try {
			com.dropbox.core.DbxEntry.File file = dropboxClient.uploadFile(dropboxParentPath+title, DbxWriteMode.add(), -1, inputStream);
			LOG.debug(file.toStringMultiline());
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		} finally {
			IOUtils.closeQuietly(inputStream);
		}
		
	}
	
	@Test
	public void copyDropboxToGoogle() throws IOException {
		String dropboxPath = "/O_3xoPsL.exe (1).part";
		String googleParentId = "root";
		String fileName = null;
		String fileMimeType = null;
		
		File target = File.createTempFile(Long.toString(System.currentTimeMillis()), null, new File(Env.getRepositoryUploadDirPath()));
		OutputStream fos = new FileOutputStream(target);
		
		try {
			com.dropbox.core.DbxEntry.File file = dropboxClient.getFile(dropboxPath, null, fos);
			LOG.debug(file.toStringMultiline());
			fileName = file.name;
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		} finally {
			IOUtils.closeQuietly(fos);
		}
		
//		Tika tika = new Tika();
//		fileMimeType = tika.detect(target);
//	    LOG.debug(fileMimeType);
	    
		com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
	    body.setTitle(fileName);
	    body.setParents(Arrays.asList(new ParentReference().setId(googleParentId)));
	    
	    FileContent mediaContent = new FileContent(fileMimeType, target);
		
		com.google.api.services.drive.model.File file = googleClient.files().insert(body, mediaContent).execute();
		LOG.debug(file.toPrettyString());
		
		target.delete();
	}
	
}
