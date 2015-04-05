package com.cocs.service.twitter;

import net.sf.json.JSONObject;

import org.apache.amber.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.commons.lang.NotImplementedException;

import twitter4j.Twitter;

import com.cocs.common.DefaultConstants;
import com.cocs.common.FileResource;
import com.cocs.handler.Params;
import com.cocs.handler.Response;
import com.cocs.handler.ResponseResult;
import com.cocs.handler.ResponseResultImpl;
import com.cocs.handler.ResponseSuccess;
import com.cocs.handler.ResponseSuccessImpl;
import com.cocs.service.ClientManager;
import com.cocs.service.IService;
import com.cocs.service.SocialService;
import com.cocs.webapp.api.exception.ApiException;

public class TwitterService extends SocialService implements DefaultConstants{

	private final static String VENDOR = "twitter";
	private static TwitterService instance = new TwitterService();
	
	private TwitterDAO twitterDAO = null;
	
	public TwitterService() {
		twitterDAO = new TwitterDAO();
	}
	
	public static TwitterService getInstance() {
		return instance;
	}

	@Override
	public IService setClient(ClientManager clientManager) {
		Twitter client = clientManager.getTwitterClient();
		if(client != null) {
			twitterDAO.setClient(client);
		}
		return this;
	}
	
	@Override
	public String getAuthorizeURL() {
		throw new NotImplementedException("OAtuth 2.0 is not supported.");
	}

	@Override
	public OAuthJSONAccessTokenResponse getTokenResponse(String code, String redirectUri) throws OAuthSystemException, OAuthProblemException {
		throw new NotImplementedException("OAtuth 2.0 is not supported.");
	}
	
	@Override
	public ResponseResult getUserInfo() throws ApiException {
		JSONObject result = twitterDAO.getUserInfo();
		
		ResponseResultImpl responseResult = Response.createResponseResult(VENDOR);
		responseResult.setResult(result);
		return responseResult;
	}

	@Override
	public ResponseSuccess createPost(Params params, FileResource fileResource) throws ApiException {
		String contents = params.get("contents");
		
		JSONObject result = null;
		if(fileResource == null) {
			result = twitterDAO.createPost(contents);
		} else {
			result = twitterDAO.createPostWithFile(contents, fileResource);
		}
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setId(result.getString("id"));
		return responseSuccess;
	}

	@Override
	public ResponseSuccess deletePost(Params params) throws ApiException {
		String id = params.get("id");
		
		twitterDAO.delePost(id);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		return responseSuccess;
	}

	
	
}
