package com.minedme.social.app;

import java.io.IOException;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;

import com.minedme.social.app.data.DataStore;
import com.minedme.social.app.data.VisitorData;
import com.minedme.social.util.MemcacheUtil;

@SuppressWarnings("serial")
public class VisitorServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(VisitorServlet.class.getName());
	private final static String VISITOR_PAGE ="/WEB-INF/jsp/visitor.jsp";

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{		
		String publicId = null;
		try
		{
			publicId = req.getParameter("m");
			 
			if(publicId == null || publicId.trim().length() == 0)
			{
				logger.info("Missing publicId");
				resp.sendRedirect(req.getContextPath() + NavigationConstants.INDEX);
				return;
			}
			
			logger.info("Lookup data from memcache for public id: " + publicId);
			String publicIdDataJson = MemcacheUtil.get(MemcacheUtil.GAME_NAMESPACE, publicId);
			VisitorData publicIdData = null;
			if(publicIdDataJson == null)
			{
				DataStore dataStore = new DataStore();
				try
				{
					logger.info("Connect to data store");
					dataStore.connect();
					
					logger.info("Lookup VisitorData for public id: " + publicId);
					publicIdData = dataStore.lookupVistorData(publicId);
					
					if(publicIdData != null)
					{			
						ObjectMapper mapper = new ObjectMapper();
						logger.info("Turn data into json for public id: " + publicId);
						publicIdDataJson = mapper.writeValueAsString(publicIdData);
						
						logger.info("Write data to memcache for public id: " + publicId);
						MemcacheUtil.put(MemcacheUtil.GAME_NAMESPACE, publicId, Base64.encodeBase64String(publicIdDataJson.getBytes()));
					}
				}
				finally
				{
					dataStore.disconnect();
				}
			}
			else
			{
				logger.info("Turn json back into object for public id: " + publicId);
				ObjectMapper mapper = new ObjectMapper();
				publicIdData = mapper.readValue(Base64.decodeBase64(publicIdDataJson.getBytes()), VisitorData.class);
			}
		
			if(publicIdData != null)
			{
				req.setAttribute("visitorData", publicIdData);
				req.getRequestDispatcher(VISITOR_PAGE).forward(req, resp);
				return;
			}
		}
		catch(Throwable t)
		{
			logger.log(Level.WARNING, "Failed to complete setup for public id: " + publicId, t);
		}
		
		logger.info("Redirecting to main page due to failure processing of public id: " + publicId);
		resp.sendRedirect(req.getContextPath() + NavigationConstants.INDEX);
		return;
	}
}