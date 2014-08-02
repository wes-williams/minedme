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
import com.minedme.social.app.data.MineOverviewData;
import com.minedme.social.util.MemcacheUtil;


@SuppressWarnings("serial")
public class MineOverviewServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(MineOverviewServlet.class.getName());
	private final static String OVERVIEW_PAGE ="/WEB-INF/jsp/mineOverview.jsp";

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{		
		Long mineId = null;
		String publicId = null;
		try
		{
			logger.fine("Validating input parameters");
			mineId = Long.parseLong(req.getParameter("m"));
			publicId = req.getParameter("d");
				
			logger.info("Lookup current data from memcache for publicId " + publicId + " with mine id: " + mineId);
			
			final String memcacheKey = publicId==null?String.valueOf(mineId):(mineId + "__" + publicId ); // also preloaded in UserActivitySnapshotServlet
			String mineOverviewJson = MemcacheUtil.get(MemcacheUtil.MINE_SNAPSHOT_NAMESPACE, memcacheKey);
			MineOverviewData overview = null;
			if(mineOverviewJson == null)
			{
				DataStore dataStore = new DataStore();
				try
				{	
					logger.info("Connect to data store");
					dataStore.connect();
							
					overview = dataStore.lookupMineOverview(mineId, publicId);
					
					if(overview != null)
					{			
						ObjectMapper mapper = new ObjectMapper();
						logger.info("Turn data into json for mine " + mineId + " with public id: " + publicId);
						mineOverviewJson = mapper.writeValueAsString(overview);
						
						logger.info("Write data to memcache for mine " + mineId + " with public id: " + publicId);
						MemcacheUtil.put(MemcacheUtil.MINE_SNAPSHOT_NAMESPACE, memcacheKey, Base64.encodeBase64String(mineOverviewJson.getBytes()));
					}
				}
				finally
				{
					dataStore.disconnect();
				}
			}
			else
			{
				logger.info("Turn json back into object for mine " + mineId + " with public id: " + publicId);
				ObjectMapper mapper = new ObjectMapper();
				overview = mapper.readValue(Base64.decodeBase64(mineOverviewJson.getBytes()), MineOverviewData.class);
			}
		
			if(overview != null)
			{
				req.setAttribute("overview", overview);
				req.getRequestDispatcher(OVERVIEW_PAGE).forward(req, resp);
				return;
			}
		}
		catch(Throwable t)
		{
			logger.log(Level.WARNING, "Failed to complete setup for mine " + mineId + " with public id: " + publicId,t);
		}
		
		logger.info("Redirecting to main page due to failure processing of mine " + mineId + " with public id: " + publicId);
		resp.sendRedirect(req.getContextPath() + NavigationConstants.INDEX);
		return;
	}
}