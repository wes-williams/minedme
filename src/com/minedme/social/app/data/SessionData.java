package com.minedme.social.app.data;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.model.Token;

import com.minedme.social.app.data.entity.User;
import com.minedme.social.util.EncryptionUtil;
import com.minedme.social.util.MemcacheUtil;

public class SessionData 
{
	private final static Logger logger = Logger.getLogger(SessionData.class.getName());
	
	private final static String SESSION_USER_KEY = "mm.session.active_session_user";
	private final static String SESSION_ID_VERIFIER_KEY = "mm.session.active_session_verifier";
	private final static String SESSION_ID_VERIFIER_SECRET_KEY = "mm.session.active_session_timestamp";
	
	private final static String COOKIE_SESSION_KEY = "STK"; // "JSESSIONID"; // use jsessionid here for control over cookie lifespan
	private final static String COOKIE_SESSION_VERIFIER_KEY = "TVR"; 
	
	private final static String REQUEST_ALREADY_VALIDATED_KEY = "mm.session.request_already_validated";
	
	private HttpSession session;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public SessionData(HttpServletRequest req,HttpServletResponse resp)
	{
		this.session = req.getSession(true);
		this.request = req;
		this.response = resp;
	}
	
	public boolean isValid()
	{		
		// the SESSION_USER_KEY defines the actual user. 
		if(session.getAttribute(SESSION_USER_KEY) != null)
		{
			// the session could have been validated in a previous request before servlet chaining started
			// it would be silly to do it twice given the expense
			String requestAlreadyValidated = (String) request.getAttribute(REQUEST_ALREADY_VALIDATED_KEY); 
			if(requestAlreadyValidated != null && requestAlreadyValidated.length() > 0)
			{
				if(requestAlreadyValidated.equals((String) session.getAttribute(SESSION_ID_VERIFIER_SECRET_KEY)))
				{
					return true;
				}
				
				logger.warning("This request is flagged as already validated, but the session doesn't match!");
				return false;
			}
			
			try
			{
				// verify the session cookie
				if(verifySessionCookie())
				{
					try
					{
						resetSessionCookie();
						return true;
					}
					catch(Exception ex)
					{
						logger.info("Failure resetting Session Cookie");
					}		
				}
			}
			catch(Exception ex)
			{
				logger.info("Failure verifying Session Cookie");
			}
		}
		
		return false;
	}
	
	public void invalidate()
	{
		try
		{
			session.removeAttribute(SESSION_USER_KEY);
			session.removeAttribute(SESSION_ID_VERIFIER_SECRET_KEY);
			deleteSessionCookie(request);
			session.invalidate();
		}
		catch(Throwable t)
		{
			logger.info("Failed to invalidate session");
		}
	}
	
	public Long retrieveUserId()
	{
		Long userId = null;
		
		try
		{
			userId =  Long.parseLong(EncryptionUtil.decrypt((String)session.getAttribute(SESSION_USER_KEY)));
		}
		catch(Throwable t)
		{
			logger.info("Failure retrieving username: " + t.getMessage());
		}
		
		return userId;
	}
	
	public boolean activate(User user)
	{
		try
		{
			String sessionFlag = EncryptionUtil.encrypt(String.valueOf(user.getId()));
			session.setAttribute(SESSION_USER_KEY, sessionFlag);
			resetSessionCookie();
			return true;
		}
		catch(Throwable t)
		{
			logger.info("Failure activating user: " + t.getMessage());
		}

		return false;
	}
	
	public void storeKVP(String key, String value)
	{
		session.setAttribute(key, value);
	}
	
	public String retrieveValue(String key)
	{
		return (String) session.getAttribute(key);
	}
	
	public static boolean isValid(HttpServletRequest req, HttpServletResponse resp)
	{
		SessionData sessionData = new SessionData(req,resp);
		
		return sessionData.isValid();
	}
	
	public static void invalidate(HttpServletRequest req, HttpServletResponse resp)
	{
		SessionData sessionData = new SessionData(req,resp);
		sessionData.invalidate();
	}
	
	private void deleteSessionCookie(HttpServletRequest req)
	{
		Cookie[] cookies = req.getCookies();
		for(Cookie cookie : cookies)
		{
			cookie.setMaxAge(0); // delete the cookie
		}
	}
	
	private void resetSessionCookie() throws Exception
	{
		// 60 seconds * 60 minutes * 24 hours * 14 days
		final int TWO_WEEKS = 1209600;
		
		// get session id
		String sessionId = session.getId();

		// create a signed cookie to verify session cookie
		String verifierSecret = String.valueOf(System.currentTimeMillis());
		String sessionVerifier = EncryptionUtil.sha1Random(sessionId, verifierSecret);
		// store both the verifier and secret in the session - will stay on server for two weeks
		session.setAttribute(SESSION_ID_VERIFIER_KEY, sessionVerifier);
		session.setAttribute(SESSION_ID_VERIFIER_SECRET_KEY, verifierSecret);

		// set session cookie for sessions beyond browser close
		Cookie persistentSessionCookie = new Cookie(COOKIE_SESSION_KEY, sessionId);
		persistentSessionCookie.setSecure(true);
		persistentSessionCookie.setPath("/");
		persistentSessionCookie.setMaxAge(TWO_WEEKS);
		response.addCookie(persistentSessionCookie);

		Cookie persistentSessionVerifierCookie = new Cookie(COOKIE_SESSION_VERIFIER_KEY, sessionVerifier);
		persistentSessionVerifierCookie.setSecure(true);
		persistentSessionVerifierCookie.setPath("/");
		persistentSessionVerifierCookie.setMaxAge(TWO_WEEKS);
		response.addCookie(persistentSessionVerifierCookie);
		
		// this is checked in isValid() to prevent a duplicate check while servlet chaining
		request.setAttribute(REQUEST_ALREADY_VALIDATED_KEY,verifierSecret);
	}
	
	private boolean verifySessionCookie() throws Exception
	{
		// get the jsessionid
		String sessionId = session.getId();
		// get the timestamp to salt the session - makes it unique per request
		String sessionVerifierSecret = (String) session.getAttribute(SESSION_ID_VERIFIER_SECRET_KEY);
		
		// make sure both exist
		if(sessionId != null && sessionVerifierSecret != null &&
				sessionId.length() > 0 && sessionVerifierSecret.length() > 0)
		{
			// make sha1 to compare with cookie sha1
			String sessionVerifier = EncryptionUtil.sha1Random(sessionId, sessionVerifierSecret);
			
			Cookie[] cookies = request.getCookies();
			if(cookies.length>=2)
			{
				String cookieSessionId = null;
				String cookieSessionVerifier = null;
				
				for(Cookie cookie : cookies)
				{
					// find the session cookie
					if(COOKIE_SESSION_KEY.equals(cookie.getName()))
					{
						cookieSessionId = cookie.getValue();
					}
					// find the session verifier in the cookie
					else if(COOKIE_SESSION_VERIFIER_KEY.equals(cookie.getName()))
					{
						cookieSessionVerifier = cookie.getValue();
					}
				}
				
				if(cookieSessionId != null && cookieSessionVerifier != null &&
						cookieSessionId.length() > 0 && cookieSessionVerifier.length() > 0)
				{
					// make the cookie sha1
					String localCookieSessionVerifier = EncryptionUtil.sha1Random(cookieSessionId,sessionVerifierSecret);
					
					// check that the verified sessions match
					return sessionVerifier.equals(cookieSessionVerifier) && sessionVerifier.equals(localCookieSessionVerifier);
				}
			}
		}
		
		return false;
	}
	
	public void storeRequestToken(String networkName, Token requestToken) throws Exception
	{
		if(networkName == null || networkName.trim().length()==0)
		{
			throw new IllegalArgumentException("Network Name is Required");
		}
		
		HashMap<String,String> tokenJson = new HashMap<String,String>();
		tokenJson.put("token", requestToken.getToken());
		tokenJson.put("secret", requestToken.getSecret());
		ObjectMapper mapper = new ObjectMapper();
		String encryptedToken = EncryptionUtil.encrypt(mapper.writeValueAsString(tokenJson));
		// This may be a bad idea for the long term...
		MemcacheUtil.put(MemcacheUtil.CONNECT_NAMESPACE, networkName + "-" + session.getId(),encryptedToken, 120);
	}
	
	public Token retrieveRequestToken(String networkName) 
	{
		Token token = null;
		try
		{
			String encryptedToken = MemcacheUtil.get(MemcacheUtil.CONNECT_NAMESPACE,  networkName + "-" + session.getId());
			if(encryptedToken != null)
			{
				ObjectMapper mapper = new ObjectMapper();
				HashMap<String,String> tokenJson = (HashMap<String,String>) mapper.readValue(EncryptionUtil.decrypt(encryptedToken), HashMap.class);
				token = new Token(tokenJson.get("token"),tokenJson.get("secret"));
			}
		}
		catch(Exception ex)
		{
		}
		
		return token;	
	}
}
