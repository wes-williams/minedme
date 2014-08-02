package com.minedme.social.linkedin.data;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.LinkedInApi;
import org.scribe.oauth.OAuthService;

import com.minedme.social.app.data.DataStore;
import com.minedme.social.app.data.SessionData;
import com.minedme.social.util.EncryptionUtil;
import com.minedme.social.app.data.entity.Network;
import com.minedme.social.app.data.entity.UserNetwork;
import com.minedme.social.app.data.oauth.UserData;

public class LinkedInDataStore extends DataStore
{
	private final static Logger logger = Logger.getLogger(LinkedInDataStore.class.getName());
	
	private static final String NETWORK_NAME = "LINKEDIN";
	private static final String USER_URL ="http://api.linkedin.com/v1/people/~:(id,num-connections,public-profile-url)?format=json";
	private static final String CONNECTIONS_URL = "http://api.linkedin.com/v1/people/~:(id,num-connections)?format=json";
	
/*	
	private static final String API_KEY = "XXXXXXXXXXXXXXXXX";
	private static final String API_SECRET = "XXXXXXXXXXXXXXXXX";
	private static final String API_CALLBACK_URL = "https://mined-me.appspot.com/liCallback";
*/
	private Network network;
	private OAuthService service;
	
	public LinkedInDataStore()
	{
	}
	
	public static boolean isForNetwork(Network network)
	{
		return NETWORK_NAME.equals(network.getName());
	}
	
	public Network retrieveNetwork()
	{
		if(network !=null)
		{
			return network;
		}
		
		network = findNetworkByName(NETWORK_NAME);
	
/*		
		if(network == null)
		{
			try
			{
					network = new Network();
					network.setName(NETWORK_NAME);
					
					ObjectMapper mapper = new ObjectMapper();
					HashMap<String,String> privateData = new HashMap<String,String>();
					privateData.put("apiKey", EncryptionUtil.decrypt(API_KEY));
					privateData.put("apiSecret", EncryptionUtil.decrypt(API_SECRET));
					privateData.put("apiCallbackUrl",API_CALLBACK_URL);
					network.setPrivateData(EncryptionUtil.encrypt(mapper.writeValueAsString(privateData)));
					
					network.setCreatedDate(System.currentTimeMillis());
					
					pm.makePersistent(network);
			}
			catch(Exception ex)
			{
				logger.warning("Failure creating LINKEDIN network");
			}
		}
*/
		
		return network;
	}
	
	public OAuthService getService(Network network) throws Exception
	{
		if(service == null)
		{
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String,String> privateData = (HashMap<String,String>) mapper.readValue(EncryptionUtil.decrypt(network.getPrivateData()), HashMap.class);
			
			service = new ServiceBuilder()
		 					.provider(LinkedInApi.class)
		 					.apiKey(privateData.get("apiKey"))
		 					.apiSecret(privateData.get("apiSecret"))
		 					.callback(privateData.get("apiCallbackUrl")).build();
		}
		
		return service;
	}
	
	public static String createConnectionsUrl()
	{				
		return CONNECTIONS_URL;
	}
	
	public static String createUserUrl()
	{
		return USER_URL;
	}
	
	public LinkedInUserData retrieveUserData(UserNetwork userNetwork) 
	{		
		LinkedInUserData userData = null;
		
		try
		{
			String decryptedJson = EncryptionUtil.decrypt(userNetwork.getPrivateData());
			ObjectMapper mapper = new ObjectMapper();
			userData = mapper.readValue(decryptedJson, LinkedInUserData.class);
		}
		catch(Throwable t)
		{
			logger.warning("Failure Retrieving User Data: " + t.getMessage());
		}
		
		return userData;
	}
	
	public boolean storeUserData(UserData userData,UserNetwork userNetwork)  
	{
		try
		{
			LinkedInUserData fsUserData = (LinkedInUserData) userData;
			
			StringWriter writer = new StringWriter();
			ObjectMapper mapper = new ObjectMapper();
			mapper.writeValue(writer, fsUserData);
			writer.flush();
			String encryptedJson = EncryptionUtil.encrypt(writer.toString());

			userNetwork.setPrivateData(encryptedJson);
		}
		catch(Throwable t)
		{
			logger.warning("Failure Storing User Data: " + t.getMessage());
			return false;
		}
		
		return true;	
	}

	
}
