package com.cocs.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

import com.cocs.service.dropbox.DropBoxTestBase;
import com.cocs.service.facebook.FacebookTestBase;
import com.cocs.service.google.GoogleTestBase;
import com.cocs.service.twitter.TwitterTestBase;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext-test.xml"})
public abstract class AbstractServiceTest implements DropBoxTestBase, GoogleTestBase, TwitterTestBase, FacebookTestBase{
	protected static final Log LOG = LogFactory.getLog(AbstractServiceTest.class);
	
	public static DbxClient getDropboxClient() {
		DbxRequestConfig requestConfig = new DbxRequestConfig( "COCSTEST/0.1", null);
		LOG.debug(requestConfig.clientIdentifier);
		LOG.debug(requestConfig.userLocale);
		return new DbxClient(requestConfig, DroprobxDB.ACCESS_TOKEN);
	}
	
	public static Drive getGoogleClient() {
		TokenResponse tokenResponse = new TokenResponse().setAccessToken(GoogleDB.ACCESS_TOKEN).setRefreshToken(GoogleDB.REFRESH_TOKEN);
		Details web = new Details().setClientId(GoogleDB.CLIENT_ID).setClientSecret(GoogleDB.CLIENT_SECRET);
		
		GoogleCredential credentials = new GoogleCredential.Builder()
        .setClientSecrets(new GoogleClientSecrets().setWeb(web))
        .setTransport(new NetHttpTransport())
        .setJsonFactory(new JacksonFactory())
        .build();
		credentials.setFromTokenResponse(tokenResponse);
		LOG.debug(credentials.getAccessToken());
		LOG.debug(credentials.getRefreshToken());
	    return new Drive.Builder(new NetHttpTransport(), new JacksonFactory(), credentials).setApplicationName("cocs").build();
	}
	
	public static Facebook getFacebookClient() {
		facebook4j.conf.ConfigurationBuilder cb = new facebook4j.conf.ConfigurationBuilder();
		cb.setDebugEnabled(true)
		.setOAuthAppId(FacebookDB.CLIENT_ID)
		.setOAuthAppSecret(FacebookDB.CLIENT_SECRET)
		.setOAuthAccessToken(FacebookDB.ACCESS_TOKEN)
		.setOAuthPermissions("email, publish_actions");
		FacebookFactory ff = new FacebookFactory(cb.build());
		Facebook facebook = ff.getInstance();
		return facebook;
	}
	
	public static Twitter getTwitterClient() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setDebugEnabled(true)
	            .setOAuthConsumerKey(TwitterDB.CLIENT_ID)
	            .setOAuthConsumerSecret(TwitterDB.CLIENT_SECRET)
	            .setOAuthAccessToken(TwitterDB.ACCESS_TOKEN)
	            .setOAuthAccessTokenSecret(TwitterDB.ACCESS_TOKEN_SECRET);

	    TwitterFactory tf = new TwitterFactory(cb.build());
	    Twitter twitter = tf.getInstance();
	    return twitter;
	}
}
