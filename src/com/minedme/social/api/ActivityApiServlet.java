package com.minedme.social.api;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import com.minedme.social.app.data.DataStore;
import com.minedme.social.app.data.entity.Network;
import com.minedme.social.app.data.entity.UserActivityScorecard;
import com.minedme.social.app.data.entity.UserNetworkActivity;
import com.minedme.social.app.data.entity.UserScorecard;
import com.minedme.social.util.NetworkUtil;

public class ActivityApiServlet extends HttpServlet {
	private final static Logger logger = Logger.getLogger(ActivityApiServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{ 
	  try {
		  final String pathInfo = req.getPathInfo();
		  
		  // api exposed at /api/social/activity/v100
		  // params in path should match one of the following:  
		  //  /user/123/network/123 - yesterday for network (all is valid network value)
		  //  /user/123/network/123/date/2012010  - chosen date for network (all is valid network value)
		  if(pathInfo ==null  || !pathInfo.matches("^((/\\w+){2}){2,3}$")) {
			throw new Exception("URL needs to be 2 to 3 pairs of directory paths: " + pathInfo);
		  }
		  
		  // separate the url into KVPs
		  // keep the original order
		  String[] pathTokens = pathInfo.substring(1).split("/");
          LinkedHashMap<String,String> queryParams = new LinkedHashMap<String,String>();
          for(int i=0;i<pathTokens.length;i=i+2) {
        	  //logger.log(Level.INFO, i + "=" + pathTokens[i] + " of " + pathInfo);
        	  
        	  // validate path matches expectations 
        	  switch(1+(i/2)) {
        	  	case 1: // user must be 9001
        	  		if(!pathTokens[i].equals("user")) {
        	  			throw new Exception("'user' must be 1st parameter");
        	  		}
        	  		else if(pathTokens[i+1].equals("wes")) { // allow wes to equal 9001
        	  			pathTokens[i+1] = "9001";
        	  		}
        	  		else if(!pathTokens[i+1].equals("9001")) { // 9001 is the only user...
        	  			throw new Exception("user must be valid");
        	  		}
        	  		break;
        	  	case 2: // network must be all, facebook, twitter, foursquare, or linkedin
        	  		if(!pathTokens[i].equals("network")) {
        	  			throw new Exception("'network' must be 3rd parameter");
        	  		}
        	  		else if(!pathTokens[i+1].matches("^(all|facebook|twitter|foursquare|linkedin)$")) {
        	  			throw new Exception("network must be valid");
        	  		}
        	  		break;  	
        	  	case 3:	// date must be valid - YYYYMMDD
        	  		if(!pathTokens[i].equals("date")) {
        	  			throw new Exception("'date' must be the 3rd parameter");
        	  		}
        	  		else if(!pathTokens[i+1].matches("^20[0-9]{2}(0[1-9]|1[0-2])(0[1-9]|1[0-9]|2[0-9]|3[01])$")) { //  need better regex
        	  			throw new Exception("date must be a valid");
        	  		}

        	  		break;
        	  }
        	  
        	  queryParams.put(pathTokens[i], pathTokens[i+1]);
          }
          
          // TODO - need to hide this date logic somewhere
          SimpleDateFormat dateFormat =new SimpleDateFormat("yyyyMMdd");
          dateFormat.setTimeZone(TimeZone.getTimeZone("America/Denver")); 
          
          Long activityDate = queryParams.get("date")==null?null:dateFormat.parse(queryParams.get("date")).getTime();
          
          // safety check on date requested. - must be between 7/2011 and yesterday
          if(activityDate != null &&
        		  (activityDate < dateFormat.parse("20110701").getTime() || 
        		   activityDate >= dateFormat.parse(dateFormat.format(new java.util.Date())).getTime())) { 
        	  
          }

          long userId = Long.parseLong(queryParams.get("user"));
          String networkName = queryParams.get("network");
	
          StringBuilder json = new StringBuilder();
          
		  DataStore dataStore = new DataStore();
		  try {
		     dataStore.connect();
		     
	         if(activityDate==null) {
	        	 activityDate = dataStore.yesterday().getUtcTime();
	         }	     
	    	 
	    	 if("all".equals(networkName)) {
	    		 // would it be better to lookup user's networks and loop that instead?
	    		 List<UserNetworkActivity> activities = dataStore.findUserNetworkActivity(userId, activityDate);
	    		 	             
	    		 // create json
	    		 // array of network, date, count
	    		 json.append("[ ");
	    		 int activityCount=0;
	    		 for(UserNetworkActivity activity : activities) {
	    			 activityCount++;
	    			 if(activityCount > 1) {
	    				 json.append(", ");
	    			 }
	    			 
	    			 // wasteful to load this for just the name...
	    			 Network network = dataStore.retrieveNetwork(activity.getNetworkId());
	    			 
		    		 json.append("{ ");
		    		 json.append(" \"network\" :  \"").append(network.getName().toLowerCase()).append("\", ");
		    		 json.append(" \"date\" : \"").append(dateFormat.format(new java.util.Date(activity.getActivityDate()))).append("\",");
		    		 json.append(" \"count\" : ").append(activity.getCount());
		    		 json.append(" }");    		 
	    		 }
	    		 json.append(" ]");
	    	 }
	    	 else {
	    		 Network network = dataStore.findNetworkByName(networkName.toUpperCase());
	    		 List<UserNetworkActivity> activities = dataStore.findUserActivity(userId, network.getId(), activityDate);
	    		 
	    		 // create json
	    		 // network, date, count
	    		 if(activities.size()>0) {
	    			 UserNetworkActivity activity = activities.get(0);	 
		    		 
		    		 json.append("{ ");
		    		 json.append(" \"network\" :  \"").append(networkName).append("\", ");
		    		 json.append(" \"date\" : \"").append(dateFormat.format(new java.util.Date(activity.getActivityDate()))).append("\",");
		    		 json.append(" \"count\" : ").append(activity.getCount());
		    		 json.append(" }");
	    		 }
	    		 else {
	    			 // send back default data    		 
		    		 json.append("{ ");
		    		 json.append(" \"network\" :  \"").append(networkName).append("\", ");
		    		 json.append(" \"date\" : \"").append(dateFormat.format(new java.util.Date(activityDate))).append("\",");
		    		 json.append(" \"count\" : 0");
		    		 json.append(" }");
	    		 }
	    	 }
		  }
		  finally {
		    dataStore.disconnect();
		  }
		  
		  // return as json	  
		  resp.setContentType("application/json; charset=utf-8");
		  resp.getWriter().println(json.toString());

		}
		catch(Throwable t)
		{
			logger.log(Level.WARNING,"API call failed",t);
			
			resp.setContentType("application/json; charset=utf-8");
			resp.getWriter().println("{ \"error\" : \"Unable to complete request\" }");
		}
	}
}
