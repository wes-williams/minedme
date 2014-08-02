package com.minedme.social.facebook.data;

import java.io.StringWriter;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;

import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FacebookApi;
import org.scribe.oauth.OAuthService;

import com.minedme.social.app.data.DataStore;
import com.minedme.social.app.data.NetworkDataHandler;
import com.minedme.social.app.data.SessionData;
import com.minedme.social.util.EncoderUtil;
import com.minedme.social.util.EncryptionUtil;
import com.minedme.social.app.data.entity.Network;
import com.minedme.social.app.data.entity.UserNetwork;
import com.minedme.social.app.data.oauth.UserData;

public class FacebookDataStore extends DataStore
{
	private final static Logger logger = Logger.getLogger(FacebookDataStore.class.getName());
	
	private static final String NETWORK_NAME = "FACEBOOK";
	private static final String USER_URL = "https://graph.facebook.com/me?fields=id,link";
	
	private static final String ACTIVITY_URL = "https://api.facebook.com/method/fql.query?format=json&query=";
	private static final String ACTIVITY_QUERY = "SELECT 1 FROM stream WHERE actor_id = me() AND source_id = me() AND created_time >= ";

/*	
	private static final String API_KEY = "XXXXXXXXXXXXXXXXX";
	private static final String API_SECRET = "XXXXXXXXXXXXXXXXX";
	private static final String API_CALLBACK_URL = "https://mined-me.appspot.com/fbCallback";
*/
	
	private Network network;
	private OAuthService service;
	private FacebookDataHandler networkHandler;
	
	public FacebookDataStore()
	{
	}
		
	public static boolean isForNetwork(Network network)
	{
		return NETWORK_NAME.equals(network.getName());
	}
	
	public void disconnect()
	{
		super.disconnect();
		networkHandler=null;
	}
	
	@Override
	protected FacebookDataHandler getNetworkHandler()
	{
		if(networkHandler!=null)
		{
			return networkHandler;
		}
		
		networkHandler = new FacebookDataHandler(this);
		
		return networkHandler;
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
				logger.warning("Failure creating FACEBOOK network");
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
		 					.provider(FacebookApi.class)
		 					.apiKey(privateData.get("apiKey"))
		 					.apiSecret(privateData.get("apiSecret"))
		 					.callback(privateData.get("apiCallbackUrl")).build();
		}
		
		return service;
	}
	
	public static String createActivityUrl(long activityDate)
	{				
		String unixTime = String.valueOf(activityDate);
		unixTime = unixTime.substring(0,unixTime.length()-3);
		
		return ACTIVITY_URL + EncoderUtil.urlEncode(ACTIVITY_QUERY + unixTime);
	}
	
	public static String createPostUrl()
	{
		return null;
	}
	
	public static String createUserUrl()
	{
		return USER_URL;
	}
	
	public FacebookUserData retrieveUserData(UserNetwork userNetwork) 
	{		
		FacebookUserData userData = null;
		
		try
		{
			String decryptedJson = EncryptionUtil.decrypt(userNetwork.getPrivateData());
			ObjectMapper mapper = new ObjectMapper();
			userData = mapper.readValue(decryptedJson, FacebookUserData.class);
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
			FacebookUserData fsUserData = (FacebookUserData) userData;
			
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
