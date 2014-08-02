package com.minedme.social.facebook.oauth;

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
import com.minedme.social.facebook.data.FacebookDataStore;
import com.minedme.social.util.EncryptionUtil;


@SuppressWarnings("serial")
public class FacebookConnectServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(FacebookConnectServlet.class.getName());

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
			FacebookDataStore dataStore = new FacebookDataStore();
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
			//logger.info("Getting the Request Token...");
			//Token requestToken = service.getRequestToken();
			logger.info("Fetching the Authorization URL...");
			String authorizationUrl = service.getAuthorizationUrl(null);

			//sessionData.storeRequestToken(network.getName(),requestToken);
			
			authorizationUrl += "&scope=read_stream,publish_stream,offline_access";
			
			resp.sendRedirect(resp.encodeRedirectURL(authorizationUrl));
			return;
		}
		catch(Throwable t)
		{
			logger.info("Failed Facebook Connect: " + t.getMessage());
		}
	}
}
