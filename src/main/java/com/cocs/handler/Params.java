package com.cocs.handler;

import java.util.Map;

import com.cocs.webapp.api.exception.ApiException;

public class Params {
	Map<String, Object> map = null;
	
	public Params(Map<String, Object> map) {
		this.map = map;
	}
	
	public String get(String key) throws ApiException {
		return get(key, true);
	}
	
	public String get(String key, boolean required) throws ApiException {
		String[] values = (String[])map.get(key);
		if(values == null) {
			if(required) {
				throw new ApiException("Invalid paramter." + key + " is required.");
			} else {
				return null;
			}
		}
		return values[0];
	}
}
