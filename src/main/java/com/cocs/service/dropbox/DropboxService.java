package com.cocs.service.dropbox;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.amber.oauth2.client.OAuthClient;
import org.apache.amber.oauth2.client.URLConnectionClient;
import org.apache.amber.oauth2.client.request.OAuthClientRequest;
import org.apache.amber.oauth2.client.request.OAuthClientRequest.TokenRequestBuilder;
import org.apache.amber.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.apache.amber.oauth2.common.message.types.GrantType;
import org.springframework.web.multipart.MultipartFile;

import com.cocs.beans.FileMeta;
import com.cocs.common.DefaultConstants;
import com.cocs.common.Env;
import com.cocs.common.FileResource;
import com.cocs.handler.Params;
import com.cocs.handler.Response;
import com.cocs.handler.ResponseResult;
import com.cocs.handler.ResponseResultImpl;
import com.cocs.handler.ResponseRows;
import com.cocs.handler.ResponseRowsImpl;
import com.cocs.handler.ResponseSuccess;
import com.cocs.handler.ResponseSuccessImpl;
import com.cocs.service.ClientManager;
import com.cocs.service.CloudService;
import com.cocs.service.IService;
import com.cocs.webapp.api.exception.ApiException;
import com.dropbox.core.DbxClient;

public class DropboxService extends CloudService implements DefaultConstants{

	private final static String VENDOR = "dropbox";
	private static DropboxService instance = new DropboxService();
	
	private DropboxDAO dropboxDAO = null;
	
	public DropboxService() {
		dropboxDAO = new DropboxDAO();
	}
	
	public static DropboxService getInstance() {
		return instance ;
	}
	
	@Override
	public IService setClient(ClientManager clientManager) throws ApiException {
		DbxClient client = clientManager.getDropboxClient();
		if(client != null) {
			dropboxDAO.setClient(client);
		}
		return this;
	}
	
	@Override
	public String getAuthorizeURL() {
		return Env.getProperty("dropbox.authorize.url")+"?response_type=code&client_id="+Env.getProperty("dropbox.client.id")+"&redirect_uri="+Env.getProperty("dropbox.redirect.uri")+"&locale=ko_KR";
	}

	@Override
	public OAuthJSONAccessTokenResponse getTokenResponse(String code, String redirectUri) throws OAuthSystemException, OAuthProblemException {
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("dropbox.token.url"));
		requestBuilder.setCode(code);
		requestBuilder.setClientId(Env.getProperty("dropbox.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("dropbox.client.secret"));
		requestBuilder.setRedirectURI(redirectUri);
		requestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE);
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		return oAuthClient.accessToken(oAuthRequest);
	}

	@Override
	public ResponseResult getUserInfo() throws ApiException {
		JSONObject result = dropboxDAO.getUserInfo();
		
		ResponseResultImpl responseResult = Response.createResponseResult(VENDOR);
		responseResult.setResult(result);
		return responseResult;
	}

	@Override
	public ResponseSuccess createFolder(Params params) throws ApiException {
		String name = params.get(NAME);
		String parentPath = params.get(PARENT_PATH);
		
		dropboxDAO.createFolder(name, parentPath);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseSuccess rename(Params params) throws ApiException {
		String name = params.get(NAME);
		String path = params.get(PATH);
		
		dropboxDAO.rename(name, path);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseRows getFiles(Params params) throws ApiException {
		String parentPath = params.get(PARENT_PATH);
		boolean includeDeleted = params.get("includeDeleted", false) == "true";
		
		List<FileMeta> files = dropboxDAO.getFiles(parentPath, includeDeleted);
		
		ResponseRowsImpl responseRows = Response.createResponseRows(VENDOR);
		responseRows.setRows(files);
		responseRows.setTotalCount(files.size());
		return responseRows;
	}

	@Override
	public ResponseSuccess copy(Params params) throws ApiException {
		String path = params.get(PATH);
		String targetParentPath = params.get(TARGET_PARENT_PATH);
		
		dropboxDAO.copy(path, targetParentPath);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseSuccess move(Params params) throws ApiException {
		String path = params.get(PATH);
		String targetParentPath = params.get(TARGET_PARENT_PATH);
		
		dropboxDAO.move(path, targetParentPath);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseSuccess uploadFile(Params params, MultipartFile file) throws ApiException {
		String parentPath = params.get(PARENT_PATH);
		
		dropboxDAO.uploadFile(parentPath, file);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseRows searchFiles(Params params) throws ApiException {
		String keyword = params.get("keyword");
		
		List<FileMeta> files = dropboxDAO.searchFiles(keyword);
		
		ResponseRowsImpl responseRows = Response.createResponseRows(VENDOR);
		responseRows.setRows(files);
		responseRows.setTotalCount(files.size());
		return responseRows;
	}

	@Override
	public FileResource downloadFile(Params params) throws ApiException {
		String path = params.get(PATH);
		
		try {
			return dropboxDAO.downloadFile(path);
		} catch (IOException e) {
			throw new ApiException(e);
		}
	}

	@Override
	public void downloadFiles(Params params, HttpServletResponse response) throws IOException, ApiException {
		String paths = params.get("paths");
		
		dropboxDAO.downloadFiles(paths.split(","), response);
	}
	
//	@Override
//	public ResponseResult archiveFiles(Params params, HttpServletResponse response) throws IOException, ApiException {
//		Params params = new Params(paramMap);
//		String paths = params.get("paths");
//		
//		String fileName = dropboxDAO.archiveFiles(paths.split(","));
//		ResponseResultImpl responseResult = Response.createResponseResult(VENDOR);
//		responseResult.setResult(fileName);
//		return responseResult;
//	}

	@Override
	public ResponseSuccess delete(Params params) throws ApiException {
		String path = params.get(PATH);
		
		dropboxDAO.delete(path);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseResult getPreviewLink(Params params) throws ApiException {
		String path = params.get("path");
		
		String previewLink = dropboxDAO.getPreviewLink(path);
		
		ResponseResultImpl responseResult = Response.createResponseResult(VENDOR);
		responseResult.setResult(previewLink);
		return responseResult;
	}

	@Override
	public ResponseRows getFolders(Params params) throws ApiException {
		String parentPath = params.get(PARENT_PATH);
		boolean includeDeleted = params.get("includeDeleted", false) == "true";
		
		List<FileMeta> folders = dropboxDAO.getFolders(parentPath, includeDeleted);
		
		ResponseRowsImpl responseRows = Response.createResponseRows(VENDOR);
		responseRows.setRows(folders);
		responseRows.setTotalCount(folders.size());
		return responseRows;
	}

	@Override
	public ResponseSuccess untrashFile(Params params) throws ApiException {
		String path = params.get(PATH);
		
		dropboxDAO.untrashFile(path);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}
	
	@Override
	public void getThumbnail(Params params, HttpServletResponse response) throws ApiException {
		String path = params.get(PATH);
		
		dropboxDAO.getThumbnail(path, response);
	}
	
	@Override
	public synchronized FileMeta sendMove(Params params) throws ApiException {
		String parentId = (String) params.get(PATH);
		return dropboxDAO.sendMove(parentId);
	}
	

	@Override
	public  ResponseSuccess targetMove(Params params, FileMeta file) throws ApiException {
		
		String parentPath = params.get(TARGET_PARENT_PATH);
		dropboxDAO.uploadFile(parentPath, file);
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		
		return responseSuccess;
	}
	
	@Override
	public synchronized FileMeta sendCopy(Params params) throws ApiException {
		String parentId = (String) params.get(PATH);
		return dropboxDAO.sendCopy(parentId);
	}
	

	@Override
	public  ResponseSuccess targetCopy(Params params, FileMeta file) throws ApiException {
		
		String parentPath = params.get(TARGET_PARENT_PATH);
		dropboxDAO.uploadFile(parentPath, file);
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		
		return responseSuccess;
	}

}
