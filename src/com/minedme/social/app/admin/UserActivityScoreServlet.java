package com.minedme.social.app.admin;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minedme.social.app.NavigationConstants;
import com.minedme.social.app.data.DataStore;
import com.minedme.social.app.data.SessionData;
import com.minedme.social.app.data.entity.ActivityDate;
import com.minedme.social.app.data.entity.User;


@SuppressWarnings("serial")
public class UserActivityScoreServlet extends HttpServlet
{
	private final static Logger logger = Logger.getLogger(UserActivityScoreServlet.class.getName());

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
				
				List<Long> usersWithActivity = dataStore.findUsersWithPossibleActivity();
				int failureCount = 0;
				for(Long userId : usersWithActivity)
				{
					try
					{
						User user = dataStore.retrieveUser(userId);
						
						if(dataStore.findUserActivityScorecard(userId, activityDate.getUtcTime()) != null)
						{
							logger.info("ActivityScorecard already present on Date " + activityDate.getUtcTime() + " for user #" + user.getId() );
							continue;
						}
						
						logger.info("Scoring Activity for User: " + user.getId());
						dataStore.scoreUserActivity(user, activityDate);
					}
					catch(Throwable t)
					{
						logger.log(Level.WARNING, "Failed scoring activity for User " + userId + " on " + activityDate.getUtcTime(), t);
						failureCount++;
					}
				}
				
				logger.info("Processed " + usersWithActivity.size() + " User Activities with " + failureCount + " failures");
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