package com.minedme.social.app.admin;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minedme.social.app.data.DataStore;
import com.minedme.social.app.data.entity.ActivityDate;
import com.minedme.social.app.data.entity.UserActivityScorecard;


@SuppressWarnings("serial")
public class UserScoreServlet extends HttpServlet
{
	private final static Logger logger = Logger.getLogger(UserScoreServlet.class.getName());

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
				
				List<UserActivityScorecard> userActivityScorecards = dataStore.findUserActivityScorecardsForDate(activityDate.getUtcTime());
				int failureCount = 0;
				for(UserActivityScorecard userActivityScorecard : userActivityScorecards)
				{
					try
					{	
						logger.info("Scoring for User " + userActivityScorecard.getUserId() + " on " + userActivityScorecard.getActivityDate() );
						dataStore.scoreUser(userActivityScorecard);
					}
					catch(Throwable t)
					{
						logger.log(Level.WARNING, "Failed scoring activity for User " + userActivityScorecard.getUserId() + " on " + userActivityScorecard.getActivityDate(), t);
						failureCount++;
					}
				}
				
				logger.info("Processed " + userActivityScorecards.size() + " Scorecards with " + failureCount + " failures");
			}
			finally
			{
				dataStore.disconnect();
			}
		}
		catch (Throwable t)
		{
			logger.log(Level.WARNING, "Failed scoring user", t);
		}
	}
}