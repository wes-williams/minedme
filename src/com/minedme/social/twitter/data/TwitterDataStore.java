package com.minedme.social.twitter.data;

import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.TwitterApi;
import org.scribe.oauth.OAuthService;

import com.minedme.social.app.data.DataStore;
import com.minedme.social.app.data.SessionData;
import com.minedme.social.util.EncryptionUtil;
import com.minedme.social.app.data.entity.Network;
import com.minedme.social.app.data.entity.UserNetwork;
import com.minedme.social.app.data.oauth.UserData;
import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

public class TwitterDataStore extends DataStore
{
	private final static Logger logger = Logger.getLogger(TwitterDataStore.class.getName());
	
	private static final String NETWORK_NAME = "TWITTER";
	private static final String USER_URL ="http://api.twitter.com/1.1/account/verify_credentials.json";
	private static final String TWEETS_URL = "http://api.twitter.com/1.1/users/show.json?include_entities=false&screen_name=";

/*
	private static final String API_KEY = "XXXXXXXXXXXXXXXXX";
	private static final String API_SECRET = "XXXXXXXXXXXXXXXXX";
	private static final String API_CALLBACK_URL = "https://mined-me.appspot.com/twCallback";
*/
	
	private Network network;
	private OAuthService service;
	
	public TwitterDataStore()
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
				logger.warning("Failure creating TWITTER network");
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
		 					.provider(TwitterApi.class)
		 					.apiKey(privateData.get("apiKey"))
		 					.apiSecret(privateData.get("apiSecret"))
		 					.callback(privateData.get("apiCallbackUrl")).build();
		}
		
		return service;
	}
	
	public static String createTweetsUrl(String screenName)
	{				
		return TWEETS_URL + screenName;
	}
	
	public static String createUserUrl()
	{
		return USER_URL;
	}
	
	public TwitterUserData retrieveUserData(UserNetwork userNetwork) 
	{		
		TwitterUserData userData = null;
		
		try
		{
			String decryptedJson = EncryptionUtil.decrypt(userNetwork.getPrivateData());
			ObjectMapper mapper = new ObjectMapper();
			userData = mapper.readValue(decryptedJson, TwitterUserData.class);
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
			TwitterUserData fsUserData = (TwitterUserData) userData;
			
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
