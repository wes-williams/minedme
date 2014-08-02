package com.minedme.social.twitter.oauth;

import java.io.IOException;
import java.util.logging.Logger;
import java.util.HashMap;

import javax.servlet.http.*;

import org.codehaus.jackson.map.ObjectMapper;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

import com.minedme.social.app.NavigationConstants;
import com.minedme.social.app.data.SessionData;
import com.minedme.social.app.data.entity.Network;
import com.minedme.social.twitter.data.TwitterDataStore;
import com.minedme.social.util.EncryptionUtil;


@SuppressWarnings("serial")
public class TwitterConnectServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(TwitterConnectServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		try
		{
			SessionData sessionData = new SessionData(req,resp);
			
			if(!sessionData.isValid())
			{
				resp.sendRedirect(NavigationConstants.LOGIN);
				return;
			}

			Network network = null;
			OAuthService service = null;
			TwitterDataStore dataStore = new TwitterDataStore();
			try
			{
				dataStore.connect();
				network = dataStore.retrieveNetwork();
				service = dataStore.getService(network);
			}
			finally
			{
				dataStore.disconnect();
			}

			// Obtain the Authorization URL
			logger.info("Getting the Request Token...");
			Token requestToken = service.getRequestToken();
			logger.info("Fetching the Authorization URL...");
			String authorizationUrl = service.getAuthorizationUrl(requestToken);


			sessionData.storeRequestToken(network.getName(),requestToken);
			
			
			resp.sendRedirect(resp.encodeRedirectURL(authorizationUrl));
			return;
		}
		catch(Throwable t)
		{
			logger.info("Failed Twitter Connect: " + t.getMessage());
		}
	}
}
