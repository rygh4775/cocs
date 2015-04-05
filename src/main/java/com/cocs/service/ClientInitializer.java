package com.cocs.service;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.cocs.common.Env;
import com.dropbox.core.DbxClient;
import com.dropbox.core.DbxRequestConfig;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets.Details;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;

import facebook4j.Facebook;
import facebook4j.FacebookFactory;

public class ClientInitializer {
	
	public static Drive getGoogleClient(String refreshToken) {
		TokenResponse tokenResponse = new TokenResponse().setRefreshToken(refreshToken);
		Details web = new Details().setClientId(Env.getProperty("google.client.id")).setClientSecret(Env.getProperty("google.client.secret"));
		
		GoogleCredential credentials = new GoogleCredential.Builder()
	    .setClientSecrets(new GoogleClientSecrets().setWeb(web))
	    .setTransport(new NetHttpTransport())
	    .setJsonFactory(new JacksonFactory())
	    .build();
		credentials.setFromTokenResponse(tokenResponse);
		return new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), credentials).setApplicationName("cocs").build();
	}
	
	public static Drive getGoogleClient(String accessToken, String refreshToken) {
		TokenResponse tokenResponse = new TokenResponse().setAccessToken(accessToken).setRefreshToken(refreshToken);
		Details web = new Details().setClientId(Env.getProperty("google.client.id")).setClientSecret(Env.getProperty("google.client.secret"));
		
		GoogleCredential credentials = new GoogleCredential.Builder()
	    .setClientSecrets(new GoogleClientSecrets().setWeb(web))
	    .setTransport(new NetHttpTransport())
	    .setJsonFactory(new JacksonFactory())
	    .build();
		credentials.setFromTokenResponse(tokenResponse);
		return new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), credentials).setApplicationName("cocs").build();
	}
	
	public static DbxClient getDropboxClient(String dropboxAccessToken) {
		DbxRequestConfig requestConfig = new DbxRequestConfig( "COCS/0.1", null);
		return new DbxClient(requestConfig, dropboxAccessToken);
	}
	
	public static Facebook getFacebookClient(String facebookAccessToken) {
		facebook4j.conf.ConfigurationBuilder cb = new facebook4j.conf.ConfigurationBuilder();
		cb.setDebugEnabled(true)
		  .setOAuthAppId(Env.getProperty("facebook.client.id"))
		  .setOAuthAppSecret(Env.getProperty("facebook.client.secret"))
		  .setOAuthAccessToken(facebookAccessToken)
		  .setOAuthPermissions("email,publish_stream,...");
		return new FacebookFactory(cb.build()).getInstance();
	}
	
	public static Twitter getTwitterClient(String twitterAccessToken, String twitterAccessTokenSecret) {
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	      .setOAuthConsumerKey(Env.getProperty("twitter.client.id"))
	      .setOAuthConsumerSecret(Env.getProperty("twitter.client.secret"))
	      .setOAuthAccessToken(twitterAccessToken)
	      .setOAuthAccessTokenSecret(twitterAccessTokenSecret);
	    return new TwitterFactory(cb.build()).getInstance();
	}
}
