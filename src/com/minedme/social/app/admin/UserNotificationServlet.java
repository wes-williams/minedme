package com.minedme.social.app.admin;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;

import com.minedme.social.app.NavigationConstants;
import com.minedme.social.app.data.DataStore;
import com.minedme.social.app.data.NetworkDataHandler;
import com.minedme.social.app.data.SessionData;
import com.minedme.social.app.data.VisitorData;
import com.minedme.social.app.data.entity.ActivityDate;
import com.minedme.social.app.data.entity.Network;
import com.minedme.social.app.data.entity.User;
import com.minedme.social.app.data.entity.UserActivityScorecard;
import com.minedme.social.app.data.entity.UserNetwork;
import com.minedme.social.app.data.entity.UserNetworkActivity;
import com.minedme.social.app.data.oauth.UserData;
import com.minedme.social.util.MemcacheUtil;


@SuppressWarnings("serial")
public class UserNotificationServlet extends HttpServlet
{
	private final static Logger logger = Logger.getLogger(UserNotificationServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
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
			DataStore dataStore = new DataStore();
			try
			{
				dataStore.connect();
				
				ActivityDate activityDate = dataStore.yesterday();
				
				List<UserNetwork> userNetworksWithActivity = dataStore.findUsersForPossibleNotification();
				int failureCount = 0;
				int notifyCount = 0;
				for(UserNetwork userNetwork : userNetworksWithActivity)
				{
					try
					{						
						UserActivityScorecard activityScorecard = dataStore.findUserActivityScorecard(userNetwork.getUserId(), activityDate.getUtcTime());
						
						if(activityScorecard != null && activityScorecard.getNetworkCount() > 0)
						{
							UserNetworkActivity networkActivity = dataStore.findUserNetworkActivity(userNetwork.getUserId(),userNetwork.getNetworkId(), activityDate.getUtcTime());
							
							if(networkActivity!=null && networkActivity.getCount() > 0) // don't want to spam a network I never use...
							{
								if(notify(dataStore,userNetwork,activityScorecard,networkActivity))
								{
									activityScorecard.setNotificationSent(System.currentTimeMillis());
									dataStore.saveUserActivityScorecard(activityScorecard);
									
									// start - load this game into memcache
									String publicId = activityScorecard.getPublicId();
									logger.info("Lookup VisitorData for public id: " + publicId);
									VisitorData publicIdData = dataStore.lookupVistorData(publicId);
									
									if(publicIdData != null)
									{			
										ObjectMapper mapper = new ObjectMapper();
										logger.info("Turn data into json for public id: " + publicId);
										String publicIdDataJson = mapper.writeValueAsString(publicIdData);
										
										logger.info("Write data to memcache for public id: " + publicId);
										MemcacheUtil.put(MemcacheUtil.GAME_NAMESPACE, publicId, Base64.encodeBase64String(publicIdDataJson.getBytes()));
									}
									// end - load this game into memcache
														
									notifyCount++;
								}
							}
						}
					}
					catch(Throwable t)
					{
						logger.log(Level.WARNING, "Failed notifying User " + userNetwork.getUserId() + " on Network " + userNetwork.getNetworkId() + " of outside activity on " + activityDate.getUtcTime(), t);
						failureCount++;
					}
				}
				
				logger.info("Processed " + userNetworksWithActivity.size() + " User Activity Notifications with " + notifyCount + " necessary notifications and " + failureCount + " failures");
			}
			finally
			{
				dataStore.disconnect();
			}
		}
		catch (Throwable t)
		{
			logger.log(Level.WARNING, "Failed notifications of user activity", t);
		}
	}
	
	private boolean notify(DataStore dataStore, UserNetwork userNetwork, UserActivityScorecard activityScorecard, UserNetworkActivity networkActivity) throws Exception
	{
		int outsideNetworkCount = activityScorecard.getNetworkCount()-networkActivity.getCount();
		int outsideNetworkPercent = 100 - ((int)((networkActivity.getCount()/Float.valueOf(activityScorecard.getNetworkCount()))*100)); 
		
		if(outsideNetworkPercent <= 40 || outsideNetworkCount <= 2 ) // other networks do not make a majority of the traffic
		{
			logger.info("Only " + outsideNetworkCount + "/" + outsideNetworkPercent + "% outside the network");
			return false;
		}
		
		logger.info("Notifiying network #" + userNetwork.getNetworkId() + " for User: " + userNetwork.getId() + " that " + outsideNetworkCount + "/"+ outsideNetworkPercent + "% of Activity was outside network on " + activityScorecard.getActivityDate());
			
		Network network = dataStore.retrieveNetwork(userNetwork.getNetworkId());
		NetworkDataHandler networkDataHandler = dataStore.getNetworkHandler(network);
		
		networkDataHandler.sendNotification(userNetwork, activityScorecard, outsideNetworkCount, outsideNetworkPercent);
		
		return true;
	}
}