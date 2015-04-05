package com.cocs.service;

import javax.annotation.Resource;

import org.springframework.beans.factory.ObjectFactory;

import twitter4j.Twitter;

import com.dropbox.core.DbxClient;
import com.google.api.services.drive.Drive;

import facebook4j.Facebook;

public class ClientManager {
	
	@Resource(name="sessionContextFactory")
	ObjectFactory sessionContextFactory;
	
	public DbxClient getDropboxClient() {
		SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
		return sessionContext.getDropboxClient();
	}
	public void setDropboxClient(DbxClient dropboxClient) {
		SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
		sessionContext.setDropboxClient(dropboxClient);
	}
	public Drive getGoogleClient() {
		SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
		return sessionContext.getGoogleClient();
	}
	public void setGoogleClient(Drive googleClient) {
		SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
		sessionContext.setGoogleClient(googleClient);
	}
	public Facebook getFacebookClient() {
		SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
		return sessionContext.getFacebookClient();
	}
	public void setFacebookClient(Facebook facebookClient) {
		SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
		sessionContext.setFacebookClient(facebookClient);
	}
	public Twitter getTwitterClient() {
		SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
		return sessionContext.getTwitterClient();
	}
	public void setTwitterClient(Twitter twitterClient) {
		SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
		sessionContext.setTwitterClient(twitterClient);
	}

	public void invalidate() {
		SessionContext sessionContext = (SessionContext) sessionContextFactory.getObject();
		sessionContext.invalidate();
	}
}
