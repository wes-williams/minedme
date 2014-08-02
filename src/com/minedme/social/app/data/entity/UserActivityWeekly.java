package com.minedme.social.app.data.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

public class UserActivityWeekly
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
	private short yearNumber;
	@Persistent
	private byte weekNumber;
	@Persistent
	private long startOfWeekDate;
	@Persistent
	private long endOfWeekDate;
	@Persistent
	private String data;
	@Persistent
	private String totalsData;

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public short getYearNumber()
	{
		return yearNumber;
	}

	public void setYearNumber(short yearNumber)
	{
		this.yearNumber = yearNumber;
	}

	public byte getWeekNumber()
	{
		return weekNumber;
	}

	public void setWeekNumber(byte weekNumber)
	{
		this.weekNumber = weekNumber;
	}

	public long getStartOfWeekDate()
	{
		return startOfWeekDate;
	}

	public void setStartOfWeekDate(long startOfWeekDate)
	{
		this.startOfWeekDate = startOfWeekDate;
	}

	public long getEndOfWeekDate()
	{
		return endOfWeekDate;
	}

	public void setEndOfWeekDate(long endOfWeekDate)
	{
		this.endOfWeekDate = endOfWeekDate;
	}

	public String getData()
	{
		return data;
	}

	public void setData(String data)
	{
		this.data = data;
	}

	public String getTotalsData()
	{
		return totalsData;
	}

	public void setTotalsData(String totalsData)
	{
		this.totalsData = totalsData;
	}
	
}
