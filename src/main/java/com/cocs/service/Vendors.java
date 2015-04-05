package com.cocs.service;

import com.cocs.service.dropbox.DropboxService;
import com.cocs.service.facebook.FacebookService;
import com.cocs.service.google.GoogleService;
import com.cocs.service.twitter.TwitterService;
import com.cocs.webapp.api.exception.ApiException;

public enum Vendors {
	
//	google(GoogleService.getInstance()), 
//	dropbox(DropboxService.getInstance());
	google(new GoogleService()), 
	dropbox(new DropboxService()),
	facebook(new FacebookService()),
	twitter(new TwitterService());
 
	private IService service;
 
	private Vendors(IService service) {
		this.service = service;
	}
 
	public IService getService() throws ApiException {
		return service;
	}

}