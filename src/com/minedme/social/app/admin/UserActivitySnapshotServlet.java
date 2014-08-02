package com.minedme.social.app.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.minedme.social.app.data.MineOverviewData;
import com.minedme.social.app.data.SessionData;
import com.minedme.social.app.data.entity.ActivityDate;
import com.minedme.social.app.data.entity.User;
import com.minedme.social.app.data.entity.UserActivityScorecard;
import com.minedme.social.app.data.entity.UserActivitySnapshot;
import com.minedme.social.app.data.entity.UserNetwork;
import com.minedme.social.app.data.entity.UserNetworkActivity;
import com.minedme.social.app.data.entity.UserScorecard;
import com.minedme.social.util.MemcacheUtil;
import com.minedme.social.util.SnapshotUtil;


@SuppressWarnings("serial")
public class UserActivitySnapshotServlet extends HttpServlet
{
	private final static Logger logger = Logger.getLogger(UserActivitySnapshotServlet.class.getName());

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
			ObjectMapper mapper = new ObjectMapper();
			
			DataStore dataStore = new DataStore();
			try
			{
				dataStore.connect();
				
				ActivityDate activityDate = dataStore.yesterday();
				long startOfYear = dataStore.startOfYear(activityDate.getUtcTime());
				
				List<Long> usersWithActivity = dataStore.findUsersWithPossibleActivity();
				int failureCount = 0;
				for(Long userId : usersWithActivity)
				{
					try
					{						
						List<UserNetworkActivity> networkActivities = dataStore.findUserNetworkActivity(userId, activityDate.getUtcTime());
						
						if(networkActivities != null && networkActivities.size()>0)
						{
							Map<Long,List<UserNetworkActivity>> pastNetworkActivities= null;
							
							List<UserNetwork> userNetworks = dataStore.findReadableUserNetworks(userId);
							for(UserNetwork userNetwork : userNetworks)
							{
								if(pastNetworkActivities==null)
								{
									pastNetworkActivities = new  HashMap<Long,List<UserNetworkActivity>>();
								}
								
								if(activityDate.getUtcTime() > startOfYear)
								{
									pastNetworkActivities.put(userNetwork.getNetworkId(), 
														  dataStore.findUserNetworkActivityBetweenDates(userNetwork.getUserId(), userNetwork.getNetworkId(), 
																  										startOfYear, activityDate.getUtcTime()));
								}
								else
								{
									pastNetworkActivities.put(userNetwork.getNetworkId(), new ArrayList<UserNetworkActivity>());
								}
							}
							
							UserScorecard userScorecard = dataStore.findScorecardForUser(userId);
							UserActivityScorecard userActivityScorecard = dataStore.findUserActivityScorecard(userId, activityDate.getUtcTime());
		
							UserActivitySnapshot snapshot = SnapshotUtil.freezeData(userId,activityDate, userScorecard, userActivityScorecard, networkActivities,  pastNetworkActivities);
							
							logger.info("Saving snapshot for User " + userId + " on " + activityDate.getUtcTime());
							dataStore.saveUserActivitySnapshot(snapshot);
							
							
							try // preload for MineOverviewServlet
							{
								logger.info("Preloading overview of mine with new snapshot for user " + userId);
								
								MineOverviewData overview = dataStore.lookupMineOverview(userId, null,snapshot.getSnapshotDate(),snapshot);
								String encodedOverviewJson = Base64.encodeBase64String(mapper.writeValueAsString(overview).getBytes());
								// loading basic snapshot
								MemcacheUtil.put(MemcacheUtil.MINE_SNAPSHOT_NAMESPACE, userId.toString(), encodedOverviewJson);
								// loading date specific snapshot
								MemcacheUtil.put(MemcacheUtil.MINE_SNAPSHOT_NAMESPACE, userId + "__" + overview.getPublicId(), encodedOverviewJson);
							}
							catch(Throwable t) // make sure the old snapshot is cleared from memcache
							{
								logger.log(Level.WARNING,"Failed to preload overview for user " + userId, t);
								
								logger.info("Removing current cached snapshot for user " + userId);
								MemcacheUtil.remove(MemcacheUtil.MINE_SNAPSHOT_NAMESPACE, userId.toString());
							}
							
						}
						else
						{
							logger.info("Nothing to snapshot for User " + userId + " on " + activityDate.getUtcTime());
						}
					}
					catch(Throwable t)
					{
						logger.log(Level.WARNING, "Failed creating activity snapshot for User " + userId + " on " + activityDate.getUtcTime(), t);
						failureCount++;
					}
				}
				
				logger.info("Processed " + usersWithActivity.size() + " User Activity Snapshots with " + failureCount + " failures");
			}
			finally
			{
				dataStore.disconnect();
			}
		}
		catch (Throwable t)
		{
			logger.log(Level.WARNING, "Failed scoring user activity", t);
		}
	}
}