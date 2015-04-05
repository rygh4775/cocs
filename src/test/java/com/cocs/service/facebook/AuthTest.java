package com.cocs.service.facebook;

import java.io.IOException;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.amber.oauth2.client.response.GitHubTokenResponse;
import org.apache.amber.oauth2.common.OAuth;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.amber.oauth2.common.message.types.GrantType;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.NotImplementedException;
import org.codehaus.jettison.json.JSONException;
import org.json.simple.parser.ParseException;
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
		String url = Env.getProperty("facebook.authorize.url")+"?scope=email,publish_actions&client_id="+Env.getProperty("facebook.client.id")+"&redirect_uri="+Env.getProperty("facebook.redirect.uri");
		System.out.println(url);
	}

	@Override
	@Test
	public void getAccessToken() throws OAuthSystemException, OAuthProblemException {
		String code = "AQClRYyjQ1erS4QUF1jL_Vew5yZ-k3uobQb0qmTNHFCI1ty6ZovWy4Kfozu5TTMOfndU3aQ2r_17_3OAPrN8KRxVLPqgOlWl6A4KHLpnUlhTM6FMCBggw1W3el4JVsRfgz0aNPtyQZ7ITHeI5rTMjQ6B3h7rnua5AP9Bbg3H1ARitakrfBnLyVozhS9kN-LZ24S5L-OlS9rhy-Exn7AnEBhczR7rCsuMPni22Nfwa5xMaxdYM55zhHsTV_8s9SS8wrWA5koKAp67_RBhuIyFnSTGPKgodkxSXCTFsUTXogdsAKteK_NsTbezZe9IRmnKLeM#_=_";
//		
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("facebook.token.url"));
		requestBuilder.setCode(code);
		requestBuilder.setClientId(Env.getProperty("facebook.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("facebook.client.secret"));
		requestBuilder.setRedirectURI(Env.getProperty("facebook.redirect.uri"));
		requestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE);
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		GitHubTokenResponse oAuthResponse = oAuthClient.accessToken(oAuthRequest, GitHubTokenResponse.class);
		LOG.info("[access token : " + oAuthResponse.getAccessToken() + " ]");
		LOG.info("[expires in : " + oAuthResponse.getExpiresIn() + "]");
		LOG.info("[expires : " + oAuthResponse.getParam("expires") + "]");
	}
	
	@Test
	public void getAppAccessToken() throws OAuthSystemException, OAuthProblemException {
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("facebook.token.url"));
		requestBuilder.setClientId(Env.getProperty("facebook.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("facebook.client.secret"));
		requestBuilder.setGrantType(GrantType.CLIENT_CREDENTIALS);
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		GitHubTokenResponse oAuthResponse = oAuthClient.accessToken(oAuthRequest, GitHubTokenResponse.class);
		LOG.info("[access token : " + oAuthResponse.getAccessToken() + " ]");
		LOG.info("[expires : " + oAuthResponse.getParam("expires") + "]");
	}
	
	@Test
	public void exchangeToken() throws OAuthSystemException, OAuthProblemException {
		String accessToken = "CAAIiscHVe6EBAButMk4cAFMz1RD3PR381iYALQNf7cRYDOxTCMaOnozvTBW5DaXd5UPQKmYpCfOViFZA39bnNFIe35eAFVQ4UlMwZCqEqZBREs9ijZCZCMbNZAjZC4nw2V2LegaJfdfav0m8vmdZCQ8Rf4nWrO50kxzSkjxrVnTOUc5sZBQSHyIAN3mv7ZBYL8VIcZD";
		
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("facebook.token.url"));
		requestBuilder.setClientId(Env.getProperty("facebook.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("facebook.client.secret"));
		requestBuilder.setParameter(OAuth.OAUTH_GRANT_TYPE, "fb_exchange_token");
		requestBuilder.setParameter("fb_exchange_token", accessToken);
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		GitHubTokenResponse oAuthResponse = oAuthClient.accessToken(oAuthRequest, GitHubTokenResponse.class);
		LOG.info("[access token : " + oAuthResponse.getAccessToken() + " ]");
		LOG.info("[expires : " + oAuthResponse.getParam("expires") + "]");
	}
	
	@Test
	public void getDebugToken() throws OAuthSystemException, OAuthProblemException, HttpException, IOException, JSONException, ParseException {
		String inputToken = "CAAIiscHVe6EBAH7YsXJ8EzDZCkBPRnRqHvBdCMqN4SwMr6fw5Hwcu2zEwgMbvsuSZCT4MBrvuHlF5QOovkzXyk66rprYRNTas3jaCM94O45O2VZBXMDvoiILcyRiPCOSsEjKCs0hz9EaTvmLg5RTjkDDCZCQXN71ghIxPcu8kwXcFRk4Vbu49oeXuujAdWkZD";
		String accessToken = "601096809970593|Bxfl9KyBdGlJ8lKWfvn8RRwtsXo";
		
		GetMethod method = new GetMethod(Env.getProperty("facebook.debug.token.url"));
		NameValuePair param1 = new NameValuePair("input_token",inputToken);
        NameValuePair param2 = new NameValuePair("access_token",accessToken);
        method.setQueryString(new NameValuePair[]{param1,param2});
		new HttpClient().executeMethod(method);
		
		JSONObject json = (JSONObject) JSONSerializer.toJSON(method.getResponseBodyAsString());
		System.out.println(json.toString(4));
	}

	@Override
	public void revokeAccessToken() {
		throw new NotImplementedException();
	}
	
}
