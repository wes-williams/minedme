package com.minedme.social.app.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.minedme.social.app.NavigationConstants;
import com.minedme.social.app.data.VisitorData.NetworkUsage;
import com.minedme.social.app.data.entity.ActivityDate;
import com.minedme.social.app.data.entity.GameResult;
import com.minedme.social.app.data.entity.Network;
import com.minedme.social.app.data.entity.NetworkActivity;
import com.minedme.social.app.data.entity.User;
import com.minedme.social.app.data.entity.UserActivitySnapshot;
import com.minedme.social.app.data.entity.UserActivityScorecard;
import com.minedme.social.app.data.entity.UserNetwork;
import com.minedme.social.app.data.entity.UserNetworkActivity;
import com.minedme.social.app.data.entity.UserScorecard;
import com.minedme.social.app.data.oauth.UserData;
import com.minedme.social.facebook.data.FacebookDataStore;
import com.minedme.social.facebook.data.FacebookUserData;
import com.minedme.social.foursquare.data.FoursquareDataStore;
import com.minedme.social.linkedin.data.LinkedInDataStore;
import com.minedme.social.twitter.data.TwitterDataStore;
import com.minedme.social.util.EncryptionUtil;
import com.minedme.social.util.NetworkUtil;
import com.minedme.social.util.ScoreUtil;
import com.minedme.social.util.VisitorUtil;

// SEARCH FOR TODO
public class DataStore 
{
	private final static String DEFAULT_TIMEZONE = "America/Denver";
	
	private final static Logger logger = Logger.getLogger(DataStore.class.getName());
	private static final PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("transactions-optional");
	
	protected PersistenceManager pm;
	
	public void connect()
	{
		if(pm == null)
		{
			pm = pmf.getPersistenceManager();
		}
	}
	
	public void disconnect()
	{
		if(pm != null)
		{
			try
			{
				if(!pm.isClosed())
				{
					pm.close();
				}
			}
			catch(Throwable t)
			{
				logger.info("Failed disconnecting from Persistence Manager: " + t.getMessage());
			}
		}
	}
	
	private DataStore connectToNetworkStore(Network network)
	{
		DataStore dataStore = null;
		if(FacebookDataStore.isForNetwork(network))
		{
			dataStore = new FacebookDataStore();
		}
		else if(FoursquareDataStore.isForNetwork(network))
		{
			dataStore = new FoursquareDataStore();
		}
		else if(LinkedInDataStore.isForNetwork(network))
		{
			dataStore = new LinkedInDataStore();
		}
		else if (TwitterDataStore.isForNetwork(network))
		{
			dataStore = new TwitterDataStore();
		}
		
		if(dataStore != null)
		{
			dataStore.pm = this.pm;
		}
		
		return dataStore;
	}

	public NetworkDataHandler getNetworkHandler(Network network)
	{
		DataStore dataStore = connectToNetworkStore(network);
		
		if(dataStore != null)
		{
			return dataStore.getNetworkHandler();
		}
		
		return null;
	}
	
	protected NetworkDataHandler getNetworkHandler()
	{
		throw new RuntimeException("Network DataStore should provide implementation");
	}
	

	
	public ActivityDate retrieveActivityDate(Long activityDateId)
	{
		Key k = KeyFactory.createKey(ActivityDate.class.getSimpleName(), activityDateId);
        ActivityDate v = pm.getObjectById(ActivityDate.class, k);
        return v;
	}
	
	public GameResult retrieveGameResults(Long gameResultsId)
	{
		Key k = KeyFactory.createKey(GameResult.class.getSimpleName(), gameResultsId);
        GameResult v = pm.getObjectById(GameResult.class, k);
        return v;
	}
	
	public Network retrieveNetwork(Long networkId)
	{
		Key k = KeyFactory.createKey(Network.class.getSimpleName(), networkId);
        Network v = pm.getObjectById(Network.class, k);
        return v;
	}
	
	public Network findNetworkByName(String name)
	{
		Query query = pm.newQuery(Network.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("name == nameParam");
	    query.declareParameters("String nameParam");	
	    List results = (List) query.execute(name);
	 
	    if(results.size() > 1)
	    {
	    	throw new RuntimeException("More than one network with name: " + name);
	    }
	    else if(results.size()==0)
	    {
	    	return null;
	    }
	    
	    return (Network) results.get(0); 
	}
	
	public void saveNetwork(Network network)
	{
		pm.makePersistent(network);
	}
	
	public NetworkActivity retrieveNetworkActivity(Long networkActivityId)
	{
		Key k = KeyFactory.createKey(NetworkActivity.class.getSimpleName(), networkActivityId);
        NetworkActivity v = pm.getObjectById(NetworkActivity.class, k);
        return v;
	}
	
	public User retrieveUser(Long userId)
	{
		Key k = KeyFactory.createKey(User.class.getSimpleName(), userId);
        User v = pm.getObjectById(User.class, k);
        return v;
	}
	
	public User findUserByUsername(String username)
	{
		Query query = pm.newQuery(User.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("username == usernameParam");
	    query.declareParameters("String usernameParam");	
	    List results = (List) query.execute(username);
	 
	    if(results.size() > 1)
	    {
	    	throw new RuntimeException("More than one user with name: " + username);
	    }
	    else if(results.size()==0)
	    {
	    	return null;
	    }
	    
	    return (User) results.get(0); 
	}
	
	public int countUsersWithUsername(String username)
	{
		Query query = pm.newQuery("select key from " + User.class.getName());
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("username == usernameParam");
	    query.declareParameters("String usernameParam");	
	    List results = (List) query.execute(username);
	 
	    return results.size();
	}
	 
	public boolean isUsernameAvailable(String username)
	{
	    return countUsersWithUsername(username)==0;    
	}
	
	public void saveUser(User user) throws Exception
	{
		Transaction tr = pm.currentTransaction(); 
		try
		{
			tr.begin();
			
			if(isUsernameAvailable(user.getUsername()))
			{
				pm.makePersistent(user);
				
				int matchingUsers = countUsersWithUsername(user.getUsername());
				if(matchingUsers==0)
				{
					tr.commit();
				}
				else
				{
					throw new Exception("User already exist");
				}
			}
			else
			{
				throw new Exception("User already exist");
			}
		}
		finally
		{
			if(tr.isActive())
			{
				tr.rollback();
			}
		}
	}

	
	public void scoreUsers(ActivityDate activityDate)
	{
		throw new RuntimeException("scoreUsers(ActivityDate activityDate)");
		
//		Transaction tr = pm.currentTransaction(); 
//		try
//		{
//			logger.info("Starting Transaction for User Score");
//			tr.begin();
//			
//			
//			// loop users here
//			logger.info("Committing Score changes");
//			tr.commit();
//			logger.info("Successfully Committed Score Changes");
//		}
//		finally
//		{
//			if(tr.isActive())
//			{
//				tr.rollback();
//			}
//		}
	}
	
	public void scoreUser(UserActivityScorecard activityScorecard)
	{		
		UserScorecard userScorecard = findScorecardForUser(activityScorecard.getUserId());
		if(userScorecard==null)
		{
			userScorecard = new UserScorecard();
			userScorecard.setUserId(activityScorecard.getUserId());
		}
		
		userScorecard.setGameCount(activityScorecard.getGameCount() + userScorecard.getGameCount());
		userScorecard.setGameScore(activityScorecard.getGameScore() + userScorecard.getGameScore());
		userScorecard.setNetworkCount(activityScorecard.getNetworkCount() + userScorecard.getNetworkCount());
		userScorecard.setNetworkScore(activityScorecard.getNetworkScore() + userScorecard.getNetworkScore());
		userScorecard.setVisitorCount(activityScorecard.getVisitorCount() + userScorecard.getVisitorCount());
		userScorecard.setVisitorScore(activityScorecard.getVisitorScore() + userScorecard.getVisitorScore());
		userScorecard.setTotalCount(activityScorecard.getTotalCount() + userScorecard.getTotalCount());
		userScorecard.setTotalScore(activityScorecard.getTotalScore() + userScorecard.getTotalScore());
		
		saveUserScorecard(userScorecard);

	}
	
	
	public void scoreUserActivity(ActivityDate activityDate)
	{
		throw new RuntimeException("Have not implemented scoreUserActivity(ActivityDate activityDate)");

//		Transaction tr = pm.currentTransaction(); 
//		try
//		{
//			logger.info("Starting Transaction for User Activity Score");
//			tr.begin();
//
//			// user loop goes here
//			
//			logger.info("Committing Score changes");
//			tr.commit();
//			logger.info("Successfully Committed User Activity Score Changes");
//			
//		}
//		finally
//		{
//			if(tr.isActive())
//			{
//				tr.rollback();
//			}
//		}
	}
	
	public void scoreUserActivity(User user, ActivityDate activityDate)
	{
		UserActivityScorecard activityScorecard = new UserActivityScorecard();
		boolean anyPublicNetworks = false;
		String publicUserActivityId = null;
		TreeMap<String,List<UserNetworkActivity>> lastSevenNetworkActivities = new TreeMap<String,List<UserNetworkActivity>>(); 
		long weekBefore = weekBefore(activityDate.getUtcTime());
		List<UserNetwork> userNetworks = findReadableUserNetworks(user.getId());
		for(UserNetwork userNetwork : userNetworks)
		{			
			int networkScoreCount = 0;
			int gameScoreCount = 0;
			int visitorScoreCount = 0;
			
			logger.info("Processing Network: " + userNetwork.getNetworkId());
			
			if(!anyPublicNetworks && userNetwork.isPublicEnabled())
			{
				anyPublicNetworks = true;
			}
			
			List<UserNetworkActivity> userNetworkActivities = findUserActivity(user.getId(), userNetwork.getNetworkId(), activityDate.getUtcTime());
			if(userNetworkActivities != null)
			{
				for(UserNetworkActivity userNetworkActivity : userNetworkActivities)
				{
					logger.info("Processing NetworkActivity: " + userNetworkActivity.getId());
					
					if(publicUserActivityId == null && userNetwork.isPublicEnabled())
					{
						publicUserActivityId = VisitorUtil.createPublicIdentifier(userNetworkActivity.getId());
					}
					
					networkScoreCount += userNetworkActivity.getCount();
				}
			}
			else
			{
				logger.info("No Network Activity for User : " + user.getId() + " in Network = " + userNetwork.getNetworkId() + " when ActivityDate =" + activityDate.getUtcTime());
			}
			

			logger.info("Collecting Game Results for User = " + user.getId() + ", Network = " + userNetwork.getNetworkId() + ", ActivityDate = " + activityDate.getUtcTime());
			List<GameResult> gameResults = findGameResults(user.getId(), userNetwork.getNetworkId(), dayBefore(activityDate.getUtcTime()));
			
			if(gameResults != null)
			{
			    logger.info("Found GameResult count: " + gameResults.size());
			    
				visitorScoreCount += gameResults.size();
				for(GameResult gameResult : gameResults)
				{
					logger.info("Processing GameResult: " + gameResult.getId());
					if(gameResult.isCorrect())
					{
						logger.info("Scored Points for GameResult: " + gameResult.getId());
						if(gameResult.getTodayResult()>0)
						{
							gameScoreCount += gameResult.getTodayResult();
						}
						else
						{
							gameScoreCount++;
						}
					}
				}
			}
			else
			{
				logger.info("No Game Resuls for User : " + user.getId() + " in Network = " + userNetwork.getNetworkId() + " when ActivityDate =" + activityDate.getUtcTime());
			}
			
			short totalCount = (short) (gameScoreCount + networkScoreCount + visitorScoreCount);
			
			activityScorecard.setGameCount((short) (activityScorecard.getGameCount() + gameScoreCount));
			activityScorecard.setNetworkCount((short)(activityScorecard.getNetworkCount() + networkScoreCount));
			activityScorecard.setVisitorCount((short)(activityScorecard.getVisitorCount() + visitorScoreCount));
			activityScorecard.setTotalCount((short) (activityScorecard.getTotalCount() + totalCount));
			
			Network network = retrieveNetwork(userNetwork.getNetworkId());
			lastSevenNetworkActivities.put(network.getName(), this.findUserNetworkActivityBetweenDates(userNetwork.getUserId(), userNetwork.getNetworkId(), weekBefore,activityDate.getUtcTime()));
		}
		
		byte networkScore = 0;
		byte gameScore = 0;
		byte visitorScore = 0;
		
		logger.info("Calculating Scores");

		if(activityScorecard.getGameCount() != 0)
		{
			gameScore = ScoreUtil.score(activityScorecard.getGameCount());
			activityScorecard.setGameScore(gameScore);
		}

		if(activityScorecard.getNetworkCount() != 0)
		{
			networkScore = ScoreUtil.score(activityScorecard.getNetworkCount());
			activityScorecard.setNetworkScore(networkScore);
		}

		if(activityScorecard.getVisitorCount() != 0)
		{
			visitorScore = ScoreUtil.score(activityScorecard.getVisitorCount());
			activityScorecard.setVisitorScore(visitorScore);
		}
		
		activityScorecard.setActivityDate(activityDate.getUtcTime());
		
		short totalScore = (short) (gameScore + networkScore + visitorScore);
		activityScorecard.setTotalScore(totalScore);
		
		if(anyPublicNetworks)
		{
			activityScorecard.setPublicId(publicUserActivityId); // TODO
			
			if(user.isPrivacyEnabled())
			{
				activityScorecard.setPasscode(""); // TODO
			}
		}
		
		
		activityScorecard.setUserId(user.getId());
		
		logger.info("Building JS Data");
		
		// start to js array
		String jsColumns = "['Day'";
		String[] jsData = new String[7];
		String jsTotalData = "['Network','Activity']";
		
		// build date element of data
		SimpleDateFormat jsDateFormat = new SimpleDateFormat("M/dd");
		jsDateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		for(int i=0;i<jsData.length;i++)
		{
			jsData[i] = "['" + jsDateFormat.format(new Date(adjustDays(weekBefore,i))) + "'";
		}
				
		// fill in data points of data
		for(String networkName : lastSevenNetworkActivities.keySet())
		{
			logger.info("Building JS data for Network: " + networkName);
			// build the list of networks
			jsColumns += ",'" + NetworkUtil.providePresentableName(networkName) +"'";
			
			int lastWeekTotal = 0;
			// fill in the data points for each day of week 
			List<UserNetworkActivity> userNetworkActivities = lastSevenNetworkActivities.get(networkName);
			int matchCount=0;
			for(int i=0; i < jsData.length; i++)
			{					
				if(matchCount < userNetworkActivities.size())
				{
					UserNetworkActivity userNetworkActivity = userNetworkActivities.get(matchCount);
					
					//logger.info(userNetworkActivity.getActivityDate() + " == " + adjustDays(weekBefore,i) + " ?");
					
					if(userNetworkActivity.getActivityDate() == adjustDays(weekBefore,i))
					{
						logger.info("Building JS data with scorecard for User " + userNetworkActivity.getUserId() + " from ActivityDate " + userNetworkActivity.getActivityDate());					
						jsData[i] += "," + userNetworkActivity.getCount();
						lastWeekTotal += userNetworkActivity.getCount();
						matchCount++;
					}
					else
					{
						logger.info("Placing filling 0 to accomodate missing data for User " + userNetworkActivity.getUserId() + " from ActivityDate " + userNetworkActivity.getActivityDate());
						jsData[i] += ",0";
					}
				}
				else
				{
					logger.info("Placing filling 0 to accomodate missing data for User " + activityScorecard.getUserId());
					jsData[i] += ",0";
				}
				
			}

			jsTotalData += ",['" + NetworkUtil.providePresentableName(networkName) +  "'," + lastWeekTotal + "]";			
		}
		jsColumns += "]";
		
		String lastWeekChartData = jsColumns;
		for(int i=0;i<jsData.length;i++)
		{	
				jsData[i] += "]";
				lastWeekChartData += "," + jsData[i];
		}
		activityScorecard.setLastWeekChartData(lastWeekChartData);
		activityScorecard.setLastWeekTotalsChartData(jsTotalData);
		// end to js array
		
		logger.info("Saving User Activity Scorecard");
		saveUserActivityScorecard(activityScorecard);

	}
	
	public UserActivityScorecard retrieveUserActivityScorecard(Long userActivityScorecard)
	{
		Key k = KeyFactory.createKey(UserActivityScorecard.class.getSimpleName(), userActivityScorecard);
        UserActivityScorecard v = pm.getObjectById(UserActivityScorecard.class, k);
        return v;
	}
	
	public UserNetwork retrieveUserNetwork(Long userNetworkId)
	{
		Key k = KeyFactory.createKey(UserNetwork.class.getSimpleName(), userNetworkId);
        UserNetwork v = pm.getObjectById(UserNetwork.class, k);
        return v;
	}
	
	public List<UserNetwork> findUserNetworks(Long networkId)
	{
		Query query = pm.newQuery(UserNetwork.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("networkId == networkIdParam");
	    query.declareParameters("Long networkIdParam");	
	    List results = (List) query.executeWithArray(networkId);
	    
	    return results; 
	}
	
	public List<UserNetwork> findReadableUserNetworksForNetwork(Long networkId)
	{
		Query query = pm.newQuery(UserNetwork.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("networkId == networkIdParam && readEnabled == true");
	    query.declareParameters("Long networkIdParam");	
	    List results = (List) query.executeWithArray(networkId);
	    
	    return results; 
	}
	
	public List<Long> findUsersWithPossibleActivity()
	{
		Query query = pm.newQuery(UserNetwork.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("readEnabled == true");
	    List<UserNetwork> results = (List<UserNetwork>) query.execute();
	    
	    HashSet<Long> userIds = new HashSet<Long>();
	    for(UserNetwork userNetwork : results)
	    {
	    	userIds.add(userNetwork.getUserId());
	    }
	    
	    return new ArrayList(userIds);
	}
	
	public List<UserNetwork> findUsersForPossibleNotification()
	{
		Query query = pm.newQuery(UserNetwork.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("writeEnabled == true");
	    List<UserNetwork> results = (List<UserNetwork>) query.execute();
	    
	    return results;
	}
	
	public UserNetwork findUserNetwork(Long userId, Long networkId)
	{
		Query query = pm.newQuery(UserNetwork.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("userId == userIdParam && networkId == networkIdParam");
	    query.declareParameters("Long userIdParam, Long networkIdParam");	
	    List results = (List) query.executeWithArray(userId, networkId);
	 
	    if(results.size() > 1)
	    {
	    	throw new RuntimeException("More than one user network for user #" + userId + " and network #" + networkId);
	    }
	    else if(results.size()==0)
	    {
	    	return null;
	    }
	    
	    return (UserNetwork) results.get(0); 
	}
	
	public List<UserNetwork> findReadableUserNetworks(Long userId)
	{
		Query query = pm.newQuery(UserNetwork.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("userId == userIdParam && readEnabled == true");
	    query.declareParameters("Long userIdParam");	
	    List<UserNetwork> results = (List<UserNetwork>) query.executeWithArray(userId);
	    
	    return results;
	}
	
	public void saveUserNetwork(UserNetwork userNetwork)
	{
		pm.makePersistent(userNetwork);
	}

	
	public void saveUserNetworkActivity(UserNetworkActivity userNetworkActivity)
	{
		pm.makePersistent(userNetworkActivity);
	}
	
	public UserNetworkActivity retrieveUserNetworkActivity(Long userNetworkActivityId)
	{
		Key k = KeyFactory.createKey(UserNetworkActivity.class.getSimpleName(), userNetworkActivityId);
        UserNetworkActivity v = pm.getObjectById(UserNetworkActivity.class, k);
        return v;
	}

	public List<UserNetworkActivity> findUserActivity(Long userId, Long networkId, Long activityDate)
	{
		Query query = pm.newQuery(UserNetworkActivity.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("userId == userIdParam && networkId == networkIdParam && activityDate == activityDateParam");
	    query.declareParameters("Long userIdParam, Long networkIdParam, Long activityDateParam");	
	    List<UserNetworkActivity> results = (List<UserNetworkActivity>) query.executeWithArray(userId, networkId, activityDate);
	    
	    return results;
	}
	
	public List<UserNetworkActivity> findUserNetworkActivityBetweenDates(Long userId, Long networkId, Long startActivityDate, Long endActivityDate)
	{
		Query query = pm.newQuery(UserNetworkActivity.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("userId == userIdParam && networkId == networkIdParam && activityDate >= startActivityDateParam && activityDate < endActivityDateParam");
	    query.setOrdering("activityDate asc");
	    query.declareParameters("Long userIdParam, Long networkIdParam, Long startActivityDateParam, Long endActivityDateParam");	
	    List<UserNetworkActivity> results = (List<UserNetworkActivity>) query.executeWithArray(userId, networkId, startActivityDate, endActivityDate);
	    
	    return results;
	}
	
	public UserNetworkActivity findUserNetworkActivity(Long userId, Long networkId, Long activityDate)
	{
		Query query = pm.newQuery(UserNetworkActivity.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("userId == userIdParam && networkId == networkIdParam && activityDate == activityDateParam");
	    query.declareParameters("Long userIdParam, Long networkIdParam, Long activityDateParam");	
	    List<UserNetworkActivity> results = (List<UserNetworkActivity>) query.executeWithArray(userId, networkId, activityDate);
	    
	    if(results.size() > 1)
	    {
	    	throw new RuntimeException("More than activity on Date " + activityDate + " in network " + networkId + " for user #" + userId );
	    }
	    else if(results.size()==0)
	    {
	    	return null;
	    }
	    
	    return results.get(0);
	}
	
	public List<UserNetworkActivity> findUserNetworkActivity(Long userId, Long activityDate)
	{
		Query query = pm.newQuery(UserNetworkActivity.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("userId == userIdParam && activityDate == activityDateParam");
	    query.declareParameters("Long userIdParam, Long activityDateParam");	
	    List<UserNetworkActivity> results = (List<UserNetworkActivity>) query.executeWithArray(userId, activityDate);
	    
	    return results;
	}
	
	public List<GameResult> findGameResults(Long userId, Long networkId, Long activityDate)
	{
		Query query = pm.newQuery(GameResult.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("userId == userIdParam && networkId == networkIdParam && activityDate == activityDateParam");
	    query.declareParameters("Long userIdParam, Long networkIdParam, Long activityDateParam");	
	    List<GameResult> results = (List<GameResult>) query.executeWithArray(userId, networkId, activityDate);
	    
	    return results;
	}

	public UserActivitySnapshot findUserActivitySnapshot(Long userId, Long snapshotDate)
	{
		Query query = pm.newQuery(UserActivitySnapshot.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("userId == userIdParam && snapshotDate == snapshotDateParam ");
	    query.declareParameters("Long userIdParam, Long snapshotDateParam");	
	    List results = (List) query.execute(userId,snapshotDate);
	 
	    if(results.size() > 1)
	    {
	    	throw new RuntimeException("More than one user activity snapshot for user #" + userId + " on " + snapshotDate);
	    }
	    else if(results.size()==0)
	    {
	    	return null;
	    }
	    
	    return (UserActivitySnapshot) results.get(0); 
	}
	
	public void saveUserActivitySnapshot(UserActivitySnapshot snapshot)
	{
		pm.makePersistent(snapshot);
	}
	
	public UserScorecard findScorecardForUser(Long userId)
	{
		Query query = pm.newQuery(UserScorecard.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("userId == userIdParam ");
	    query.declareParameters("Long userIdParam");	
	    List results = (List) query.execute(userId);
	 
	    if(results.size() > 1)
	    {
	    	throw new RuntimeException("More than one user scorecard for user #" + userId );
	    }
	    else if(results.size()==0)
	    {
	    	return null;
	    }
	    
	    return (UserScorecard) results.get(0); 
	}
	
	public UserActivityScorecard findUserActivityScorecard(Long userId, Long activityDate)
	{
		Query query = pm.newQuery(UserActivityScorecard.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("userId == userIdParam && activityDate == activityDateParam");
	    query.declareParameters("Long userIdParam, Long activityDateParam");	
	    List results = (List) query.execute(userId, activityDate);
	 
	    if(results.size() > 1)
	    {
	    	throw new RuntimeException("More than one user scorecard for user #" + userId + " on " + activityDate  );
	    }
	    else if(results.size()==0)
	    {
	    	return null;
	    }
	    
	    return (UserActivityScorecard) results.get(0); 
	}
	
	public UserActivityScorecard findUserActivityScorecardWithPublicId(String publicId)
	{
		Query query = pm.newQuery(UserActivityScorecard.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("publicId == publicIdParam");
	    query.declareParameters("String publicIdParam");	
	    List results = (List) query.execute(publicId);
	 
	    if(results.size() > 1)
	    {
	    	throw new RuntimeException("More than one user scorecard with public id " + publicId  );
	    }
	    else if(results.size()==0)
	    {
	    	return null;
	    }
	    
	    return (UserActivityScorecard) results.get(0); 
	}
	
	
	public List<UserActivityScorecard> findUserActivityScorecardsForDate(Long activityDate)
	{
		Query query = pm.newQuery(UserActivityScorecard.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("activityDate == activityDateParam");
	    query.declareParameters("Long activityDateParam");	
	    List<UserActivityScorecard> results = (List<UserActivityScorecard>) query.execute(activityDate);
	    
	    return results;
	}
	
	private final static  String[] months = {"January", "February",
		  "March", "April", "May", "June", "July",
		  "August", "September", "October", "November",
		  "December"};
	
	private Calendar todayCalendar()
	{
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		String todayFormatted = dateFormat.format(cal.getTime());
		
		try
		{
			cal.setTimeInMillis(dateFormat.parse(todayFormatted).getTime());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		return cal;
	}
	
	private ActivityDate today;
	public ActivityDate today()
	{
		if(today == null)
		{
			Calendar cal = todayCalendar();
			today = getActivityDate(cal);
		}
		return today;
	}
	
	private ActivityDate yesterday;
	public ActivityDate yesterday()
	{
		if(yesterday == null)
		{
			Calendar cal = todayCalendar();
			cal.add(Calendar.DAY_OF_YEAR,-1);
			yesterday = getActivityDate(cal);
		}
		return yesterday;
	}
	
	private String formatDateStandard(long date)
	{
		SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMMMM dd, yyyy");
		dateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		return dateFormat.format(new java.util.Date(date));
	}
	
	public long dayBefore(long timeInMillis)
	{
		return adjustDays(timeInMillis,-1);
	}
	
	public long dayAfter(long timeInMillis)
	{
		return adjustDays(timeInMillis,1);
	}
	
	public long weekBefore(long timeInMillis)
	{
		return adjustDays(timeInMillis,-7);
	}
	
	public long adjustDays(long timeInMillis,int dayAdjustment)
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		cal.setTimeInMillis(timeInMillis);
		cal.add(Calendar.DAY_OF_YEAR,dayAdjustment);
		
		return cal.getTimeInMillis();
	}
	
	public long startOfYear(long timeInMillis)
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		cal.setTimeInMillis(timeInMillis);
		cal.add(Calendar.DAY_OF_YEAR, -1 * (cal.get(Calendar.DAY_OF_YEAR)-1));
		
		return cal.getTimeInMillis();
	}

	
	private ActivityDate getActivityDate(Calendar cal)
	{		
		Query query = pm.newQuery(ActivityDate.class);
		query.addExtension("datanucleus.appengine.datastoreReadConsistency", "STRONG");
	    query.setFilter("utcTime == utcTimeParam ");
	    query.declareParameters("Long utcTimeParam");	
	    List results = (List) query.execute(Long.valueOf(cal.getTimeInMillis()));
	    
	    if(results.size()==1)
	    {
	    	return (ActivityDate) results.get(0);
	    }
	    
	    if(results.size() > 1)
	    {
	    	throw new RuntimeException("More than one ActivityDate for date");
	    }
	    
	    ActivityDate activityDate = new ActivityDate();
	    activityDate.setDayNumber(Byte.valueOf((byte)cal.get(Calendar.DAY_OF_MONTH)));
	    activityDate.setDayOfWeekNumber(Byte.valueOf((byte)cal.get(Calendar.DAY_OF_WEEK)));
	    activityDate.setMonthName(months[cal.get(Calendar.MONTH)]);
	    activityDate.setMonthNumber(Byte.valueOf((byte)(cal.get(Calendar.MONTH)+1)));
	    activityDate.setUtcTime(cal.getTimeInMillis());
	    activityDate.setWeekOfYearNumber((byte)cal.get(Calendar.WEEK_OF_YEAR));
	    activityDate.setYearNumber((short)cal.get(Calendar.YEAR));
	    pm.makePersistent(activityDate);
		
		return activityDate;
	}
	
	public void saveUserScorecard(UserScorecard userScorecard)
	{
		pm.makePersistent(userScorecard);
	}
	
	public void saveUserActivityScorecard(UserActivityScorecard userActivityScorecard)
	{
		pm.makePersistent(userActivityScorecard);
	}
	
	public void saveGameResult(GameResult gameResult)
	{
		pm.makePersistent(gameResult);
	}
	
	public MineOverviewData lookupMineOverview(Long userId, String publicId)
	{
		UserActivitySnapshot snapshot = null;
		Long snapshotDate = null;
		if(publicId != null)
		{
			logger.info("Lookup snapshot based for user " + userId + " on scorecard with public id: " + publicId);
			UserActivityScorecard userActivityScorecard = findUserActivityScorecardWithPublicId(publicId);
			
			snapshotDate = userActivityScorecard.getActivityDate();
			snapshot = findUserActivitySnapshot(userId, snapshotDate);
		}
		else
		{
			logger.info("Lookup snapshot for current date and user: " + userId );
			snapshotDate = dayBefore(todayCalendar().getTimeInMillis());
			snapshot = findUserActivitySnapshot(userId, snapshotDate);
			
			if(snapshot==null) // could be after midnight, but before the next load...
			{
				snapshotDate = dayBefore(snapshotDate);
				snapshot = findUserActivitySnapshot(userId, snapshotDate);
			}
		}
	
		return lookupMineOverview(userId, publicId, snapshotDate, snapshot); 
	}
		
		
	public MineOverviewData lookupMineOverview(Long userId, String publicId, Long snapshotDate, UserActivitySnapshot snapshot)
	{
		
		if(snapshot != null)
		{
			List<UserNetworkActivity> previousNetworkActivities = findUserNetworkActivity(snapshot.getUserId(),snapshot.getSnapshotDate());
			
			List<MineOverviewData.NetworkDetail> networkDetails = new ArrayList<MineOverviewData.NetworkDetail>();
			for(UserNetworkActivity previousNetworkActivity : previousNetworkActivities)
			{
				MineOverviewData.NetworkDetail network = new MineOverviewData.NetworkDetail();
				
				network.setNetworkId(previousNetworkActivity.getNetworkId());
				
				if(previousNetworkActivity.getNetworkUrl() != null)
				{
					try
					{
						network.setNetworkUrl(EncryptionUtil.decrypt(previousNetworkActivity.getNetworkUrl()));
					}
					catch(Exception e)
					{
						throw new RuntimeException("Failed to decrypt network url", e);
					}
				}
				
				network.setNetworkName(NetworkUtil.identifyNetworkFromLink(network.getNetworkUrl()));
				
				networkDetails.add(network);
			}
			
			Collections.sort(networkDetails, new Comparator<MineOverviewData.NetworkDetail>()
					{
						@Override
						public int compare(MineOverviewData.NetworkDetail o1, MineOverviewData.NetworkDetail o2)
						{
							if(o1==null && o2 ==null) return 0;
							if(o1.getNetworkName()==null && o2.getNetworkName()==null) return 0;
							
							if(o1.getNetworkName()!=null)
							{	
								return o1.getNetworkName().compareToIgnoreCase(o2.getNetworkName());
							}
							
							return -1;
						}
				
					});

			MineOverviewData overview = new MineOverviewData();
			overview.setFormattedDate(formatDateStandard(snapshotDate));
			overview.setDayData(snapshot.getDayData());
			overview.setDayTotalsData(snapshot.getDayTotalsData());
			overview.setWeekData(snapshot.getWeekData());
			overview.setWeekTotalsData(snapshot.getWeekTotalsData());
			overview.setMonthData(snapshot.getMonthData());
			overview.setMonthTotalsData(snapshot.getMonthTotalsData());
			overview.setYearData(snapshot.getYearData());
			overview.setYearTotalsData(snapshot.getYearTotalsData());
			overview.setProspectorCount(snapshot.getVisitorCount());
			overview.setNuggetCount(snapshot.getNetworkCount());
			overview.setAppraisalValue(snapshot.getTotalScore());
			overview.setPublicId(snapshot.getUserActivityScorecardPublicId());
			overview.setNetworkDetails(networkDetails.toArray(new MineOverviewData.NetworkDetail[networkDetails.size()]));
			
			return overview;
		}
		
		return null;
	}
	
	public VisitorData lookupVistorData(String publicId)
	{
		logger.info("Lookup UserActivityScorecard for public id: " + publicId);
		UserActivityScorecard userActivityScorecard = findUserActivityScorecardWithPublicId(publicId);
		
		if(userActivityScorecard == null)
		{
			logger.info("No UserScorecard found for public id: " + publicId);
			return null;
		}
		
		VisitorData visitorData = new VisitorData();
		visitorData.setPublicId(publicId);
		visitorData.setUserId(userActivityScorecard.getUserId());
		visitorData.setActivityDate(userActivityScorecard.getActivityDate());
		visitorData.setFormattedActivityDate(formatDateStandard(visitorData.getActivityDate()));
		visitorData.setVisitorScore(userActivityScorecard.getVisitorScore());
		visitorData.setVisitorCount(userActivityScorecard.getVisitorCount());
		visitorData.setGameScore(userActivityScorecard.getGameScore());
		visitorData.setGameCount(userActivityScorecard.getGameCount());
		visitorData.setNetworkScore(userActivityScorecard.getNetworkScore());
		visitorData.setNetworkCount(userActivityScorecard.getNetworkCount());
		visitorData.setTotalScore(userActivityScorecard.getTotalScore());
		visitorData.setTotalCount(userActivityScorecard.getTotalCount());
		visitorData.setLastWeekChartData(userActivityScorecard.getLastWeekChartData());
		visitorData.setLastWeekTotalsChartData(userActivityScorecard.getLastWeekTotalsChartData());
		
		logger.info("Lookup UserNetworkActivity for User " + userActivityScorecard.getUserId() + " and Date " + userActivityScorecard.getActivityDate());
		List<UserNetworkActivity> networkActivities = findUserNetworkActivity(userActivityScorecard.getUserId(),userActivityScorecard.getActivityDate());
	
		long previousDate = dayBefore(userActivityScorecard.getActivityDate());
		logger.info("Lookup UserNetworkActivity for User " + userActivityScorecard.getUserId() + " and Date " + previousDate);
		List<UserNetworkActivity> previousNetworkActivities = findUserNetworkActivity(userActivityScorecard.getUserId(),previousDate);
		List<VisitorData.NetworkUsage> visitorNetworkUsage = new ArrayList<VisitorData.NetworkUsage>();
		for(UserNetworkActivity currentNetworkActivity : networkActivities)
		{
			logger.info("Setting up Usage of Network: " + currentNetworkActivity.getNetworkId());
			VisitorData.NetworkUsage networkUsage = new VisitorData.NetworkUsage();
			networkUsage.setNetworkId(currentNetworkActivity.getNetworkId());
			networkUsage.setCurrentCount(currentNetworkActivity.getCount());
			
			if(currentNetworkActivity.getNetworkUrl() != null)
			{
				try
				{
					networkUsage.setNetworkUrl(EncryptionUtil.decrypt(currentNetworkActivity.getNetworkUrl()));
				}
				catch(Exception e)
				{
					throw new RuntimeException("Failed to decrypt network url", e);
				}
			}
			

			for(UserNetworkActivity previousNetworkActivity : previousNetworkActivities)
			{
				if(currentNetworkActivity.getNetworkId().equals(previousNetworkActivity.getNetworkId()))
				{
					logger.info("Found previous Usage of Network: " + previousNetworkActivity.getNetworkId());
					networkUsage.setPreviousCount(previousNetworkActivity.getCount());
					break;
				}
			}
			
			networkUsage.setNetworkName(NetworkUtil.identifyNetworkFromLink(networkUsage.getNetworkUrl()));
			
			visitorNetworkUsage.add(networkUsage);
		}
		
		Collections.sort(visitorNetworkUsage, new Comparator<VisitorData.NetworkUsage>()
				{
					@Override
					public int compare(VisitorData.NetworkUsage o1, VisitorData.NetworkUsage o2)
					{
						if(o1==null && o2 ==null) return 0;
						if(o1.getNetworkName()==null && o2.getNetworkName()==null) return 0;
						
						if(o1.getNetworkName()!=null)
						{	
							return o1.getNetworkName().compareToIgnoreCase(o2.getNetworkName());
						}
						
						return -1;
					}
			
				});
		
		visitorData.setNetworkUsage(visitorNetworkUsage.toArray(new VisitorData.NetworkUsage[visitorNetworkUsage.size()]));

		return visitorData;
	}

/*	
	public static void main(String[] args) throws Exception
	{

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		dateFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		String todayFormatted = dateFormat.format(cal.getTime());
		cal.setTimeInMillis(dateFormat.parse(todayFormatted).getTime());
		cal.add(Calendar.DAY_OF_MONTH, -7);
		System.out.println(cal.getTime().toString());
		System.out.println(cal.getTimeInMillis());
		System.out.println(cal.get(Calendar.DAY_OF_MONTH));
		System.out.println(cal.getTimeZone().getDisplayName());
		cal.setTimeZone(TimeZone.getTimeZone("GMT+8"));
		System.out.println();
		System.out.println(cal.getTime().toString());
		System.out.println(cal.getTimeInMillis());
		System.out.println(cal.get(Calendar.DAY_OF_MONTH));
		System.out.println(cal.getTimeZone().getDisplayName());

	}
*/

}
