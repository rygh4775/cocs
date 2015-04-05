package com.cocs.server;

import java.beans.Transient;

public class User {
	private transient String key;
	private String id;
	private transient String password;
	private transient String salt;
	private String created;
	private String modified;
	private String oauthProvider;
	private String googleToken;
	private String dropboxToken;
	private String facebookToken;
	private String twitterToken;
	private String twitterTokenSecret;
	
	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getModified() {
		return modified;
	}

	public void setModified(String modified) {
		this.modified = modified;
	}
	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}
	
	public String getOauthProvider() {
		return oauthProvider;
	}

	public void setOauthProvider(String oauthProvider) {
		this.oauthProvider = oauthProvider;
	}
	
	public String getFacebookToken() {
		return facebookToken;
	}

	public void setFacebookToken(String facebookToken) {
		this.facebookToken = facebookToken;
	}

	public String getTwitterToken() {
		return twitterToken;
	}

	public void setTwitterToken(String twitterToken) {
		this.twitterToken = twitterToken;
	}

	public String getTwitterTokenSecret() {
		return twitterTokenSecret;
	}

	public void setTwitterTokenSecret(String twitterTokenSecret) {
		this.twitterTokenSecret = twitterTokenSecret;
	}

	public User() {
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	@Transient
	public String getPassword() {
		return password;
	}
	@Transient
	public void setPassword(String password) {
		this.password = password;
	}
	@Transient
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	public String getGoogleToken() {
		return googleToken;
	}
	public void setGoogleToken(String googleToken) {
		this.googleToken = googleToken;
	}
	public String getDropboxToken() {
		return dropboxToken;
	}
	public void setDropboxToken(String dropboxToken) {
		this.dropboxToken = dropboxToken;
	}

	@Override
	public String toString() {
		return  "key : " + key + "\n" +
				"id : " + id + "\n" + 
 				"password : " + password + "\n" + 
 				"salt : " + salt + "\n" + 
 				"oauthProvider : " + oauthProvider + "\n" + 
 				"googleToken : " + googleToken + "\n" + 
 				"dropboxToken : " + dropboxToken + "\n" + 
 				"facebookToken : " + facebookToken + "\n" + 
 				"twitterToken : " + twitterToken + "\n" + 
 				"twitterTokenSecret : " + twitterTokenSecret + "\n";
	}
	
	
}
