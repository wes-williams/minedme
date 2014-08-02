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
import com.minedme.social.app.data.SessionData;
import com.minedme.social.app.data.VisitorData;
import com.minedme.social.app.data.entity.GameResult;
import com.minedme.social.util.MemcacheUtil;

@SuppressWarnings("serial")
public class GameServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(GameServlet.class.getName());

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{		
		try
		{
			String publicId = req.getParameter("m");
			String publicIdNetwork = req.getParameter("n");
			String answer = req.getParameter("a");
			
			if(publicId==null || publicId.length() == 0 || 
			   publicIdNetwork == null || publicIdNetwork.length() == 0 || 
			   answer == null || answer.length() == 0)
			{
				logger.info("Missing Parameter - network = " + publicIdNetwork + ", public id " + publicId + ", answer = " + answer  );
				return;
			}
			
			long network = 0;
			char answerChar = 0;
			
			try
			{
				network = Long.parseLong(publicIdNetwork);
				answerChar = answer.charAt(0);
				
				if(network <= 0 || answer.length()>1 || !(answerChar == '=' || answerChar == '<' || answerChar == '>'))
				{
					throw new Exception();
				}
			}
			catch(Exception ex)
			{
				logger.info("Invalid Parameters Network=" + network  + ", answerChar=" + answerChar);
				return;
			}
			
			String networkUsageJson = MemcacheUtil.get(MemcacheUtil.GAME_NAMESPACE, publicId);
			
			if(networkUsageJson == null)
			{
				logger.info("Network not found for network " + publicIdNetwork + " and public id " + publicId );
				return;
			}
			
			ObjectMapper mapper = new ObjectMapper();
			VisitorData visitorData = (VisitorData) mapper.readValue(Base64.decodeBase64(networkUsageJson.getBytes()), VisitorData.class);
			
			DataStore dataStore = new DataStore();
			try
			{
				dataStore.connect();
			
				if(visitorData == null)
				{
					logger.warning("Looking up VisitorData from DB inside Game Storage - should never happen");
					visitorData = dataStore.lookupVistorData(publicId);
					
					if(visitorData != null)
					{			
						logger.info("Turn data into json for public id: " + publicId);
						String publicIdDataJson = mapper.writeValueAsString(visitorData);
						
						logger.info("Write data to memcache for public id: " + publicId);
						MemcacheUtil.put(MemcacheUtil.GAME_NAMESPACE, publicId, Base64.encodeBase64String(publicIdDataJson.getBytes()));
					}
				}
			
				if(visitorData == null)
				{
					logger.info("Could not find VisitorData for Game");
				}
				else
				{	
					GameResult gameResult = null;
					for(VisitorData.NetworkUsage networkUsage : visitorData.getNetworkUsage())
					{
						if(network == networkUsage.getNetworkId())
						{
							gameResult = new GameResult();
							gameResult.setUserId(visitorData.getUserId());
							gameResult.setNetworkId(networkUsage.getNetworkId());
							gameResult.setActivityDate(visitorData.getActivityDate());
							gameResult.setPlayedDate(System.currentTimeMillis());
							gameResult.setTodayResult(networkUsage.getCurrentCount());
							gameResult.setYesterdayResult(networkUsage.getPreviousCount());
							gameResult.setPlayerId(null);
							gameResult.setAnswer(answer);
							
							if((networkUsage.getCurrentCount() == networkUsage.getPreviousCount() && '=' == answerChar ) ||
							   (networkUsage.getCurrentCount() < networkUsage.getPreviousCount()  &&  '<' == answerChar ) ||
							   (networkUsage.getCurrentCount() > networkUsage.getPreviousCount()  &&  '>' == answerChar ))
							{
								gameResult.setCorrect(true);
							}
							else
							{
								gameResult.setCorrect(false);
							}
							
							break;
						}
					}
						
					if(gameResult != null)
					{
						
						if(dataStore.adjustDays(visitorData.getActivityDate(),2) < System.currentTimeMillis())
						{
							logger.info("This game is closed");
						}
						else
						{
							// need to make sure there isn't a bot here - 1000 seems like a reasonable number = 250 x 4 networks
							long gamePlaysLeft = MemcacheUtil.countDown(MemcacheUtil.GAME_NAMESPACE, "gamePlays_" + publicId, 1000);
						
							if(gamePlaysLeft > 0) 
							{
								logger.info("Save GameResult - plays left = " + gamePlaysLeft);
								dataStore.saveGameResult(gameResult);
							}
							else
							{
								logger.warning("Exceeded Daily Game Plays - not saving after " + gamePlaysLeft);
							}
						}
						
						resp.setContentType("application/json; charset=utf-8");
						resp.getWriter().println("{ \"answer\" : " + gameResult.getTodayResult() + " , \"correct\" : " + gameResult.isCorrect() + " }" );
					}
				}
			}
			finally
			{
				dataStore.disconnect();
			}	
		}
		catch(Throwable t)
		{
			logger.log(Level.WARNING,"Failed Game Play",t);
		}
		
	}
}