package com.minedme.social.app.data.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class UserActivityScorecard
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	public Long getId()
	{
		return key == null ? null : key.getId();
	}

	@Persistent
	private Long userId;
	@Persistent
	private long activityDate;
	@Persistent
	private String publicId;
	@Persistent
	private String passcode;
	@Persistent
	private byte networkScore;
	@Persistent
	private short networkCount;
	@Persistent
	private byte visitorScore;
	@Persistent
	private short visitorCount;
	@Persistent
	private byte gameScore;
	@Persistent
	private short gameCount;
	@Persistent
	private short totalScore;
	@Persistent
	private short totalCount;
	@Persistent
	private String lastWeekChartData;
	@Persistent
	private String lastWeekTotalsChartData;
	@Persistent
	private Long notificationSent;

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public long getActivityDate()
	{
		return activityDate;
	}

	public void setActivityDate(long activityDate)
	{
		this.activityDate = activityDate;
	}

	public String getPublicId()
	{
		return publicId;
	}

	public void setPublicId(String publicId)
	{
		this.publicId = publicId;
	}

	public String getPasscode()
	{
		return passcode;
	}

	public void setPasscode(String passcode)
	{
		this.passcode = passcode;
	}

	public byte getNetworkScore()
	{
		return networkScore;
	}

	public void setNetworkScore(byte networkScore)
	{
		this.networkScore = networkScore;
	}

	public short getNetworkCount()
	{
		return networkCount;
	}

	public void setNetworkCount(short networkCount)
	{
		this.networkCount = networkCount;
	}

	public byte getVisitorScore()
	{
		return visitorScore;
	}

	public void setVisitorScore(byte visitorScore)
	{
		this.visitorScore = visitorScore;
	}

	public short getVisitorCount()
	{
		return visitorCount;
	}

	public void setVisitorCount(short visitorCount)
	{
		this.visitorCount = visitorCount;
	}

	public byte getGameScore()
	{
		return gameScore;
	}

	public void setGameScore(byte gameScore)
	{
		this.gameScore = gameScore;
	}

	public short getGameCount()
	{
		return gameCount;
	}

	public void setGameCount(short gameCount)
	{
		this.gameCount = gameCount;
	}

	public short getTotalScore()
	{
		return totalScore;
	}

	public void setTotalScore(short totalScore)
	{
		this.totalScore = totalScore;
	}

	public short getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(short totalCount)
	{
		this.totalCount = totalCount;
	}

	public String getLastWeekChartData()
	{
		return lastWeekChartData;
	}

	public void setLastWeekChartData(String lastWeekChartData)
	{
		this.lastWeekChartData = lastWeekChartData;
	}

	public String getLastWeekTotalsChartData()
	{
		return lastWeekTotalsChartData;
	}

	public void setLastWeekTotalsChartData(String lastWeekTotalsChartData)
	{
		this.lastWeekTotalsChartData = lastWeekTotalsChartData;
	}

	public Long getNotificationSent()
	{
		return notificationSent;
	}

	public void setNotificationSent(Long notificationSent)
	{
		this.notificationSent = notificationSent;
	}
	
	
}
