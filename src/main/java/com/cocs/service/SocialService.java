package com.cocs.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.web.multipart.MultipartFile;

import com.cocs.beans.FileMeta;
import com.cocs.common.FileResource;
import com.cocs.handler.Params;
import com.cocs.handler.ResponseResult;
import com.cocs.handler.ResponseRows;
import com.cocs.handler.ResponseSuccess;
import com.cocs.webapp.api.exception.ApiException;

public abstract class SocialService implements IService{
	
	@Override
	public ResponseSuccess createFolder(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public ResponseSuccess rename(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public ResponseRows getFiles(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public ResponseSuccess copy(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public ResponseSuccess move(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public ResponseSuccess uploadFile(Params params, MultipartFile file) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public ResponseRows searchFiles(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public FileResource downloadFile(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public void downloadFiles(Params params, HttpServletResponse response) throws IOException, ApiException {
		throw new NotImplementedException();		
	}

	@Override
	public ResponseSuccess delete(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public ResponseResult getPreviewLink(Params params) throws ApiException {
		throw new NotImplementedException();
		
	}

	@Override
	public ResponseRows getFolders(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public ResponseSuccess untrashFile(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public void getThumbnail(Params params, HttpServletResponse response) throws ApiException {
		throw new NotImplementedException();
	}


	@Override
	public FileMeta sendMove(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public ResponseSuccess targetMove(Params params, FileMeta file) throws ApiException {
		throw new NotImplementedException();
	}
	
	@Override
	public FileMeta sendCopy(Params params) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public ResponseSuccess targetCopy(Params params, FileMeta file) throws ApiException {
		throw new NotImplementedException();
	}
//	@Override
//	public ResponseResult archiveFiles(Params params, HttpServletResponse response) throws IOException, ApiException {
//		throw new NotImplementedException();
//	}
	
	
}
