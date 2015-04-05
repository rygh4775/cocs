package com.cocs.service;

import java.util.Map;

import org.apache.commons.lang.NotImplementedException;

import com.cocs.common.FileResource;
import com.cocs.handler.Params;
import com.cocs.handler.ResponseSuccess;
import com.cocs.webapp.api.exception.ApiException;


public abstract class CloudService implements IService{

	@Override
	public ResponseSuccess createPost(Params params, FileResource fileResource) throws ApiException {
		throw new NotImplementedException();
	}
	
	@Override
	public ResponseSuccess deletePost(Params params) throws ApiException {
		throw new NotImplementedException();
	}
	
//	@Override
//	public void downloadFiles(Map<String, Object> paramMap, HttpServletResponse response) throws IOException, ApiException {
//		Params params = new Params(paramMap);
//		String name = params.get("name");
//		File zipFile = new File(Env.getRepositoryUploadDirPath()+"/"+name);
//		
//		response.setContentType("application/zip");
//		response.setHeader("Content-Disposition", "attachment; filename=COCS_"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".zip");
//		
//		FileCopyUtils.copy(new FileInputStream(zipFile), response.getOutputStream());
//		
//	}
	
}
