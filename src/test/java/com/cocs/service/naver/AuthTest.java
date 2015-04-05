package com.cocs.service.naver;

import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.amber.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.amber.oauth2.common.message.types.GrantType;
import org.apache.commons.lang.NotImplementedException;
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
	public void printAuthUrl() {
		String url = Env.getProperty("naver.authorize.url")+"?response_type=code&state=green&client_id="+Env.getProperty("naver.client.id")+"&redirect_uri="+Env.getProperty("naver.redirect.signin.uri");
		System.out.println(url);
	}

	@Override
	@Test
	public void getAccessToken() throws OAuthSystemException, OAuthProblemException {
		String code = "iE75b7g4XnLkZww2";
		
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("naver.token.url"));
		requestBuilder.setCode(code);
		requestBuilder.setClientId(Env.getProperty("naver.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("naver.client.secret"));
		requestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE);
		requestBuilder.setParameter("state", "green");
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(oAuthRequest);
		LOG.info("[access token : " + oAuthResponse.getAccessToken() + " ]");
		LOG.info("[expires in : " + oAuthResponse.getExpiresIn() + "]");
		LOG.info("[refresh token : " + oAuthResponse.getRefreshToken() + "]");
	}

	@Override
	public void revokeAccessToken() {
		throw new NotImplementedException();
	}
	
}
