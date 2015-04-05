package com.cocs.beans;

import java.io.File;
import java.io.InputStream;

public class FileMeta {
	private String id;
	private String path;
	private String name;
	private long size;
	private long created;
	private long modified;
	private String mimeType;
	private boolean isDeleted;
	private boolean isFile;
	private boolean previewSupported;
	private String vendor;
	private String parentsId;
	private String thumbnailLink;
	private String previewLink;
	private InputStream fileBinary;
	
	public InputStream getFileBinary() {
		return fileBinary;
	}
	public void setFileBinary(InputStream inputStream) {
		this.fileBinary = inputStream;
	}
	
	public String getpreviewLink() {
		return previewLink;
	}
	public void setPreviewLink(String previewLink) {
		this.previewLink = previewLink;
	}
	public String getThumbnailLink() {
		return thumbnailLink;
	}
	public void setThumbnailLink(String thumbnailLink) {
		this.thumbnailLink = thumbnailLink;
	}
	//	temp
	private String iconInfo;
	
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
	public long getCreated() {
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public long getModified() {
		return modified;
	}
	public void setModified(long modified) {
		this.modified = modified;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public boolean getIsDeleted() {
		return isDeleted;
	}
	public void setIsDeleted(boolean isDeleted) {
		this.isDeleted = isDeleted;
	}
	public boolean getIsFile() {
		return isFile;
	}
	public void setIsFile(boolean isFile) {
		this.isFile = isFile;
	}
	public String getIconInfo() {
		return iconInfo;
	}
	public void setIconInfo(String iconInfo) {
		this.iconInfo = iconInfo;
	}
	public boolean getPreviewSupported() {
		return previewSupported;
	}
	public void setPreviewSupported(boolean previewSupported) {
		this.previewSupported = previewSupported;
	}
	public String getVendor() {
		return vendor;
	}
	public void setVendor(String vendor) {
		this.vendor = vendor;
	}
	public String getParentsId() {
		return parentsId;
	}
	public void setParentsId(String parentsId) {
		this.parentsId = parentsId;
	}
}
