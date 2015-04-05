package com.cocs.service.twitter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import net.sf.json.JSONObject;
import twitter4j.AccountSettings;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.cocs.common.Env;
import com.cocs.common.FileResource;
import com.cocs.webapp.api.exception.ApiException;

import facebook4j.FacebookException;

public class TwitterDAO {

	private final static String VENDOR = "twitter";
	
	private Twitter client = null;
	
	public void setClient(Twitter client) {
		this.client = client;
	}

	public JSONObject getUserInfo() throws ApiException {
		JSONObject result = new JSONObject();
		try {
			AccountSettings accountSettings = client.getAccountSettings();
			result.put("name", accountSettings.getScreenName());
		} catch (Exception e) {
			throw new ApiException(e.getMessage()).setVendor(VENDOR);
		}
		return result;
	}
	
	public JSONObject createPost(String contents) throws ApiException {
		JSONObject result = new JSONObject();
		try {
			StatusUpdate statusUpdate = new StatusUpdate(contents);
			Status updateStatus = client.updateStatus(statusUpdate);
			result.put("id", Long.toString(updateStatus.getId()));
		} catch (Exception e) {
			if(e instanceof TwitterException) {
				if(((TwitterException) e).getErrorCode() == 187) {
					throw new ApiException("콘텐츠 내용이 적합하지 않습니다.").setVendor(VENDOR);
				}
			}
			throw new ApiException(e.getMessage()).setVendor(VENDOR);
		}
		return result;
	}

	public JSONObject createPostWithFile(String contents, FileResource fileResource) throws ApiException {
		JSONObject result = new JSONObject();
		File tmpFile = null;
		OutputStream fos = null;
		try {
			StatusUpdate statusUpdate = new StatusUpdate(contents);
			tmpFile = fileResource.getFile();
			if(tmpFile == null) {
				tmpFile = new File(Env.getRepositoryUploadDirPath() + "/" + fileResource.getName());
				fos = new FileOutputStream(tmpFile);
				FileCopyUtils.copy(fileResource.getInputStream(), fos);
			}
//			file.transferTo(tmpFile);
			statusUpdate.setMedia(tmpFile);
			Status updateStatus = client.updateStatus(statusUpdate);
			result.put("id", Long.toString(updateStatus.getId()));
		} catch (Exception e) {
			throw new ApiException(e.getMessage()).setVendor(VENDOR);
		} finally {
			tmpFile.delete();
			if(fos != null) {
				IOUtils.closeQuietly(fos);
			}
			fileResource.deleteFile();
		}
		return result;
	}
	
	public void delePost(String id) throws ApiException {
		try {
			client.destroyStatus(Long.parseLong(id));
		} catch (Exception e) {
			throw new ApiException(e.getMessage()).setVendor(VENDOR);
		}
	}

}
