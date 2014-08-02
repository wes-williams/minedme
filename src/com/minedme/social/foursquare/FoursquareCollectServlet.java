package com.minedme.social.foursquare;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.*;
import java.util.HashMap;
import java.util.List;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.minedme.social.app.data.entity.ActivityDate;
import com.minedme.social.app.data.entity.Network;
import com.minedme.social.app.data.entity.User;
import com.minedme.social.app.data.entity.UserNetwork;
import com.minedme.social.app.data.entity.UserNetworkActivity;
import com.minedme.social.foursquare.data.FoursquareDataStore;
import com.minedme.social.foursquare.data.FoursquareUserData;
import com.minedme.social.util.EncryptionUtil;

import org.codehaus.jackson.map.ObjectMapper;


@SuppressWarnings("serial")
public class FoursquareCollectServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(FoursquareCollectServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		if("true".equals(req.getHeader("X-AppEngine-Cron")))
		{
			logger.info("Being called from Cron");
		}
		else
		{
			logger.info("Only allowed to be called from Cron - Exiting now.");
			return;
		}
		
		try
		{
			FoursquareDataStore dataStore = new FoursquareDataStore();
			try
			{
				logger.info("Connect to DataStore");
				dataStore.connect();
				
				logger.info("Retrieve FOURSQUARE Network");
				Network network = dataStore.retrieveNetwork();
				logger.info("Retrieve Yesterday");
				ActivityDate activityDate = dataStore.yesterday();
				
				logger.info("Find Readable User Networks for Network " + network.getId());
				List<UserNetwork> userNetworks = dataStore.findReadableUserNetworksForNetwork(network.getId());
				logger.info("Found " + userNetworks.size() + " User Networks");
				int failureCount = 0;
				for(UserNetwork userNetwork : userNetworks)
				{
					try
					{
						logger.info("Checking if User " + userNetwork.getUserId() + " Activity already exist in Network " + userNetwork.getNetworkId() + " on " + activityDate.getUtcTime());
						if(dataStore.findUserNetworkActivity(userNetwork.getUserId(), network.getId(), activityDate.getUtcTime()) != null)
						{
							logger.info("Activity already present on Date " + activityDate.getUtcTime() + " in network " + network.getId() + " for user #" + userNetwork.getUserId() );
							continue;
						}
						
						collect(dataStore, network,userNetwork,activityDate);
					}
					catch(Throwable t)
					{
						logger.log(Level.WARNING, "Failed collecting from Foursquare for User " + userNetwork.getUserId(), t);
						failureCount++;
					}
				}
				
				logger.info("Processed " + userNetworks.size() + " User Networks with " + failureCount + " failures");
			}
			finally
			{
				dataStore.disconnect();
			}
		}
		catch(Throwable t)
		{
			logger.log(Level.WARNING, "Failed Foursquare Collect", t);
		}

	}
	
	private void collect(FoursquareDataStore dataStore, Network network, UserNetwork userNetwork, ActivityDate activityDate) throws Exception, RuntimeException
	{	
		FoursquareUserData userData = dataStore.retrieveUserData(userNetwork);
		
		Token accessToken = new Token(userData.getToken(), userData.getSecret()); 
		
		if(accessToken != null) 
		{
			OAuthService service = dataStore.getService(network);
			
			logger.info("Accessing the resource...");
			OAuthRequest request = new OAuthRequest(Verb.GET, FoursquareDataStore.createCheckinUrl(accessToken.getToken()));
			service.signRequest(accessToken, request);
			
			Response response = null;
			
			int retryCount = 0;
			while(retryCount<3) // try three times
			{
				if(retryCount > 0)
				{
					Thread.sleep(2000 * retryCount); // wait a little longer each time
				}
				
				retryCount++;
				try
				{
					response = request.send();
					break;
				}
				catch(org.scribe.exceptions.OAuthException ex)
				{
					logger.info("Failed connecting to foursquare: " + ex.getMessage());
				}
			}

			UserNetworkActivity userNetworkActivity = new UserNetworkActivity();
			userNetworkActivity.setNetworkId(network.getId());
			userNetworkActivity.setUserId(userNetwork.getUserId());
			userNetworkActivity.setActivityDate(activityDate.getUtcTime());
			userNetworkActivity.setNetworkUrl(EncryptionUtil.encrypt("https://foursquare.com/user/" + userData.getId()));
			
			if(response==null)
			{
				logger.info("Failed 3 attempts of connecting to foursquare for user #" + userNetwork.getUserId());
				
				// must store something
				dataStore.saveUserNetworkActivity(userNetworkActivity);
				
				throw new Exception("Failed 3 attempts of connecting to foursquare");
			}
			
			if(response.getCode() == 200)
			{
				Integer checkinCount = null;	
				try
				{
					ObjectMapper mapper = new ObjectMapper();
					HashMap data = mapper.readValue(response.getBody(), HashMap.class);
					checkinCount = (Integer) ((HashMap)((HashMap)data.get("response")).get("checkins")).get("count");
					
					if(checkinCount > userNetwork.getActivityCount())
					{
						userNetworkActivity.setCount((byte)(checkinCount-userNetwork.getActivityCount()));
					}
					else
					{
						userNetworkActivity.setCount((byte)0);
					}
					
					userNetwork.setActivityCount(checkinCount);
					dataStore.saveUserNetwork(userNetwork);
				}
				catch(Exception e)
				{
					logger.info("Failed to extract foursquare activity count for user #" + userNetwork.getUserId());
					throw new Exception("Failed to extract activity count", e);
				}
				finally // must save something regardless
				{
					dataStore.saveUserNetworkActivity(userNetworkActivity);
				}

				logger.info("Recorded Activity of " + checkinCount + " on Date " + activityDate.getUtcTime() + " in network " + network.getId() + " for user #" + userNetwork.getUserId() );
			}

		}
	}
}
