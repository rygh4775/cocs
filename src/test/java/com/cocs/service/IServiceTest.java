package com.cocs.service;

import java.io.IOException;

import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;

public interface IServiceTest {
	
	public void test();
	
	interface AuthTest{
		public void printAuthUrl() ;
		public void getAccessToken() throws OAuthSystemException, OAuthProblemException ;
		public void revokeAccessToken() ;
//		public void refreshAccessToken() throws OAuthSystemException, OAuthProblemException ;
	}
	
	interface UserTest {
		public void getInfo();
	}
	
	interface FolderTest {
		public void create() throws IOException ;
		public void rename() throws IOException ;
		public void copy() ;
		public void move() throws IOException;
	}
	
	interface FileTest {
		void getFiles() throws IOException ;
		void retrieve() throws IOException ;
		void copy() throws IOException ;
		void move() throws IOException ;
		void upload() throws IOException ;
		void download() throws IOException ;
	}

}
