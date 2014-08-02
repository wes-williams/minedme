package com.minedme.social.foursquare.oauth;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.*;

import org.scribe.oauth.OAuthService;

import com.minedme.social.app.NavigationConstants;
import com.minedme.social.app.data.SessionData;
import com.minedme.social.app.data.entity.Network;
import com.minedme.social.foursquare.data.FoursquareDataStore;


@SuppressWarnings("serial")
public class FoursquareConnectServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(FoursquareConnectServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException 
	{
		try
		{
			if(!SessionData.isValid(req,resp))
			{
				resp.sendRedirect(NavigationConstants.LOGIN);
				return;
			}

			Network network = null;
			OAuthService service = null;
			FoursquareDataStore dataStore = new FoursquareDataStore();
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
			logger.info("Fetching the Authorization URL...");
			String authorizationUrl = service.getAuthorizationUrl(null);

			resp.sendRedirect(resp.encodeRedirectURL(authorizationUrl));
			return;
		}
		catch(Throwable t)
		{
			logger.info("Failed Foursquare Connect: " + t.getMessage());
		}
	}
}
