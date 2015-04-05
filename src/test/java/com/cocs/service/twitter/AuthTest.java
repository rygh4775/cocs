package com.cocs.service.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.cocs.common.Env;
import com.cocs.service.AbstractServiceTest;
import com.cocs.service.IServiceTest;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml"})
public class AuthTest extends AbstractServiceTest implements IServiceTest.AuthTest{

	@Override
	@Test
	public void printAuthUrl() {
		Twitter twitter = TwitterFactory.getSingleton();
	    twitter.setOAuthConsumer(Env.getProperty("twitter.client.id"), Env.getProperty("twitter.client.secret"));
	    RequestToken requestToken = null;
		try {
			requestToken = twitter.getOAuthRequestToken();
			System.out.println(requestToken.getToken());
			System.out.println(requestToken.getTokenSecret());
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	    System.out.println("Open the following URL and grant access to your account:");
	    System.out.println(requestToken.getAuthorizationURL());
	}
	
	@Override
	@Test
	public void getAccessToken() {
		String token = "JZ6rigAYezDHN5m2T3XS1fYZNKzo357KRwidd4QRxaU";
		String tokenSecret = "J1htOMywg2zypVrbbIorOJ82xTsnZFoIujqv96Wbg";
		RequestToken requestToken = new RequestToken(token, tokenSecret);
		
		String pin = "3nYzahzHYHGGSeOv4ukjyIVW9Rc4WZflNqrqzUeiviY";
		
		Twitter twitter = TwitterFactory.getSingleton();
	    twitter.setOAuthConsumer(Env.getProperty("twitter.client.id"), Env.getProperty("twitter.client.secret"));
	    AccessToken accessToken = null;
		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, pin);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
        LOG.info("[access token : " + accessToken.getToken() + " ]");
        LOG.info("[access token secret: " + accessToken.getTokenSecret() + " ]");
        LOG.info("[access token userId: " + accessToken.getUserId() + " ]");
        LOG.info("[access token screenName: " + accessToken.getScreenName() + " ]");
	}
	
	@Test
	public void getAuthUrlAndAccessToekn() {
		 // The factory instance is re-useable and thread safe.
	    Twitter twitter = TwitterFactory.getSingleton();
	    twitter.setOAuthConsumer(Env.getProperty("twitter.client.id"), Env.getProperty("twitter.client.secret"));
	    RequestToken requestToken = null;
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			e.printStackTrace();
		}
	    AccessToken accessToken = null;
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
	    while (null == accessToken) {
	      System.out.println("Open the following URL and grant access to your account:");
	      System.out.println(requestToken.getAuthorizationURL());
	      System.out.print("Enter the PIN(if aviailable) or just hit enter.[PIN]:");
	      String pin = null;
		try {
			pin = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	      try{
	         if(pin.length() > 0){
	           accessToken = twitter.getOAuthAccessToken(requestToken, pin);
	           LOG.info("[access token : " + accessToken.getToken() + " ]");
	           LOG.info("[access token secret: " + accessToken.getTokenSecret() + " ]");
	           LOG.info("[access token userId: " + accessToken.getUserId() + " ]");
	           LOG.info("[access token screenName: " + accessToken.getScreenName() + " ]");
	         }else{
	           accessToken = twitter.getOAuthAccessToken();
	         }
	      } catch (TwitterException te) {
	        if(401 == te.getStatusCode()){
	          System.out.println("Unable to get the access token.");
	        }else{
	          te.printStackTrace();
	        }
	      }
	    }
	}
	
	@Override
	@Test
	public void revokeAccessToken() {
		String accessToken = "N7zmOUDpKXcAAAAAAAAAbXjWhfGOYHjrwIcg7Wy26OzmMsbksMrSaK4aaYbCWdis";
		
		GetMethod method = new GetMethod(Env.getProperty("twitter.revokeToken.ur"));
		NameValuePair param = new NameValuePair("access_token",accessToken);
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
	
}
