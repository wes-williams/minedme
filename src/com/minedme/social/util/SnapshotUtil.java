package com.minedme.social.util;

//import com.minedme.social.app.admin.UserActivityAccumulatorServlet;
import com.minedme.social.app.data.entity.ActivityDate;
import com.minedme.social.app.data.entity.UserActivityScorecard;
import com.minedme.social.app.data.entity.UserActivitySnapshot;
import com.minedme.social.app.data.entity.UserNetworkActivity;
import com.minedme.social.app.data.entity.UserScorecard;

//import java.text.SimpleDateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

//import org.codehaus.jackson.map.ObjectMapper;


public class SnapshotUtil
{
	private final static Logger logger = Logger.getLogger(SnapshotUtil.class.getName());
	
	//private static final int HEADER_ROW = 0;
	//private static final int FIRST_DATA_COLUMN=1;
	private final static String DEFAULT_TIMEZONE = "America/Denver";
	
	//private ObjectMapper mapper = new ObjectMapper();
		
	private void accumulateYear(TreeMap<String,List<Integer>> yearData, String networkName, UserNetworkActivity networkActivity, ActivityDate activityDate, Map<Long,List<UserNetworkActivity>> pastNetworkActivities )
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		
		if(yearData.get(networkName)==null)
		{
			yearData.put(networkName,new ArrayList<Integer>());
		}
		
		List<Integer> networkYearData = yearData.get(networkName);
		
		if(networkYearData.size() <= activityDate.getMonthNumber()) // not current to today?
		{
			logger.info("Filling in missing year data for " + networkActivity.getNetworkId());
			Integer[] yearFiller = new Integer[activityDate.getMonthNumber()-networkYearData.size()];
			Arrays.fill(yearFiller, 0);
			networkYearData.addAll(Arrays.asList(yearFiller));
		}

		
		int currentMonthPosition = activityDate.getMonthNumber()-1;
		networkYearData.set(currentMonthPosition,networkYearData.get(currentMonthPosition)+networkActivity.getCount());
		
		if(pastNetworkActivities!=null && pastNetworkActivities.get(networkActivity.getNetworkId()) != null)
		{	
			logger.info("Backfilling missing year data for Network " + networkActivity.getNetworkId());
			for(UserNetworkActivity thisNetworkActivity : pastNetworkActivities.get(networkActivity.getNetworkId()))
			{
				if(thisNetworkActivity.getActivityDate() >= activityDate.getUtcTime())
				{
					break;
				}
				
				cal.setTimeInMillis(thisNetworkActivity.getActivityDate());
				if(cal.get(Calendar.YEAR)==activityDate.getYearNumber())
				{
					int thisMonthPosition = cal.get(Calendar.MONTH); // month is 0 based
					int thisMonthValue = networkYearData.get(thisMonthPosition);
					networkYearData.set(thisMonthPosition,thisMonthValue + thisNetworkActivity.getCount());
				}
			}
		}
	}
	
	private void accumulateMonth(TreeMap<String,List<Integer>> monthData, String networkName, UserNetworkActivity networkActivity, ActivityDate activityDate, Map<Long,List<UserNetworkActivity>> pastNetworkActivities )
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		
		if(monthData.get(networkName)==null)
		{
			monthData.put(networkName,new ArrayList<Integer>());
		}
		
		List<Integer> networkMonthData = monthData.get(networkName);
		
		if(networkMonthData.size() <= activityDate.getDayNumber()) // not current to today?
		{
			logger.info("Filling in missing month data for " + networkActivity.getNetworkId());
			Integer[] monthFiller = new Integer[activityDate.getDayNumber()-networkMonthData.size()];
			Arrays.fill(monthFiller, 0);
			networkMonthData.addAll(Arrays.asList(monthFiller));
		}
		
		int currentDayPosition = activityDate.getDayNumber()-1;
		networkMonthData.set(currentDayPosition, networkMonthData.get(currentDayPosition)+networkActivity.getCount());
		
		if(pastNetworkActivities!=null && pastNetworkActivities.get(networkActivity.getNetworkId()) != null)
		{	
			logger.info("Backfilling missing month data for Network " + networkActivity.getNetworkId());
			for(UserNetworkActivity thisNetworkActivity : pastNetworkActivities.get(networkActivity.getNetworkId()))
			{
				if(thisNetworkActivity.getActivityDate() >= activityDate.getUtcTime())
				{
					break;
				}
				
				cal.setTimeInMillis(thisNetworkActivity.getActivityDate());
				if(cal.get(Calendar.YEAR)==activityDate.getYearNumber() && cal.get(Calendar.MONTH)+1==activityDate.getMonthNumber()) // month is 0 based
				{
					int thisDayPosition = cal.get(Calendar.DAY_OF_MONTH)-1;
					int thisDayValue = networkMonthData.get(thisDayPosition);
					networkMonthData.set(thisDayPosition,thisDayValue + thisNetworkActivity.getCount());
				}
			}
		}
	}

	private void accumulateWeek(TreeMap<String,List<Integer>> weekData, String networkName, UserNetworkActivity networkActivity, ActivityDate activityDate, Map<Long,List<UserNetworkActivity>> pastNetworkActivities )
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
		
		if(weekData.get(networkName)==null)
		{
			weekData.put(networkName,new ArrayList<Integer>());
		}
		
		List<Integer> networkWeekData = weekData.get(networkName);
		
		if(networkWeekData.size() <= activityDate.getDayOfWeekNumber()-1) // not current to today?
		{
			logger.info("Filling in missing week data for " + networkActivity.getNetworkId());
			Integer[] weekFiller = new Integer[activityDate.getDayOfWeekNumber()-networkWeekData.size()];
			Arrays.fill(weekFiller, 0);
			networkWeekData.addAll(Arrays.asList(weekFiller));
		}
		
		int currentDayPosition = activityDate.getDayOfWeekNumber()-1;
		networkWeekData.set(currentDayPosition,networkWeekData.get(currentDayPosition)+networkActivity.getCount());
	
		if(pastNetworkActivities!=null && pastNetworkActivities.get(networkActivity.getNetworkId()) != null)
		{	
			logger.info("Backfilling missing week data for Network " + networkActivity.getNetworkId());
			for(UserNetworkActivity thisNetworkActivity : pastNetworkActivities.get(networkActivity.getNetworkId()))
			{
				if(thisNetworkActivity.getActivityDate() >= activityDate.getUtcTime())
				{
					break;
				}
				
				cal.setTimeInMillis(thisNetworkActivity.getActivityDate());
				if(cal.get(Calendar.YEAR)==activityDate.getYearNumber() && cal.get(Calendar.WEEK_OF_YEAR)==activityDate.getWeekOfYearNumber())
				{
					int thisDayPosition = cal.get(Calendar.DAY_OF_WEEK)-1;
					networkWeekData.set(thisDayPosition,Integer.valueOf(thisNetworkActivity.getCount()));
				}
			}
		}
	}

	public static UserActivitySnapshot freezeData(Long userId, ActivityDate activityDate, UserScorecard userScorecard, UserActivityScorecard userActivityScorecard, List<UserNetworkActivity> currentNetworkActivities, Map<Long,List<UserNetworkActivity>> pastNetworkActivities )
	{
		UserActivitySnapshot userActivitySnapshot = new UserActivitySnapshot();
		userActivitySnapshot.setUserId(userId);
		userActivitySnapshot.setSnapshotDate(activityDate.getUtcTime());
		userActivitySnapshot.setNetworkCount(userScorecard.getNetworkCount());
		userActivitySnapshot.setNetworkScore(userScorecard.getNetworkScore());
		userActivitySnapshot.setVisitorCount(userScorecard.getVisitorCount());
		userActivitySnapshot.setVisitorScore(userScorecard.getVisitorScore());
		userActivitySnapshot.setGameCount(userScorecard.getGameCount());
		userActivitySnapshot.setGameScore(userScorecard.getGameScore());
		userActivitySnapshot.setTotalCount(userScorecard.getTotalCount());
		userActivitySnapshot.setTotalScore(userScorecard.getTotalScore());
		userActivitySnapshot.setUserActivityScorecardId(userActivityScorecard.getId());
		userActivitySnapshot.setUserActivityScorecardPublicId(userActivityScorecard.getPublicId());

		SnapshotUtil util = new SnapshotUtil();
		util.accumulate(userActivitySnapshot, activityDate, currentNetworkActivities, pastNetworkActivities);
		
		return userActivitySnapshot;
	}
	
	
	private void accumulate(UserActivitySnapshot snapshot, ActivityDate activityDate, List<UserNetworkActivity> currentNetworkActivities, Map<Long,List<UserNetworkActivity>> pastNetworkActivities )
	{	
		try
		{	
			logger.info("accumulating network data for User " + snapshot.getUserId());
			// * WILL JUST REBUILD IT ALL FOR NOW 
//			TreeMap<String,List<Integer>> yearData = retrieveNetworkDataFromString(snapshot.getYearData());
//			TreeMap<String,List<Integer>> monthData = retrieveNetworkDataFromString(snapshot.getMonthData());
//			TreeMap<String,List<Integer>> weekData = retrieveNetworkDataFromString(snapshot.getWeekData());
			TreeMap<String,List<Integer>> yearData = new TreeMap<String,List<Integer>>();
			TreeMap<String,List<Integer>> monthData = new TreeMap<String,List<Integer>>();
			TreeMap<String,List<Integer>> weekData = new TreeMap<String,List<Integer>>();
			TreeMap<String,Integer> dayData = new TreeMap<String,Integer>();
			
			
			
			Set<String> currentNetworks = new HashSet<String>();
			for(UserNetworkActivity networkActivity : currentNetworkActivities)
			{
				logger.info("accumulating Network " + networkActivity.getNetworkId() + " for User " +  networkActivity.getUserId());
				
				String networkName = NetworkUtil.identifyNetworkFromLink(EncryptionUtil.decrypt(networkActivity.getNetworkUrl()));
				currentNetworks.add(networkName);
				
				accumulateYear(yearData,networkName,networkActivity,activityDate,pastNetworkActivities );
				accumulateMonth(monthData,networkName,networkActivity,activityDate,pastNetworkActivities );
				accumulateWeek(weekData,networkName,networkActivity,activityDate,pastNetworkActivities );			
				dayData.put(networkName, Integer.valueOf(networkActivity.getCount()));
			}
			
			// * WILL JUST REBUILD IT ALL FOR NOW 
//			if(!currentNetworks.containsAll(yearData.keySet()))
//			{
//				logger.info("Filling in networks that are not in use");;
//				for(String thisNetwork : yearData.keySet())
//				{
//					if(!currentNetworks.contains(thisNetwork))
//					{
//						Integer[] yearFiller = new Integer[activityDate.getMonthNumber()-yearData.get(thisNetwork).size()];
//						Arrays.fill(yearFiller, 0);
//						yearData.get(thisNetwork).addAll(Arrays.asList(yearFiller));
//						
//						Integer[] monthFiller = new Integer[activityDate.getDayNumber()-monthData.get(thisNetwork).size()];
//						Arrays.fill(monthFiller, 0);
//						monthData.get(thisNetwork).addAll(Arrays.asList(monthFiller));
//						
//						Integer[] weekFiller = new Integer[activityDate.getDayOfWeekNumber()-yearData.get(thisNetwork).size()];
//						Arrays.fill(weekFiller, 0);
//						weekData.get(thisNetwork).addAll(Arrays.asList(weekFiller));
//					}
//					
//				}
//			}
			
			logger.info("Converting snapshot back to presentable form");
			snapshot.setYearData(convertNetworkDataToString(yearData,activityDate,Presentation.YEARLY));
			snapshot.setYearTotalsData(convertNetworkDataToTotalsString(yearData,activityDate));
			snapshot.setMonthData(convertNetworkDataToString(monthData,activityDate,Presentation.MONTHLY));
			snapshot.setMonthTotalsData(convertNetworkDataToTotalsString(monthData,activityDate));
			snapshot.setWeekData(convertNetworkDataToString(weekData,activityDate,Presentation.WEEKLY));
			snapshot.setWeekTotalsData(convertNetworkDataToTotalsString(weekData,activityDate));
			snapshot.setDayData(convertDailyNetworkDataToString(dayData,activityDate));
			snapshot.setDayTotalsData(convertDailyNetworkDataToTotalsString(dayData,activityDate));
			snapshot.setSnapshotDate(activityDate.getUtcTime());
		}
		catch(Exception e)
		{
			logger.log(Level.INFO, "Snapshot Failed",e);
			throw new RuntimeException(e);
		}
	}
	
	// * WILL JUST REBUILD IT ALL FOR NOW 
//	private TreeMap<String,List<Integer>> retrieveNetworkDataFromString(String data) throws Exception
//	{
//		TreeMap<String,List<Integer>> dataByNetwork = new TreeMap<String,List<Integer>>();
//		
//		if(data!= null)
//		{
//			List<List> allData = mapper.readValue("[" + data  + "]", List.class);
//			for(int n=FIRST_DATA_COLUMN; n<allData.get(HEADER_ROW).size();n++) // loop over networks
//			{
//				List<Integer> networkDataList = new ArrayList<Integer>();
//				for(int m=FIRST_DATA_COLUMN;m<allData.size();m++) // loop over months
//				{
//					networkDataList.add((Integer) allData.get(m).get(n));
//				}
//				dataByNetwork.put((String)allData.get(HEADER_ROW).get(n), networkDataList);
//			}
//		}
//		
//		return dataByNetwork;
//	}
	
	private final static String[] DOW = {"'Sun'","'Mon'","'Tue'","'Wed'","'Thu'","'Fri'","'Sat'"};
	private final static String[] MOY = {"'Jan'","'Feb'","'Mar'","'Apr'","'May'","'Jun'","'Jul'","'Aug'","'Sep'","'Oct'","'Nov'","'Dec'"};
	private enum Presentation { WEEKLY,MONTHLY,YEARLY};
	private static String convertNetworkDataToString(TreeMap<String,List<Integer>>  data, ActivityDate activityDate,Presentation presentation) throws Exception
	{		
		if(presentation == null) throw new IllegalArgumentException("Presenation parameter is not optional");
		
		String headers=null;
		String[] rows = new String[data.get(data.firstKey()).size()];
		logger.info("Converting " + data.size() + " networks to strings");
		for(String networkName : data.keySet())
		{	
			if(headers==null)
			{
				switch(presentation)
				{
					case WEEKLY:
						headers = "['Day of Week'";
						break;
					case MONTHLY:
						headers = "['Day of Month'";
						break;
					case YEARLY:
						headers = "['Month of Year'";
						break;		
				}
			}
			headers += ",'" + networkName + "'";
			
			List<Integer> networkCounts = data.get(networkName);
			for(int i=0;i<networkCounts.size();i++)
			{
				if(rows[i]==null)
				{
					switch(presentation)
					{
						case WEEKLY:
							rows[i] =",[" + DOW[i];
							break;
						case YEARLY:
							rows[i]  = ",[" + MOY[i];
							break;
						default:
							rows[i]  = ",['" + (i+1) + "'";
							break;
					}
				}
				
				rows[i] += "," + networkCounts.get(i);
			}
			
		}
		headers += "]";
		String allData = headers;
		for(int i=0;i<rows.length;i++)
		{
			rows[i] += "]";
			allData += rows[i];
		}
		
		
		return allData;
	}

	
	private final static SimpleDateFormat todayFormat = new SimpleDateFormat("M/d/yyyy");
	static
	{
		todayFormat.setTimeZone(TimeZone.getTimeZone(DEFAULT_TIMEZONE));
	}
	
	private static String convertDailyNetworkDataToString(TreeMap<String,Integer>  data, ActivityDate activityDate) throws Exception
	{
		String header="['Day'";
		String row="['" + todayFormat.format(new Date(activityDate.getUtcTime()))+ "'";
		
		logger.info("Converting " + data.size() + " networks to strings");
		for(String networkName : data.keySet())
		{	
			header += ",'" + networkName + "'";
			row += "," + data.get(networkName);
		}
		header += "]";
		row += "]";
		
		return header + "," + row;
	}
	
	private static String convertDailyNetworkDataToTotalsString(TreeMap<String,Integer>  data, ActivityDate activityDate) throws Exception
	{	
		String headers="['Network','Usage']";
		String[] rows = new String[data.size()];
		int networkCount=0;
		for(String networkName : data.keySet())
		{				
			rows[networkCount] = ",['" + networkName + "'," + data.get(networkName) + "]";
			networkCount++;
		}
		String allData = headers;
		for(int i=0;i<rows.length;i++)
		{
			allData += rows[i];
		}
		
		return allData;
	}
	
	private static String convertNetworkDataToTotalsString(TreeMap<String,List<Integer>>  data, ActivityDate activityDate) throws Exception
	{
		String headers="['Network','Usage']";
		String[] rows = new String[data.size()];
		int networkCount=0;
		for(String networkName : data.keySet())
		{				
			int rowTotals =0;
			List<Integer> networkCounts = data.get(networkName);
			for(int i=0;i<networkCounts.size();i++)
			{
				rowTotals += networkCounts.get(i);	
			}
			rows[networkCount] = ",['" + networkName + "'," + rowTotals + "]";
			networkCount++;
		}
		String allData = headers;
		for(int i=0;i<rows.length;i++)
		{
			allData += rows[i];
		}
		
		return allData;
	}
	
}
