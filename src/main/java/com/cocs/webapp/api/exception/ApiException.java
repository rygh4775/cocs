package com.cocs.webapp.api.exception;

import org.apache.commons.httpclient.HttpStatus;

public class ApiException extends Exception{
	String vendor;
	private int statusCode = HttpStatus.SC_BAD_REQUEST;
	
	public ApiException() {
		super();
	}
	
	public ApiException(String message) {
		super(message);
	}
	
	public ApiException(String message, int statusCode) {
		super(message);
		this.statusCode = statusCode;
	}

	public ApiException(String vendor, Throwable cause) {
		super(vendor, cause);
	}
	
	public ApiException(Throwable cause) {
		super(cause);
	}
	
	public ApiException setVendor(String vendor) {
		this.vendor = vendor;
		return this;
	}
	
	public ApiException setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}

	public String getVendor() {
		return this.vendor;
	}
	
	public int getStatusCode() {
		return this.statusCode;
	}

}
