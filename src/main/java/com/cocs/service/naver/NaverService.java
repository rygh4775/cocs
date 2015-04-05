package com.cocs.service.naver;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.amber.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.amber.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.amber.oauth2.common.message.types.GrantType;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.cocs.common.Env;
import com.cocs.webapp.api.exception.ApiException;

public class NaverService {
	
	public String getAuthorizeURL() {
		String url = Env.getProperty("naver.authorize.url")+"?response_type=code&state=green&client_id="+Env.getProperty("naver.client.id")+"&redirect_uri="+Env.getProperty("naver.redirect.signin.uri");
		return url;
	}

	public OAuthAccessTokenResponse getTokenResponse(String code) throws OAuthSystemException, OAuthProblemException {
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("naver.token.url"));
		requestBuilder.setCode(code);
		requestBuilder.setClientId(Env.getProperty("naver.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("naver.client.secret"));
		requestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE);
		requestBuilder.setParameter("state", "green");
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(oAuthRequest);
		return oAuthResponse;
	}
	
	public String getEmail(String accessToken) throws ApiException {
		String email = null;
		
		try {
			HttpClient client = new HttpClient();
		
			HttpMethod method = new PostMethod("https://apis.naver.com/nidlogin/nid/getUserProfile.xml");
			method.setRequestHeader("Authorization", "Bearer " + accessToken);
			client.executeMethod(method);
		
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(method.getResponseBodyAsString()));
            Document doc = builder.parse(is);
            NodeList list = doc.getElementsByTagName("email");
            email = list.item(0).getTextContent();
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
			throw new ApiException(e);
		}
		return email;
	}
}
