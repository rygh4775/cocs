package com.cocs.webapp.controller;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.amber.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import twitter4j.Twitter;

import com.cocs.common.DefaultConstants;
import com.cocs.common.Env;
import com.cocs.common.EveryoneAccessable;
import com.cocs.common.URLParser;
import com.cocs.mail.MailSender;
import com.cocs.mail.Template;
import com.cocs.security.DesCrypt;
import com.cocs.server.User;
import com.cocs.server.dao.UsersDAO;
import com.cocs.service.ClientInitializer;
import com.cocs.service.ClientManager;
import com.cocs.service.facebook.FacebookService;
import com.cocs.service.naver.NaverService;
import com.cocs.webapp.exception.UserLoginException;
import com.cocs.webapp.intercepter.UserLoginIntercepter;
import com.dropbox.core.DbxClient;
import com.google.api.services.drive.Drive;

import facebook4j.Facebook;



@Controller
public class LoginController implements EveryoneAccessable, DefaultConstants{
	
	@Autowired
	ClientManager clientManager;
	
	@Autowired
	UsersDAO users;
	
	@Autowired
	MailSender mailSender;
	
	@RequestMapping(value = { "/signin" })
	public ModelAndView signin(final HttpServletRequest request, final ModelMap model) throws UserLoginException {
		return new ModelAndView("signin/index");
	}
	
	@RequestMapping(value = { "/signin/" })
	public ModelAndView signin2(final HttpServletRequest request, final ModelMap model) throws UserLoginException {
		return new ModelAndView("signin/index");
	}
	
	@RequestMapping(value = { "/signup" })
	public ModelAndView signup(final HttpServletRequest request, final ModelMap model) throws UserLoginException {
		return new ModelAndView("signup/index");
	}
	
	@RequestMapping(value = { "/signup/" })
	public ModelAndView signup2(final HttpServletRequest request, final ModelMap model) throws UserLoginException {
		return new ModelAndView("signup/index");
	}
	
	@RequestMapping(value = { "/sentMail" })
	public ModelAndView sentMail(final HttpServletRequest request, final ModelMap model) throws UserLoginException {
		return new ModelAndView("signup/sentMail");
	}
	
	@RequestMapping(value = { "/sentMail/" })
	public ModelAndView sentMail2(final HttpServletRequest request, final ModelMap model) throws UserLoginException {
		return new ModelAndView("signup/sentMail");
	}
	
	@RequestMapping(value = { "/forgotPassword" })
	public ModelAndView forgotPassword(final HttpServletRequest request, final ModelMap model) throws UserLoginException {
		return new ModelAndView("forgotPassword/index");
	}
	
	@RequestMapping(value = { "/forgotPassword/" })
	public ModelAndView forgotPassword2(final HttpServletRequest request, final ModelMap model) throws UserLoginException {
		return new ModelAndView("forgotPassword/index");
	}
	
	@RequestMapping(value = { "/legal" })
	public ModelAndView legal(final HttpServletRequest request, final ModelMap model) throws UserLoginException {
		return new ModelAndView("legal/index");
	}
	
	@RequestMapping(value = { "/legal/" })
	public ModelAndView legal2(final HttpServletRequest request, final ModelMap model) throws UserLoginException {
		return new ModelAndView("legal/index");
	}
	
	@RequestMapping(value = { "/doSignin.*" })
	public ModelAndView signin(	HttpSession session,
								@RequestParam(value="email",required=false,defaultValue="") String email,
								@RequestParam(value="password",required=false,defaultValue="") String password,
								@RequestParam(value="oauth_provider",required=false,defaultValue="default") String oauth_provider,
								@RequestParam(value="code",required=false,defaultValue="") String code,
								final HttpServletRequest request, final ModelMap model) throws Throwable {
		
		User user = null;
		
		if("facebook".equals(oauth_provider)) {
			FacebookService facebookService = new FacebookService();
			OAuthAccessTokenResponse tokenResponse = facebookService.getTokenResponse(code, Env.getProperty("facebook.redirect.signin.uri"));
			String accessToken = tokenResponse.getAccessToken();
			
			Facebook facebookClient = ClientInitializer.getFacebookClient(accessToken);
			if(facebookClient != null) {
				email = facebookClient.getMe().getEmail();
			}
			
			user = users.getUser(email, oauth_provider);
			
			if(user == null || !"facebook".equals(user.getOauthProvider())) {
				user = new User();
				user.setId(email);
				user.setOauthProvider("facebook");
				user.setFacebookToken(accessToken);
				users.newUser(user);
			}
		} else if("naver".equals(oauth_provider)) {
			NaverService naverService = new NaverService();
			OAuthAccessTokenResponse tokenResponse = naverService.getTokenResponse(code);
			String accessToken = tokenResponse.getAccessToken();
			
			email = naverService.getEmail(accessToken);
			
			user = users.getUser(email, oauth_provider);
			
			if(user == null || !"naver".equals(user.getOauthProvider())) {
				user = new User();
				user.setId(email);
				user.setOauthProvider("naver");
				users.newUser(user);
			}
		} else {
			if(StringUtils.isBlank(email) || StringUtils.isBlank(password)) {
				throw new UserLoginException("계정 정보를 입력하세요.");
			}
			
			if(!users.equalsPassword(email, password)) {
				throw new UserLoginException("이메일 또는 비밀번호가 일치하지 않습니다.");
			}
			user = users.getUser(email, oauth_provider);
		}
		
		setClinets(user);
		
		session.setAttribute(UserLoginIntercepter.LOGIN_USER, user.getId());
		session.setAttribute(UserLoginIntercepter.OAUTH_PROVIDER, user.getOauthProvider());
		
		if("system".equals(user.getId())) {
			return new ModelAndView("redirect:./admin");
		}
		return new ModelAndView("redirect:./home");
	}
	
	@RequestMapping(value = { "/signout.*" })
	public ModelAndView signout(HttpSession session,	
								@RequestParam(value="email",required=false,defaultValue="") String email,
								@RequestParam(value="password",required=false,defaultValue="") String password,
								@RequestParam(value="confirmPassword",required=false,defaultValue="") String confirmPassword,
								final ModelMap model) throws UserLoginException {
		session.invalidate();
		clientManager.invalidate();
		return new ModelAndView("redirect:./main");
	}
	
	@RequestMapping(value = { "/doSignup.*" })
	public void signup(	@RequestParam(value="email",required=false,defaultValue="") String email,
								@RequestParam(value="password",required=false,defaultValue="") String password,
								@RequestParam(value="confirmPassword",required=false,defaultValue="") String confirmPassword,
								final HttpServletRequest request, final ModelMap model) throws Throwable {
		
		if(StringUtils.isBlank(email) || StringUtils.isBlank(password) || StringUtils.isBlank(password)) {
			model.put(SUCCESS, false);
			model.put("message", "회원 정보를 입력하세요.");
			return;
		}
		
		if(!password.equals(confirmPassword)) {
			model.put(SUCCESS, false);
			model.put("message", "비밀번호가 일치하지 않습니다.");
			return;
		}
		
		if(users.exists(email, "default")) {
			model.put(SUCCESS, false);
			model.put("message", "이미 사용 중인 이메일입니다.");
			return;
		}
		
		String target = email + "::" + password;
		String key = DesCrypt.doEncode(target);
		String link = URLParser.getURLWithContextPath(request) + "/signupComplete.do?key=" + key;
		String resourceURL = URLParser.getResourceURL(request);
		
		try {
			HashMap<String, Object> prop = new HashMap<String, Object>();
			prop.put("resourceURL", resourceURL);
			prop.put("link", link);
			prop.put("email", email);
			
			mailSender.send(Template.SIGNUP, email, prop);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		
		model.put(SUCCESS, true);
	}
	
	@RequestMapping(value = { "/signupComplete.*" })
	public ModelAndView signupComplete(	@RequestParam(value="key",required=false,defaultValue="") String key,
										final HttpServletRequest request, final ModelMap model) throws Throwable {
		try {
			key = DesCrypt.doDecode(key);
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("error/invalidLink");
		}
		String[] keyInfo = key.split("::");

		String email = keyInfo[0];
		String password = keyInfo[1];
		
		model.put(EMAIL, email);
		
		if(users.exists(email, "default")) {
			model.put("email", email);
			model.put(SUCCESS, false);
			return new ModelAndView("signup/signupComplete");
		}
		
		User user = new User();
		user.setId(email);
		user.setPassword(password);
		user.setOauthProvider("default");
		
		users.newUser(user);
		
		model.put(SUCCESS, true);
		model.put("email", email);
		
		
		return new ModelAndView("signup/signupComplete");
	}
	
	@RequestMapping(value = { "/doForgotPassword.*" })
	public void forgotPassword(	@RequestParam(value="email",required=false,defaultValue="") String email,
								//@RequestParam(value="password",required=false,defaultValue="") String password,
								@RequestParam(value="confirmPassword",required=false,defaultValue="") String confirmPassword,
								final HttpServletRequest request, final ModelMap model) throws Throwable {
		
		if(StringUtils.isBlank(email)) {
			model.put(SUCCESS, false);
			model.put("message", "이메일 정보를 입력하세요.");
			return;
		}
		
		if(!users.exists(email, "default")) {
			model.put(SUCCESS, false);
			model.put("message", "등록되지 않은 이메일 입니다.");
			return;
		}
		
		String target = email + "::" + System.currentTimeMillis();
		String key = DesCrypt.doEncode(target);
		
		String link = URLParser.getURLWithContextPath(request) + "/newPassword.do?key=" + key;
		String resourceURL = URLParser.getResourceURL(request);
		
		Map<String, Object> prop = new HashMap<String, Object>();
		prop.put("resourceURL", resourceURL);
		prop.put("link", link);
		prop.put("email", email);
		
		try {
			mailSender.send(Template.FORGOTPASSWORD, email, prop);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
		
		model.put(SUCCESS, true);
	}
	
	@RequestMapping(value = { "/newPassword.*" })
	public ModelAndView newPassword(@RequestParam(value="key",required=false,defaultValue="") String key,
									final HttpServletRequest request, final ModelMap model) {
		try {
			key = DesCrypt.doDecode(key);
		} catch (Exception e) {
			e.printStackTrace();
			return new ModelAndView("error/invalidLink");
		}
		String[] keyInfo = key.split("::");

		String email = keyInfo[0];
		long timeStamp = Long.parseLong(keyInfo[1]);

		Calendar expiredTime = Calendar.getInstance();
		expiredTime.setTimeInMillis(timeStamp);
		expiredTime.add(Calendar.HOUR, 1);
		
		if(System.currentTimeMillis() > expiredTime.getTimeInMillis()) {
			return new ModelAndView("error/invalidLink");
		}
		
		model.put(SUCCESS, true);
		model.put("email", email);
		
		return new ModelAndView("forgotPassword/newPassword");
	}
	
	@RequestMapping(value = { "/forgotPasswordComplete.*" })
	public ModelAndView forgotPasswordComplete(	@RequestParam(value="email",required=false,defaultValue="") String email,
			@RequestParam(value="newPassword",required=false,defaultValue="") String password,
			@RequestParam(value="confirmPassword",required=false,defaultValue="") String confirmPassword,
										final HttpServletRequest request, final ModelMap model) throws Throwable {
		
		if(!users.exists(email, "default")) {
			return new ModelAndView("redirect:./forgotPassword");
		}
		
		if(!password.equals(confirmPassword)) {
			model.put(SUCCESS, false);
			model.put("message", "비밀번호가 일치하지 않습니다.");
//			This code doesn't make sense. It should be called by AJAX.
		}
		
		users.changePassword(email, password);
		
		model.put(SUCCESS, true);
		
		return new ModelAndView("forgotPassword/forgotPasswordComplete");
	}
	
	@RequestMapping(value = { "/authorize.*" })
	public ModelAndView authorize(	HttpSession session,
									@RequestParam(value="oauth_provider",required=false,defaultValue="") String oauth_provider,
									final HttpServletRequest request, final ModelMap model) throws Throwable {
		String url = null;
		if("facebook".equals(oauth_provider)) {
			url = Env.getProperty("facebook.authorize.url")+"?scope=email,publish_actions&client_id="+Env.getProperty("facebook.client.id")+"&redirect_uri="+Env.getProperty("facebook.redirect.signin.uri");
		}
		if("naver".equals(oauth_provider)) {
			url = Env.getProperty("naver.authorize.url")+"?response_type=code&state=green&client_id="+Env.getProperty("naver.client.id")+"&redirect_uri="+Env.getProperty("naver.redirect.signin.uri");
		}
		if(url == null) {
			throw new UserLoginException("oauth_provider is not set.");
		}
		return new ModelAndView("redirect:"+url);
	}
	
	private void setClinets(User user) {
		//Google authentication
		String googleRefreshToken = user.getGoogleToken();
		if(StringUtils.isNotBlank(googleRefreshToken) && clientManager.getGoogleClient() == null) {
			Drive googleClient = ClientInitializer.getGoogleClient(googleRefreshToken);
			if(googleClient != null) {
				clientManager.setGoogleClient(googleClient);
			}
		}
		//Dropbox authentication
		String dropboxAccessToken = user.getDropboxToken();
		if(StringUtils.isNotBlank(dropboxAccessToken) && clientManager.getDropboxClient() == null) {
			DbxClient dropboxClient = ClientInitializer.getDropboxClient(dropboxAccessToken);
			if(dropboxClient != null) {
				clientManager.setDropboxClient(dropboxClient);
			}
		}
		//Facebook authentication
		String facebookAccessToken = user.getFacebookToken();
		if(StringUtils.isNotBlank(facebookAccessToken) && clientManager.getFacebookClient() == null) {
			Facebook facebookClient = ClientInitializer.getFacebookClient(facebookAccessToken);
			if(facebookClient != null) {
				clientManager.setFacebookClient(facebookClient);
			}
		}
		//Twitter authentication
		String twitterAccessToken = user.getTwitterToken();
		String twitterAccessTokenSecret = user.getTwitterTokenSecret();
		if(StringUtils.isNotBlank(twitterAccessToken) && clientManager.getTwitterClient() == null) {
			Twitter twitterClient = ClientInitializer.getTwitterClient(twitterAccessToken, twitterAccessTokenSecret);
			if(twitterClient != null) {
				clientManager.setTwitterClient(twitterClient);
			}
		}
	}
}

