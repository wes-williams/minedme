package com.minedme.social.foursquare.oauth;

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
import com.minedme.social.foursquare.data.FoursquareDataStore;
import com.minedme.social.foursquare.data.FoursquareUserData;

@SuppressWarnings("serial")
public class FoursquareCallbackServlet extends HttpServlet
{
	private final static Logger logger = Logger
			.getLogger(FoursquareCallbackServlet.class.getName());

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

			String verifierToken = req.getParameter("code");

			if(verifierToken != null && verifierToken.length() > 0)
			{
				FoursquareDataStore dataStore = new FoursquareDataStore();
				try
				{
					dataStore.connect();
					Network network = dataStore.retrieveNetwork();
					
					OAuthService service = dataStore.getService(network);

					logger.info("Trading the Request Token for an Access Token...");
					Verifier verifier = new Verifier(verifierToken);
					Token accessToken = service.getAccessToken(null, verifier);

					OAuthRequest request = new OAuthRequest(Verb.GET, FoursquareDataStore.createUserUrl() + accessToken.getToken());
					service.signRequest(accessToken, request);
					Response response = request.send();

					FoursquareUserData userData = new FoursquareUserData();
					if(response.getCode() == 200)
					{
						String userId = null;
						int activityCount = 0;
						try
						{
							ObjectMapper mapper = new ObjectMapper();
							HashMap data = mapper.readValue(response.getBody(), HashMap.class);
							HashMap user = (HashMap) ((HashMap) data.get("response")).get("user");
							userId = user.get("id").toString();
							activityCount = Integer.parseInt(((HashMap) user.get("checkins")).get("count").toString());
						}
						catch (Exception e)
						{
							logger.info("Failed to find userid or checkins: " + e.getMessage());
						}

						if(userId != null && userId.length() > 0)
						{
							userData.setId(userId);
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
								throw new Exception("Failed to store Foursquare token");
							}

							dataStore.saveUserNetwork(userNetwork);

						}
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
			logger.info("Failed Foursquare Callback: " + t.getMessage());
		}

		resp.sendRedirect(req.getContextPath() + NavigationConstants.HOME);
	}
}
