package com.cocs.service.dropbox;

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
import org.apache.commons.lang.NotImplementedException;
import org.junit.Test;
import org.junit.internal.runners.statements.Fail;
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
	public void printAuthUrl() {
		String url = Env.getProperty("dropbox.authorize.url")+"?response_type=token&client_id="+Env.getProperty("dropbox.client.id")+"&redirect_uri="+Env.getProperty("dropbox.redirect.uri");
		System.out.println(url);
	}

	@Override
	@Test
	public void getAccessToken() throws OAuthSystemException, OAuthProblemException {
		String code = "AeNBg3pu-wwAAAAAAAAAAfW-vX1DYXZZnoQoDZyHyac";
		
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("dropbox.token.url"));
		requestBuilder.setCode(code);
		requestBuilder.setClientId(Env.getProperty("dropbox.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("dropbox.client.secret"));
		requestBuilder.setRedirectURI(Env.getProperty("dropbox.redirect.uri"));
		requestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE);
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(oAuthRequest);
		
		LOG.info("[access token : " + oAuthResponse.getAccessToken() + "]");
	    LOG.info("[user id : " + oAuthResponse.getParam("uid")+ "]");
	}
	
	@Override
	@Test
	public void revokeAccessToken() {
		throw new NotImplementedException();
//		String accessToken = "N7zmOUDpKXcAAAAAAAAAbXjWhfGOYHjrwIcg7Wy26OzmMsbksMrSaK4aaYbCWdis";
//		
//		GetMethod method = new GetMethod(Env.getProperty("dropbox.revokeToken.url"));
//		NameValuePair param = new NameValuePair("token",accessToken);
//        method.setQueryString(new NameValuePair[]{param});
//        
//		try {
//			int executeMethod = new HttpClient().executeMethod(method);
//			if(executeMethod == 200) {
//				LOG.info("Successfully revoke token");
//			} else {
//				LOG.info("fail");
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
	}
}
