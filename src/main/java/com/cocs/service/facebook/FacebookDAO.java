package com.cocs.service.facebook;

import net.sf.json.JSONObject;

import com.cocs.common.FileResource;
import com.cocs.webapp.api.exception.ApiException;

import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.Media;
import facebook4j.PhotoUpdate;
import facebook4j.User;

public class FacebookDAO {

	private final static String VENDOR = "facebook";
	
	private Facebook client = null;
	
	public void setClient(Facebook client) {
		this.client = client;
	}

	public JSONObject getUserInfo() throws ApiException {
		JSONObject result = new JSONObject();
		try {
			User me = client.getMe();
			
			result.put("name", me.getName());
		} catch (Exception e) {
			throw new ApiException(e.getMessage()).setVendor(VENDOR);
		}
		return result;
	}
	
	public JSONObject createPost(String contents) throws ApiException {
		JSONObject result = new JSONObject();
		try {
			String updateStatus = client.postStatusMessage(contents);
			result.put("id", updateStatus);
		} catch (Exception e) {
			if(e instanceof FacebookException) {
				if(((FacebookException) e).getErrorCode() == 506) {
					throw new ApiException("콘텐츠 내용이 적합하지 않습니다.").setVendor(VENDOR);
				}
			}
			throw new ApiException(e.getMessage()).setVendor(VENDOR);
		}
		return result;
	}
	
	public JSONObject createPostWithFile(String contents, FileResource fileResource) throws ApiException {
		JSONObject result = new JSONObject();
		try {
			Media media = new Media(fileResource.getName(), fileResource.getInputStream());
			PhotoUpdate photoUpdate = new PhotoUpdate(media);
			photoUpdate.setMessage(contents);
			String updateStatus = client.postPhoto(photoUpdate);
			result.put("id", updateStatus);
		} catch (Exception e) {
			throw new ApiException(e.getMessage()).setVendor(VENDOR);
		} finally {
			fileResource.deleteFile();
		}
		return result;
	}

	public boolean deletePost(String id) throws ApiException {
		try {
			return client.deletePost(id);
		} catch (Exception e) {
			throw new ApiException(e.getMessage()).setVendor(VENDOR);
		}
	}
	
	
}
