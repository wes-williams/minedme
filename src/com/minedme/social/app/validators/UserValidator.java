package com.minedme.social.app.validators;

import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;

import com.minedme.social.app.data.entity.User;
import com.minedme.social.util.EncryptionUtil;

public class UserValidator
{
	private final static Logger logger = Logger.getLogger(UserValidator.class.getName());
	
	public static boolean validateAndPopulate(User user, HttpServletRequest req)
	{	
		String username = req.getParameter("username");
		if(username != null)
		{
			username = username.trim();
			if(username.length()>0)
			{
				user.setUsername(username);
			}
		}
			
		if(user.getUsername()==null)
		{
			logger.info("User has an invalid username");
			return false;
		}
		
		String validPassword = null;
		String password1 = req.getParameter("password1");
		String password2 = req.getParameter("password2");
		if(password1 != null && password2 != null)
		{
			password1 = password1.trim();
			password2 = password2.trim();
			
			// 5 chars sounds reasonable
			if(password1.length() >= 5 && password1.equals(password2))
			{
				validPassword = password1;
			}
		}		
		
		if(validPassword==null)
		{
			logger.info("User has an invalid password");
			return false;
		}
		
		try
		{
			String sha1Password = EncryptionUtil.sha1(validPassword);
			HashMap<String, String> privateDataMap = new HashMap<String, String>();
			privateDataMap.put("password", sha1Password);
			ObjectMapper mapper = new ObjectMapper();
			String privateData = EncryptionUtil.encrypt(mapper.writeValueAsString(privateDataMap));
			user.setPrivateData(privateData);
		}
		catch(Throwable t)
		{
			logger.info("Error encrypting User's private data: " +  t.getMessage());
		}
		
		if(user.getPrivateData()==null)
		{
			return false;
		}
		
		
		String nickname = req.getParameter("nickname");
		if(nickname != null)
		{
			nickname = nickname.trim();
			user.setAccountName(nickname);
		}
		
		if(user.getAccountName()==null)
		{
			logger.info("User has an invalid nickname");
			return false;
		}
		
		String privacy = req.getParameter("privacy");
		if(privacy == null || "1".equals(privacy))
		{
			user.setPrivacyEnabled("1".equals(privacy));
		}
		else
		{
			logger.info("User has an invalid privacy flag");
			return false;
		}
		
		user.setCreatedDate(System.currentTimeMillis());
		
		return true;
	}
}
