package com.cocs.service.google;

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
import org.apache.commons.lang.NotImplementedException;
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
import com.google.api.services.drive.Drive;

public class GoogleService extends CloudService implements DefaultConstants{

	private final static String VENDOR = "google";
	private static GoogleService instance = new GoogleService();
	
	private GoogleDAO googleDAO = null;
	
	public GoogleService() {
		googleDAO = new GoogleDAO();
	}
	
	public static GoogleService getInstance() {
		return instance;
	}

	@Override
	public IService setClient(ClientManager clientManager) throws ApiException {
		Drive client = clientManager.getGoogleClient();
		if(client != null) {
			googleDAO.setClient(client);
		}
		return this;
	}
	
	@Override
	public String getAuthorizeURL() {
		String url = Env.getProperty("google.authorize.url")+"?response_type=code&client_id="+Env.getProperty("google.client.id")+"&redirect_uri="+Env.getProperty("google.redirect.uri")+"&scope=https://www.googleapis.com/auth/drive";
		url += "&approval_prompt=force";
		url += "&access_type=offline";
		return url;
	}

	@Override
	public OAuthJSONAccessTokenResponse getTokenResponse(String code, String redirectUri) throws OAuthSystemException, OAuthProblemException {
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("google.token.url"));
		requestBuilder.setCode(code);
		requestBuilder.setClientId(Env.getProperty("google.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("google.client.secret"));
		requestBuilder.setRedirectURI(redirectUri);
		requestBuilder.setGrantType(GrantType.AUTHORIZATION_CODE);
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		return oAuthClient.accessToken(oAuthRequest);
		
	}

	public OAuthJSONAccessTokenResponse refreshToken(String refreshToken) throws OAuthSystemException, OAuthProblemException {
		TokenRequestBuilder requestBuilder = OAuthClientRequest.tokenLocation(Env.getProperty("google.token.url"));
		requestBuilder.setRefreshToken(refreshToken);
		requestBuilder.setClientId(Env.getProperty("google.client.id"));
		requestBuilder.setClientSecret(Env.getProperty("google.client.secret"));
		requestBuilder.setGrantType(GrantType.REFRESH_TOKEN);
		
		OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
		OAuthClientRequest oAuthRequest = requestBuilder.buildBodyMessage();
		return oAuthClient.accessToken(oAuthRequest);
	}

	@Override
	public ResponseResult getUserInfo() throws ApiException {
		JSONObject result = googleDAO.getUserInfo();
		
		ResponseResultImpl responseResult = Response.createResponseResult(VENDOR);
		responseResult.setResult(result);
		return responseResult;
	}

	@Override
	public ResponseSuccess createFolder(Params params) throws ApiException {
		String name = params.get(NAME);
		String parentId = params.get(PARENT_ID);
		
		googleDAO.createFolder(name, parentId);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseSuccess rename(Params params) throws ApiException {
		String id = params.get(ID);
		String name = params.get(NAME);
		
		googleDAO.rename(id, name);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseRows getFiles(Params params) throws ApiException {
		String parentId = params.get(PARENT_ID);
		
		boolean includeDeleted = "true".equals(params.get("includeDeleted", false));
		
		List<FileMeta> files = googleDAO.getFiles(parentId, includeDeleted);
		
		ResponseRowsImpl responseRows = Response.createResponseRows(VENDOR);
		responseRows.setRows(files);
		responseRows.setTotalCount(files.size());
		return responseRows;
	}

	@Override
	public ResponseSuccess copy(Params params) throws ApiException {
		String id = params.get(ID);
		String targetParentId = params.get(TARGET_PARENT_ID);
		
		googleDAO.copy(id, targetParentId);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseSuccess move(Params params) throws ApiException {
		String id = params.get(ID);
		String targetParentId = params.get(TARGET_PARENT_ID);
		
		googleDAO.move(id, targetParentId);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseSuccess uploadFile(Params params, MultipartFile file) throws ApiException {
		String parentId = params.get(PARENT_ID);
		
		googleDAO.uploadFile(parentId, file);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseRows searchFiles(Params params) throws ApiException {
		String keyword = params.get("keyword");
		
		List<FileMeta> files = googleDAO.searchFiles(keyword);
		
		ResponseRowsImpl responseRows = Response.createResponseRows(VENDOR);
		responseRows.setRows(files);
		responseRows.setTotalCount(files.size());
		return responseRows;
	}

	@Override
	public FileResource downloadFile(Params params) throws ApiException {
		String id = params.get(ID);
		
		try {
			return googleDAO.downloadFile(id);
		} catch (IOException e) {
			throw new ApiException(e);
		}
	}

	@Override
	public void downloadFiles(Params params, HttpServletResponse response) throws IOException, ApiException {
		String ids = params.get("ids");
		
		googleDAO.downloadFiles(ids.split(","), response);
	}
	
//	@Override
//	public ResponseResult archiveFiles(Params params, HttpServletResponse response) throws IOException, ApiException {
//		Params params = new Params(paramMap);
//		String ids = params.get("ids");
//		
////		response.setContentLength(10000000);
////		response.addIntHeader("Content-Length", 11111111);
//		
//		String fileName = googleDAO.archiveFiles(ids.split(","));
//		
//		ResponseResultImpl responseResult = Response.createResponseResult(VENDOR);
//		responseResult.setResult(fileName);
//		return responseResult;
//	}

	@Override
	public ResponseSuccess delete(Params params) throws ApiException {
		String id = params.get(ID);
		
		googleDAO.delete(id);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseResult getPreviewLink(Params params) throws ApiException {
		String id = params.get(ID);
		
		String previewLink = googleDAO.getPreviewLink(id);
		
		ResponseResultImpl responseResult = Response.createResponseResult(VENDOR);
		responseResult.setResult(previewLink);
		return responseResult;
	}

	@Override
	public ResponseRows getFolders(Params params) throws ApiException {
		String parentId = params.get(PARENT_ID);
		boolean includeDeleted = params.get("includeDeleted", false) == "true";
		
		List<FileMeta> folders = googleDAO.getFolders(parentId, includeDeleted);
		
		ResponseRowsImpl responseRows = Response.createResponseRows(VENDOR);
		responseRows.setRows(folders);
		responseRows.setTotalCount(folders.size());
		return responseRows;
	}

	@Override
	public ResponseSuccess untrashFile(Params params) throws ApiException {
		String id = params.get(ID);
		
		googleDAO.untrashFile(id);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}
	
	@Override
	public void getThumbnail(Params params, HttpServletResponse response) throws ApiException {
		throw new NotImplementedException();
	}

	@Override
	public FileMeta sendMove(Params params) throws ApiException {
		String parentId = (String) params.get(ID);
		FileMeta returnFile = googleDAO.sendMove(parentId);
		return returnFile;
	}
	
	@Override
	public FileMeta sendCopy(Params params) throws ApiException {
		String parentId = (String) params.get(ID);
		FileMeta returnFile = googleDAO.sendCopy(parentId);
		return returnFile;
	}


	@Override
	public ResponseSuccess targetMove(Params params, FileMeta file) throws ApiException {
		String parentId = params.get(TARGET_PARENT_ID);
		
		googleDAO.uploadFile(parentId, file);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}

	@Override
	public ResponseSuccess targetCopy(Params params, FileMeta file) throws ApiException {
		String parentId = params.get(TARGET_PARENT_ID);
		
		googleDAO.uploadFile(parentId, file);
		
		ResponseSuccessImpl responseSuccess = Response.createResponseSuccess(VENDOR);
		responseSuccess.setSuccess(true);
		return responseSuccess;
	}
}
