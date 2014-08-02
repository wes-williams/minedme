package com.minedme.social.twitter.oauth;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.model.Verifier;
import org.scribe.oauth.OAuthService;

import com.minedme.social.app.NavigationConstants;
import com.minedme.social.app.data.SessionData;
import com.minedme.social.app.data.entity.Network;
import com.minedme.social.app.data.entity.User;
import com.minedme.social.app.data.entity.UserNetwork;
import com.minedme.social.twitter.data.TwitterDataStore;
import com.minedme.social.twitter.data.TwitterUserData;

@SuppressWarnings("serial")
public class TwitterCallbackServlet extends HttpServlet
{
	private final static Logger logger = Logger
			.getLogger(TwitterCallbackServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
			ServletException
	{
		try
		{
			SessionData sessionData = new SessionData(req, resp);
			if(!sessionData.isValid())
			{
				resp.sendRedirect(NavigationConstants.LOGIN);
				return;
			}

			//String token = req.getParameter("oauth_token");
			String verifierToken = req.getParameter("oauth_verifier");
			
			if(verifierToken != null && verifierToken.length() > 0)
			{	
				TwitterDataStore dataStore = new TwitterDataStore();
				try
				{
					dataStore.connect();
					Network network = dataStore.retrieveNetwork();
					
					OAuthService service = dataStore.getService(network);

					logger.info("Trading the Request Token for an Access Token...");
					Verifier verifier = new Verifier(verifierToken);
					Token requestToken = sessionData.retrieveRequestToken(network.getName());
					if(requestToken == null)
					{
						logger.info("Failed to retrieve Request Token");
						resp.sendRedirect(req.getContextPath() + NavigationConstants.HOME);
						return;
					}
					
					//logger.info(token + " = " + requestToken.getToken());
					
					Token accessToken = service.getAccessToken(requestToken, verifier);

					logger.info("Making Request");
					OAuthRequest request = new OAuthRequest(Verb.GET, TwitterDataStore.createUserUrl());
					service.signRequest(accessToken, request);
					Response response = request.send();

					TwitterUserData userData = new TwitterUserData();
					if(response.getCode() == 200)
					{
						logger.info("Successfully got access");
						
						String userId = null;
						String screenName = null;
						int activityCount = 0;
						try
						{
							ObjectMapper mapper = new ObjectMapper();
							HashMap data = mapper.readValue(response.getBody(), HashMap.class);
							userId =  data.get("id").toString();
							screenName = data.get("screen_name").toString();
							activityCount = Integer.parseInt(data.get("statuses_count").toString());
						}
						catch (Exception e)
						{
							logger.info("Failed to find id, screen_name, or statuses_count: " + e.getMessage());
						}

						if(userId != null && userId.length() > 0)
						{
							logger.info("Found data");
							
							userData.setId(userId);
							userData.setScreenName(screenName);
							userData.setToken(accessToken.getToken());
							userData.setSecret(accessToken.getSecret());

							User user = dataStore.retrieveUser(sessionData.retrieveUserId());

							UserNetwork userNetwork = new UserNetwork();
							userNetwork.setCreatedDate(System.currentTimeMillis());
							userNetwork.setNetworkId(network.getId());
							userNetwork.setReadEnabled(true);
							userNetwork.setWriteEnabled(false);
							userNetwork.setUserId(user.getId());
							userNetwork.setActivityCount(activityCount);

							if(!dataStore.storeUserData(userData, userNetwork))
							{
								throw new Exception("Failed to store Twitter token");
							}

							dataStore.saveUserNetwork(userNetwork);

						}
					}
					else
					{
						logger.info("Failure retrieving data due to: " + response.getBody());
					}
				}
				finally
				{
					dataStore.disconnect();
				}
			}
		}
		catch (Throwable t)
		{
			logger.info("Failed Twitter Callback: " + t.getMessage());
		}

		resp.sendRedirect(req.getContextPath() + NavigationConstants.HOME);
	}
}
