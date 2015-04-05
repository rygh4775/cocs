package com.cocs.service.google;

import java.io.IOException;

import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.amber.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.amber.oauth2.common.message.types.GrantType;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cocs.common.Env;
import com.cocs.service.AbstractServiceTest;
import com.cocs.service.IServiceTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml"})
public class AuthTest extends AbstractServiceTest implements IServiceTest.AuthTest{
	
	@Override
	@Test
	public void printAuthUrl(){
		String url = Env.getProperty("google.authorize.url")+"?response_type=code&client_id="+Env.getProperty("google.client.id")+"&redirect_uri="+Env.getProperty("google.redirect.uri")+"&scope=https://www.googleapis.com/auth/drive";
		url += "&approval_prompt=force";
		url += "&access_type=offline";
		System.out.println(url);
	}
	
	@Override
	@Test
	public void getAccessToken() throws OAuthSystemException, OAuthProblemException {
		String code = "4/DG0M8zuyB0MccvkMjPMoGamRsFlH.QsZ7Zy8oFPEVXE-sT2ZLcbTZzbP7iAI";
		
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("google.token.url"));
		requestBuilder.setCode(code);
		requestBuilder.setClientId(Env.getProperty("google.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("google.client.secret"));
		requestBuilder.setRedirectURI(Env.getProperty("google.redirect.uri"));
		requestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE);
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(oAuthRequest);
		
		LOG.info("[access token : " + oAuthResponse.getAccessToken() + "]");
	    LOG.info("[expires in seconds : " + oAuthResponse.getExpiresIn() + "]");
	    LOG.info("[refresh token : " + oAuthResponse.getRefreshToken() + "]");
	}
	
	@Test
	public void refreshAccessToken() throws OAuthSystemException, OAuthProblemException{
		String refreshToken = "1/NoAXV7_vsx12z-FlTKm21d04Ct2XrGKZLORG4eDIH4s";
		
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("google.token.url"));
		requestBuilder.setRefreshToken(refreshToken);
		requestBuilder.setClientId(Env.getProperty("google.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("google.client.secret"));
		requestBuilder.setGrantType(GrantType.REFRESH_TOKEN);
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(oAuthRequest);
		
		LOG.info("[access token : " + oAuthResponse.getAccessToken() + "]");
	    LOG.info("[expires in seconds : " + oAuthResponse.getExpiresIn() + "]");
	    LOG.info("[refresh token : " + oAuthResponse.getRefreshToken() + "]");
	}

	@Override
	@Test
	public void revokeAccessToken() {
		String accessToken = "1/NoAXV7_vsx12z-FlTKm21d04Ct2XrGKZLORG4eDIH4s";
		
		GetMethod method = new GetMethod(Env.getProperty("google.revokeToken.url"));
		NameValuePair param = new NameValuePair("token",accessToken);
        method.setQueryString(new NameValuePair[]{param});
        
		try {
			int executeMethod = new HttpClient().executeMethod(method);
			if(executeMethod == 200) {
				LOG.info("Successfully revoke token");
			} else {
				LOG.info("fail");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
//	@Test
//	public void authorize() {
//		HttpTransport httpTransport = new NetHttpTransport();
//	    JsonFactory jsonFactory = new JacksonFactory();
//	   
//	    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//	        httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
//	    	.setAccessType("offline")	// for refresh token
////	    	.setApprovalPrompt("force")	// for refresh token
//	        .setApprovalPrompt("auto").build();
//	    
//	    String url = flow.newAuthorizationUrl().setRedirectUri(REDIRECT_URI).build();
//	    System.out.println("Please open the following URL in your browser then type the authorization code:");
//	    System.out.println("  " + url);
//	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//	    String code = null;
//		try {
//			code = br.readLine();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//	    GoogleTokenResponse response = null;
//		try {
//			response = flow.newTokenRequest(code).setGrantType("authorization_code").setRedirectUri(REDIRECT_URI).execute();
//		} catch (IOException e) {
//			e.printStackTrace();
//			fail();
//		}
//		
//	    LOG.info("[access token : " + response.getAccessToken() + "]");
//	    LOG.info("[expires in seconds : " + response.getExpiresInSeconds() + "]");
//	    LOG.info("[token type : " + response.getTokenType() + "]");
//	    LOG.info("[refresh token : " + response.getRefreshToken() + "]");
//	    LOG.info("[scope : " + response.getScope() + "]");
//	}
//	
//	@Test
//	public void refresshToken() {
//		HttpTransport httpTransport = new NetHttpTransport();
//	    JsonFactory jsonFactory = new JacksonFactory();
//	    
//		 GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
//			        httpTransport, jsonFactory, CLIENT_ID, CLIENT_SECRET, Arrays.asList(DriveScopes.DRIVE))
////			    	.setAccessType("offline")	// for refresh token
////			    	.setApprovalPrompt("force")	// for refresh token
////			        .setApprovalPrompt("auto")
//			        .build();
//		 GoogleTokenResponse response = null;
//		 try {
//			response = flow.newTokenRequest("1/ppHfNpfOB61Ggf7aQAy-8DJwr1qY6v3W5VsVMwk8ke8").setGrantType("refresh_token").execute();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		 
//	 	LOG.info("[access token : " + response.getAccessToken() + "]");
//	    LOG.info("[expires in seconds : " + response.getExpiresInSeconds() + "]");
//	    LOG.info("[token type : " + response.getTokenType() + "]");
//	    LOG.info("[refresh token : " + response.getRefreshToken() + "]");
//	    LOG.info("[scope : " + response.getScope() + "]");
//	}
}
