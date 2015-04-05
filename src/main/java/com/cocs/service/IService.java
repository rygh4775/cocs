package com.cocs.service;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.apache.amber.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.amber.oauth2.common.exception.OAuthProblemException;
import org.apache.amber.oauth2.common.exception.OAuthSystemException;
import org.springframework.web.multipart.MultipartFile;

import com.cocs.beans.FileMeta;
import com.cocs.common.FileResource;
import com.cocs.handler.Params;
import com.cocs.handler.ResponseResult;
import com.cocs.handler.ResponseRows;
import com.cocs.handler.ResponseSuccess;
import com.cocs.webapp.api.exception.ApiException;
/**
 * 
 * @author CHO
 *
 */
public interface IService {
	public enum serviceNames {google, dropbox}
	/**
	 * @return IService 
	 * @param ClientManager
	 * @throws ApiException 
	 */
	IService setClient(ClientManager clientManager) throws ApiException;
	/**
	 * 
	 * @return authorize URL of a vendor<String>
	 */
	String getAuthorizeURL() ;
	/**
	 * 
	 * @param code
	 * @param redirectUri
	 * @return token response of a vendor<OAuthJSONAccessTokenResponse>
	 * @throws OAuthSystemException
	 * @throws OAuthProblemException
	 */
	OAuthAccessTokenResponse getTokenResponse(String code, String redirectUri) throws OAuthSystemException, OAuthProblemException ;
	/**
	 * 
	 * @return name<String>, quotaUsed<Long>, quotaTotal<Long>, rootFolderId<String>
	 * @throws ApiException
	 */
	ResponseResult getUserInfo() throws ApiException ;
	/**
	 * 
	 * @param params
	 * @return success<Boolean>
	 * @throws ApiException
	 * @Google name(required), parentId(required)
	 * @Dropbox name(required), parentPath(required)
	 */
	ResponseSuccess createFolder(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return success<Boolean>
	 * @throws ApiException
	 * @Google id(required), name(required)
	 * @Dropbox path(required), name(required)
	 */
	ResponseSuccess rename(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return ResponseRowsImpl
	 * @throws ApiException
	 * @Google parentId(required), includeDeleted
	 * @Dropbox parentPath(required), includeDeleted
	 */
	ResponseRows getFiles(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return success<Boolean>
	 * @throws ApiException
	 * @Google id(required), parentId(required), targetParentId(required) *Folder can't be copied
	 * @Dropbox path(required), targetParentPath(required)
	 */
	ResponseSuccess copy(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return success<Boolean>
	 * @throws ApiException
	 * @Google id(required), targetParentId(required)
	 * @Dropbox path(required), targetParentPath(required)
	 */
	ResponseSuccess move(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @param file 
	 * @return success<Boolean>
	 * @throws ApiException
	 * @Google id(required), parentId(required)
	 * @Dropbox path(required), parentPath(required)
	 */
	ResponseSuccess uploadFile(Params params, MultipartFile file) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return rows<Array>, totalCount<Integer>
	 * @throws ApiException
	 * @Google keyword(required)
	 * @Dropbox keyword(required)
	 */
	ResponseRows searchFiles(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return file stream
	 * @throws IOException 
	 * @throws ApiException 
	 * @Google id(required)
	 * @Dropbox path(required)
	 */
	FileResource downloadFile(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @param response 
	 * @return zip file stream
	 * @throws IOException 
	 * @throws ApiException 
	 * @Google ids(required)
	 * @Dropbox paths(required)
	 */
	void downloadFiles(Params params, HttpServletResponse response) throws IOException, ApiException;
	/**
	 * 
	 * @param params
	 * @return result<String>
	 * @throws ApiException
	 * @Google id(required)
	 * @Dropbox path(required)
	 */
//	ResponseResult archiveFiles(Params params, HttpServletResponse response) throws IOException, ApiException;
//	/**
//	 * 
//	 * @param params
//	 * @return success<Boolean>
//	 * @throws ApiException
//	 * @Google id(required)
//	 * @Dropbox path(required)
//	 */
	ResponseSuccess delete(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return result<String>
	 * @throws ApiException
	 * @Google id(required)
	 * @Dropbox path(required)
	 */
	ResponseResult getPreviewLink(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @param response
	 * @throws ApiException
	 * @Google id(required)
	 * @Dropbox path(required)
	 */
	void getThumbnail(Params params, HttpServletResponse response) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return ResponseRowsImpl
	 * @throws ApiException
	 * @Google parentId(required), includeDeleted
	 * @Dropbox parentPath(required), includeDeleted
	 */
	ResponseRows getFolders(Params params) throws ApiException;
	
	ResponseSuccess untrashFile(Params params) throws ApiException;
	
	/**
	 * 
	 * @param params
	 * @param file 
	 * @return ResponseSuccess
	 * @throws ApiException
	 */
	ResponseSuccess createPost(Params params, FileResource fileResource) throws ApiException;
	/**
	 * 
	 * @param params
	 * @param file
	 * @return ResponseSuccess
	 * @throws ApiException
	 */
	ResponseSuccess deletePost(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return FileMeta
	 * @throws ApiException
	 * @Google id(required), targetParentId(required)
	 * @Dropbox path(required), targetParentPath(required)
	 */
	FileMeta sendMove(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return success<Boolean>
	 * @throws ApiException
	 * @Google id(required), targetParentId(required)
	 * @Dropbox path(required), targetParentPath(required)
	 */
	ResponseSuccess targetMove(Params params, FileMeta file) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return FileMeta
	 * @throws ApiException
	 * @Google id(required), targetParentId(required)
	 * @Dropbox path(required), targetParentPath(required)
	 */
	FileMeta sendCopy(Params params) throws ApiException;
	/**
	 * 
	 * @param params
	 * @return success<Boolean>
	 * @throws ApiException
	 * @Google id(required), targetParentId(required)
	 * @Dropbox path(required), targetParentPath(required)
	 */
	ResponseSuccess targetCopy(Params params, FileMeta file) throws ApiException;
}
