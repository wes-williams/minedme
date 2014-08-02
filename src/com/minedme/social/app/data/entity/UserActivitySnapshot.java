package com.minedme.social.app.data.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class UserActivitySnapshot
{
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	public Long getId()
	{
		return key==null?null:key.getId();
	}
	
	@Persistent
	private Long userId;
	@Persistent
	private Long snapshotDate; // date of snapshot
	
	@Persistent
	private Long userActivityScorecardId;
	@Persistent
	private String userActivityScorecardPublicId;
	
	@Persistent
	private int networkScore;
	@Persistent
	private int networkCount;
	@Persistent
	private int visitorScore;
	@Persistent
	private int visitorCount;
	@Persistent
	private int gameScore;
	@Persistent
	private int gameCount;
	@Persistent
	private int totalScore;
	@Persistent
	private int totalCount;
	
	@Persistent
	private String dayData; // chart data for day by network
	@Persistent
	private String dayTotalsData; // chart data for day by network
	@Persistent
	private String weekData; // working copy of chart data for current week by network and day of week
	@Persistent
	private String weekTotalsData; // working copy of chart totals for current week by network
	@Persistent
	private String monthData; // working copy of chart data for current month by network and day of month - 31 possible entries too much for String in bigtable = 500+ chars
	@Persistent 
	private Text monthDataText; // month data is too long at end of month (>500chars). must change columns 
	@Persistent
	private String monthTotalsData; // working copy of chart totals for current month by network 
	@Persistent
	private String yearData; // working copy of chart data for current year by network and month of year
	@Persistent
	private Text yearDataText; // possibility of being too long at end of year. change columns now...
	@Persistent
	private String yearTotalsData; // working copy of chart totals for current year by network

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public Long getSnapshotDate()
	{
		return snapshotDate;
	}

	public void setSnapshotDate(Long snapshotDate)
	{
		this.snapshotDate = snapshotDate;
	}

	public Long getUserActivityScorecardId()
	{
		return userActivityScorecardId;
	}

	public void setUserActivityScorecardId(Long userActivityScorecardId)
	{
		this.userActivityScorecardId = userActivityScorecardId;
	}

	public String getUserActivityScorecardPublicId()
	{
		return userActivityScorecardPublicId;
	}

	public void setUserActivityScorecardPublicId(String userActivityScorecardPublicId)
	{
		this.userActivityScorecardPublicId = userActivityScorecardPublicId;
	}

	public int getNetworkScore()
	{
		return networkScore;
	}

	public void setNetworkScore(int networkScore)
	{
		this.networkScore = networkScore;
	}

	public int getNetworkCount()
	{
		return networkCount;
	}

	public void setNetworkCount(int networkCount)
	{
		this.networkCount = networkCount;
	}

	public int getVisitorScore()
	{
		return visitorScore;
	}

	public void setVisitorScore(int visitorScore)
	{
		this.visitorScore = visitorScore;
	}

	public int getVisitorCount()
	{
		return visitorCount;
	}

	public void setVisitorCount(int visitorCount)
	{
		this.visitorCount = visitorCount;
	}

	public int getGameScore()
	{
		return gameScore;
	}

	public void setGameScore(int gameScore)
	{
		this.gameScore = gameScore;
	}

	public int getGameCount()
	{
		return gameCount;
	}

	public void setGameCount(int gameCount)
	{
		this.gameCount = gameCount;
	}

	public int getTotalScore()
	{
		return totalScore;
	}

	public void setTotalScore(int totalScore)
	{
		this.totalScore = totalScore;
	}

	public int getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(int totalCount)
	{
		this.totalCount = totalCount;
	}

	public String getDayData()
	{
		return dayData;
	}

	public void setDayData(String dayData)
	{
		this.dayData = dayData;
	}
	
	public String getDayTotalsData()
	{
		return dayTotalsData;
	}

	public void setDayTotalsData(String dayTotalsData)
	{
		this.dayTotalsData = dayTotalsData;
	}

	public String getWeekData()
	{
		return weekData;
	}

	public void setWeekData(String weekData)
	{
		this.weekData = weekData;
	}

	public String getWeekTotalsData()
	{
		return weekTotalsData;
	}

	public void setWeekTotalsData(String weekTotalsData)
	{
		this.weekTotalsData = weekTotalsData;
	}

	public String getMonthData()
	{
		if(monthDataText==null)
		{
			return monthData;
		}
		
		return monthDataText.getValue();
	}
	
	public void setMonthData(String monthData)
	{
		if(monthData==null)
		{
			this.monthDataText = null;
			this.monthData = null;
		}
		else
		{
			this.monthDataText = new Text(monthData);
			this.monthData=null;
		}
	}

	public String getMonthTotalsData()
	{		
		return monthTotalsData;
	}

	public void setMonthTotalsData(String monthTotalsData)
	{
		this.monthTotalsData = monthTotalsData;
	}

	public String getYearData()
	{
		if(yearDataText==null)
		{
			return yearData;
		}
		
		return yearDataText.getValue();
	}

	public void setYearData(String yearData)
	{
		if(yearData==null)
		{
			this.yearDataText = null;
			this.yearData = null;
		}
		else
		{
			this.yearDataText = new Text(yearData);
			this.yearData=null;
		}
	}

	public String getYearTotalsData()
	{
		return yearTotalsData;
	}

	public void setYearTotalsData(String yearTotalsData)
	{
		this.yearTotalsData = yearTotalsData;
	}
}
