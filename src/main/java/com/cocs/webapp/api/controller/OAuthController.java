package com.cocs.webapp.api.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.amber.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import com.cocs.common.DefaultConstants;
import com.cocs.common.Env;
import com.cocs.common.EveryoneAccessable;
import com.cocs.server.User;
import com.cocs.server.dao.UsersDAO;
import com.cocs.service.ClientInitializer;
import com.cocs.service.ClientManager;
import com.cocs.service.IService;
import com.cocs.service.Vendors;
import com.cocs.service.dropbox.DropboxService;
import com.cocs.service.facebook.FacebookService;
import com.cocs.service.google.GoogleService;
import com.cocs.service.twitter.TwitterService;
import com.cocs.webapp.exception.UserLoginException;
import com.cocs.webapp.intercepter.UserLoginIntercepter;
import com.dropbox.core.DbxClient;
import com.google.api.services.drive.Drive;

import facebook4j.Facebook;

@Controller
public class OAuthController implements DefaultConstants, EveryoneAccessable{
	
	@Autowired
	UsersDAO usersDAO;
	
	@Autowired
	ClientManager clientManager;
	
	@RequestMapping(value = { "/{vendor}/authorize.*" })
	public ModelAndView authorize(	HttpSession session,
									@PathVariable String vendor,
									final HttpServletRequest request, final ModelMap model) throws Throwable {
		IService service = Vendors.valueOf(vendor).getService();
		
		if("twitter".equals(vendor)) {
			Twitter twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(Env.getProperty("twitter.client.id"), Env.getProperty("twitter.client.secret"));
			session.setAttribute("twitter", twitter);
			
			RequestToken requestToken = twitter.getOAuthRequestToken(Env.getProperty("twitter.redirect.uri"));
			session.setAttribute("requestToken", requestToken);
			return new ModelAndView("redirect:"+requestToken.getAuthenticationURL());
		}
		return new ModelAndView("redirect:"+service.getAuthorizeURL());
	}
	
	@RequestMapping(value = { "/{vendor}/token.*" })
	public ModelAndView token(	HttpSession session,
								@RequestParam(value="code",required=false,defaultValue="") String code,
								@RequestParam(value="error",required=false,defaultValue="") String error,
								@PathVariable String vendor,
								final HttpServletRequest request, final ModelMap model) throws Throwable {
		String loginUser = (String) session.getAttribute(UserLoginIntercepter.LOGIN_USER);
		String oauthProvider = (String) session.getAttribute(UserLoginIntercepter.OAUTH_PROVIDER);
		if(StringUtils.isBlank(loginUser)) {
			throw new UserLoginException();
		}
		
		if(StringUtils.isNotBlank(error)){
			if("twitter".equals(vendor)) {
				clientManager.setTwitterClient(null);
			}
			return new ModelAndView("redirect:/home");
		}
		
		IService service = Vendors.valueOf(vendor).getService();
		
		User user = usersDAO.getUser(loginUser, oauthProvider);
		String redirectUri = "/home";
		
		if(service instanceof GoogleService) {
			OAuthAccessTokenResponse tokenResponse = service.getTokenResponse(code, Env.getProperty("google.redirect.uri"));
			String accessToken = tokenResponse.getAccessToken();
			String refreshToken = tokenResponse.getRefreshToken();
			
			user.setGoogleToken(refreshToken);
			
			Drive googleClient = ClientInitializer.getGoogleClient(accessToken, refreshToken);
			if(googleClient != null) {
				clientManager.setGoogleClient(googleClient);
			}
			
		} else if (service instanceof DropboxService) {
			OAuthAccessTokenResponse tokenResponse = service.getTokenResponse(code, Env.getProperty("dropbox.redirect.uri"));
			String accessToken = tokenResponse.getAccessToken();
			String userId = tokenResponse.getParam("uid");
			
			user.setDropboxToken(accessToken);
			
			DbxClient dropboxClient = ClientInitializer.getDropboxClient(accessToken);
			if(dropboxClient != null) {
				clientManager.setDropboxClient(dropboxClient);
			}
		} else if (service instanceof FacebookService) {
			OAuthAccessTokenResponse tokenResponse = service.getTokenResponse(code, Env.getProperty("facebook.redirect.uri"));
			String accessToken = tokenResponse.getAccessToken();
			
			user.setFacebookToken(accessToken);
			
			Facebook facebookClient = ClientInitializer.getFacebookClient(accessToken);
			if(facebookClient != null) {
				clientManager.setFacebookClient(facebookClient);
			}
			
			redirectUri += "#social";
		} else if (service instanceof TwitterService) {
			Twitter twitter = (Twitter) request.getSession().getAttribute("twitter");
	        RequestToken requestToken = (RequestToken) request.getSession().getAttribute("requestToken");
			String oauthVerifier = request.getParameter("oauth_verifier");
			
			AccessToken accessToken = twitter.getOAuthAccessToken(requestToken, oauthVerifier);
			user.setTwitterToken(accessToken.getToken());
			user.setTwitterTokenSecret(accessToken.getTokenSecret());
			
			clientManager.setTwitterClient(twitter);
			
            session.removeAttribute("requestToken");
            session.removeAttribute("twitter");
            
            redirectUri += "#social";
		} 
		
		usersDAO.updateUser(user, oauthProvider);
			
		return new ModelAndView("redirect:" + redirectUri);
	}
	
	// Oauth 인증 해지
	@RequestMapping(value = { "/{vendor}/unauthorize.*" })
	public ModelAndView unAuthorize(	HttpSession session,
										@PathVariable String vendor,
										final HttpServletRequest request, final ModelMap model) throws Throwable {
		IService service = Vendors.valueOf(vendor).getService();
		
		String loginUser = (String) session.getAttribute(UserLoginIntercepter.LOGIN_USER);
		String oauthProvider = (String) session.getAttribute(UserLoginIntercepter.OAUTH_PROVIDER);
		String redirectUri = "/account/mycloud";
		User user = usersDAO.getUser(loginUser, oauthProvider);
		
		if (service instanceof GoogleService) {
			user.setGoogleToken("");
			clientManager.setGoogleClient(null);
		} else if(service instanceof DropboxService) {
			user.setDropboxToken("");
			clientManager.setDropboxClient(null);
		} else if(service instanceof FacebookService) {
			user.setFacebookToken("");
			clientManager.setFacebookClient(null);
			redirectUri = "/account/mysocial";
		} else if(service instanceof TwitterService) {
			user.setTwitterToken("");
			user.setTwitterTokenSecret("");
			clientManager.setTwitterClient(null);
			redirectUri = "/account/mysocial";
		}
		usersDAO.updateUser(user, oauthProvider);
		
		return new ModelAndView("redirect:"+redirectUri);
	}
	
}
