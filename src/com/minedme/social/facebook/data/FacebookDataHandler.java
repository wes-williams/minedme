package com.minedme.social.facebook.data;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

import com.minedme.social.app.data.NetworkDataHandler;
import com.minedme.social.app.data.entity.Network;
import com.minedme.social.app.data.entity.UserActivityScorecard;
import com.minedme.social.app.data.entity.UserNetwork;
import com.minedme.social.util.EncoderUtil;

public class FacebookDataHandler implements NetworkDataHandler
{
	private final static Logger logger = Logger.getLogger(FacebookDataHandler.class.getName());
	private final static String USER_POST_URL = "https://graph.facebook.com/me/feed";
	private final static String DEFAULT_TIMEZONE = "America/Denver";
	
	private FacebookDataStore dataStore;

	FacebookDataHandler(FacebookDataStore dataStore)
	{
		this.dataStore = dataStore;
	}

	@Override
	public void sendNotification(UserNetwork userNetwork, UserActivityScorecard activityScorecard,
			int outsideNetworkCount, int outsideNetworkPercent) throws Exception
	{	
		FacebookUserData userData = dataStore.retrieveUserData(userNetwork);

		Token accessToken = new Token(userData.getToken(), userData.getSecret());

		if(accessToken != null)
		{
			Network network = dataStore.retrieveNetwork();
			OAuthService service = dataStore.getService(network);

			SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("M/dd");
			dateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
			
			
			logger.info("Updating the resource...");
			OAuthRequest request = new OAuthRequest(Verb.POST, USER_POST_URL);
			request.addBodyParameter("picture","http://www.minedme.com/media/minedme-small-logo.jpg");
			request.addBodyParameter("link", "http://www.minedme.com/prospect?m=" + activityScorecard.getPublicId());
			request.addBodyParameter("name", "Mining Report for " + dateFormat.format(new Date(activityScorecard.getActivityDate())));
			request.addBodyParameter("caption", activityScorecard.getNetworkCount() + " New Nuggets! (" + outsideNetworkPercent + "% Exotic)");
			request.addBodyParameter("description","Discover network usage trends by viewing the charts. Stay aware by playing the game!");
			service.signRequest(accessToken, request);

			Response response = null;

			int retryCount = 0;
			while (retryCount < 3) // try three times
			{
				retryCount++;
				try
				{
					//logger.info(request.getUrl());
					response = request.send();
					break;
				}
				catch (org.scribe.exceptions.OAuthException ex)
				{
					logger.info("Failed connecting to Facebook: " + ex.getMessage());
					Thread.sleep(1000 * retryCount); // wait a little longer each time
				}
			}

			if(response == null)
			{
				throw new Exception("Failed 3 attempts of connecting to Facebook");
			}

			if(response.getCode() == 200)
			{
				logger.info("Successfully Posted on Wall of User " + activityScorecard.getUserId() + " on " + activityScorecard.getActivityDate());
			}
			else
				throw new Exception("Response of " + response.getCode() + " indicates notification failure");

		}
	}
}
