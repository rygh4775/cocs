package com.cocs.common;import java.io.File;import java.io.FileInputStream;import java.io.FileNotFoundException;import java.io.InputStream;import org.apache.commons.io.IOUtils;public class FileResource {		private File file = null;	private InputStream inputStream = null;	private String mimeType = "application/octet-stream";	private String name;	public FileResource() {		this.file = new File(Env.getRepositoryUploadDirPath(), Long.toString(System.currentTimeMillis()));	}		public FileResource(InputStream in) {		this.inputStream = in;	}		public FileResource(File file) {		this.file = file;	}	public File getFile() {//		if(file == null && inputStream != null) {//			file = new File(Env.getRepositoryUploadDirPath(), Long.toString(System.currentTimeMillis()));//			OutputStream fio = null;//			try {//				fio = new FileOutputStream(file);//				FileCopyUtils.copy(inputStream, fio);//			} catch (IOException e) {//				throw new RuntimeException(e);//			} finally {//				IOUtils.closeQuietly(fio);//			}//		}		return file;	}		public InputStream getInputStream() {		if(inputStream == null && file != null) {			try {				inputStream = new FileInputStream(file);			} catch (FileNotFoundException e) {				throw new RuntimeException(e);			}		}		return inputStream;	}	//	public OutputStream getOutputStream() {//		try {//			return new FileOutputStream(file);//		} catch (FileNotFoundException e) {//			throw new RuntimeException(e);//		}//	}		public void deleteFile(){		IOUtils.closeQuietly(inputStream);		if(file != null && file.exists()) {			file.delete();		}	}		public void setMimeType(String mimeType) {		this.mimeType = mimeType;	}		public String getMimeType() {		return mimeType;	}		public String getName() {		return name;	}	public void setName(String name) {		this.name = name;	}	}