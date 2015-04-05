package com.cocs.service.dropbox;

import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.sf.json.JSONObject;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import com.cocs.service.AbstractServiceTest;
import com.cocs.service.IServiceTest;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxEntry;
import com.dropbox.core.DbxEntry.File;
import com.dropbox.core.DbxEntry.WithChildren;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxStreamWriter;
import com.dropbox.core.DbxStreamWriter.InputStreamCopier;
import com.dropbox.core.DbxThumbnailFormat;
import com.dropbox.core.DbxThumbnailSize;
import com.dropbox.core.DbxUrlWithExpiration;
import com.dropbox.core.DbxWriteMode;

public class FileTest extends AbstractServiceTest implements DropBoxTestBase, IServiceTest.FileTest{
	private static DbxClient client;
	
	@BeforeClass
	public static void beforeClass() {
		client = getDropboxClient();
	}
	
	@Override
	@Test
	public void getFiles() {
		String parentPath = "/";
		
		try {
			WithChildren parent = client.getMetadataWithChildren(parentPath);
			for (DbxEntry file : parent.children) {
				if(file.isFile()) {
					LOG.debug(file.toStringMultiline());
				}
			}
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		}
	}

	@Override
	@Test
	public void retrieve() {
		String path = "/test.txt";
		
		try {
			DbxEntry file = client.getMetadata(path);
			LOG.debug(file.toStringMultiline());
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		}
	}

	@Override
	@Test
	public void copy() {
		String fromPath = "/cocs";
		String toPath = "/cocs4";
		
		try {
			DbxEntry copy = client.copy(fromPath, toPath+fromPath);
			LOG.debug(copy.toStringMultiline());
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		}
		
	}

	@Override
	@Test
	public void move() {
		String fromPath = "/cocs";
		String toPath = "/cocs_folder_rename";
		
		try {
			DbxEntry file = client.move(fromPath, toPath+fromPath);
			LOG.debug(file.toStringMultiline());
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		}
		
	}
	
	@Override
	@Test
	public void upload() throws IOException {
		String targetPath = "/test.txt";
		
		ClassPathResource classPathResource = new ClassPathResource("test.txt");
		InputStream fis = new FileInputStream(classPathResource.getFile());
		
		try {
			File file = client.uploadFile(targetPath, DbxWriteMode.add(), -1, fis);
			LOG.debug(file.toStringMultiline());
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}
	
	@Test
	public void chunkedUpload() throws FileNotFoundException, IOException {
		String targetPath = "/test.txt";
		
		ClassPathResource classPathResource = new ClassPathResource("test.txt");
		InputStream fis = new FileInputStream(classPathResource.getFile());
		
		DbxStreamWriter<IOException> writer = new InputStreamCopier(fis);
		
		try {
			File file = client.uploadFileChunked(1000, targetPath, DbxWriteMode.add(), -1, writer);
			LOG.debug(file.toStringMultiline());
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		} finally {
			IOUtils.closeQuietly(fis);
		}
		
	}
	
	@Override
	@Test
	public void download() throws IOException {
		String path = "/test.txt";
		
		ClassPathResource classPathResource = new ClassPathResource("test.txt");
		FileOutputStream fos = new FileOutputStream(classPathResource.getFile());
		
		try {
			File file = client.getFile(path, null, fos);
			LOG.debug(file.toStringMultiline());
		} catch (DbxException e) {
			LOG.error(e.getMessage());
			fail();
		} finally {
			IOUtils.closeQuietly(fos);
		}
		
	}
	
	@Test
	public void getFilesWithHttpClient() throws NullPointerException, HttpException, IOException {
		String path = "/";
		String restURL = "https://api.dropbox.com/1/metadata/auto" + path;
		
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod();
		method.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=UTF-8");

		method.setURI(new URI(restURL, false));
		method.setParameter("access_token", DroprobxDB.ACCESS_TOKEN);
//		method.setParameter("include_deleted", "true");
		method.setParameter("oauth_consumer_key", "837dmb1jbgwfsfr");

		client.executeMethod(method);
		JSONObject fromObject = JSONObject.fromObject(method.getResponseBodyAsString());
		LOG.debug(fromObject.toString(4));
	}
	
	@Test
	public void getPriviewLink() throws DbxException {
		String path = "/17895.jpg";
		DbxUrlWithExpiration createTemporaryDirectUrl = client.createTemporaryDirectUrl(path);
		LOG.debug(createTemporaryDirectUrl.url);
	}
	
	@Test
	public void getThumbnailLink() throws DbxException, IOException {
		String path = "/17895.jpg";
		OutputStream target = new ByteArrayOutputStream();
		File file = client.getThumbnail(DbxThumbnailSize.w128h128 , DbxThumbnailFormat.JPEG, path, null, target);
		target.close();
		LOG.debug(file.toStringMultiline());
	}
}
