package com.cocs.beans;

public class UploadFile {

//	private String fileName;
//    private String fileSize;
//    private String fileType;
// 
//    private byte[] bytes;
	
	private String name;
	private long size;
	private String error;
	
	private String id;
	private String path;
	private String type;
	private String thumbnailUrl;
	
	public UploadFile(String name, long size, String type, String thumbnailUrl) {
		this.name = name;
		this.size = size;
		this.type = type;
		this.thumbnailUrl = thumbnailUrl;
	}
	
//	error
	public UploadFile(String name, long size, String error){
		this.name = name;
		this.size = size;
		this.error = error;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getThumbnailUrl() {
		return thumbnailUrl;
	}

	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	
}
