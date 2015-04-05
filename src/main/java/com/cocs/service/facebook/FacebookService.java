package com.cocs.service.facebook;

import java.io.InputStream;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.amber.oauth2.client.response.GitHubTokenResponse;
import org.apache.amber.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.amber.oauth2.common.OAuth;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.amber.oauth2.common.message.types.GrantType;
import org.springframework.web.multipart.MultipartFile;

import com.cocs.common.DefaultConstants;
import com.cocs.common.Env;
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

import facebook4j.Facebook;

public class FacebookService extends SocialService implements DefaultConstants{

	private final static String VENDOR = "facebook";
	private static FacebookService instance = new FacebookService();
	
	private FacebookDAO facebookDAO = null;
	
	public FacebookService() {
		facebookDAO = new FacebookDAO();
	}
	
	public static FacebookService getInstance() {
		return instance;
	}

	@Override
	public IService setClient(ClientManager clientManager) {
		Facebook client = clientManager.getFacebookClient();
		if(client != null) {
			facebookDAO.setClient(client);
		}
		return this;
	}
	
	@Override
	public String getAuthorizeURL() {
		String url = Env.getProperty("facebook.authorize.url")+"?scope=email,publish_actions&client_id="+Env.getProperty("facebook.client.id")+"&redirect_uri="+Env.getProperty("facebook.redirect.uri");
		return url;
	}

	@Override
	public OAuthAccessTokenResponse getTokenResponse(String code, String redirectUri) throws OAuthSystemException, OAuthProblemException {
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("facebook.token.url"));
		requestBuilder.setCode(code);
		requestBuilder.setClientId(Env.getProperty("facebook.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("facebook.client.secret"));
		requestBuilder.setRedirectURI(redirectUri);
		requestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE);
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		GitHubTokenResponse accessToken = oAuthClient.accessToken(oAuthRequest, GitHubTokenResponse.class);
		
		return exchangeToken(accessToken.getAccessToken());
	}
	
	public OAuthAccessTokenResponse exchangeToken(String accessToken) throws OAuthSystemException, OAuthProblemException {
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("facebook.token.url"));
		requestBuilder.setClientId(Env.getProperty("facebook.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("facebook.client.secret"));
		requestBuilder.setParameter(OAuth.OAUTH_GRANT_TYPE, "fb_exchange_token");
		requestBuilder.setParameter("fb_exchange_token", accessToken);
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		GitHubTokenResponse oAuthResponse = oAuthClient.accessToken(oAuthRequest, GitHubTokenResponse.class);
		
		return oAuthResponse;
	}
	
	@Override
	public ResponseResult getUserInfo() throws ApiException {
		JSONObject result = facebookDAO.getUserInfo();
		
		ResponseResultImpl responseResult = Response.createResponseResult(VENDOR);
		responseResult.setResult(result);
		return responseResult;
	}


	@Override
	public ResponseSuccess createPost(Params params, FileResource fileResource) throws ApiException {
		String contents = params.get("contents");
		JSONObject result = null;
		if(fileResource == null) {
			result = facebookDAO.createPost(contents);
		} else {
			result = facebookDAO.createPostWithFile(contents, fileResource);
		}
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setId(result.getString("id"));
		return responseSuccess;
	}

	@Override
	public ResponseSuccess deletePost(Params params) throws ApiException {
		String id = params.get("id");
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		if(!facebookDAO.deletePost(id)) {
			responseSuccess.setSuccess(false);
		}
		return responseSuccess;
	}
	
	
}
