package com.minedme.social.app.data.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

public class UserActivityYearly
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
	private long startOfYearDate;
	@Persistent
	private long endOfYearDate;
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

	public long getStartOfYearDate()
	{
		return startOfYearDate;
	}

	public void setStartOfYearDate(long startOfYearDate)
	{
		this.startOfYearDate = startOfYearDate;
	}

	public long getEndOfYearDate()
	{
		return endOfYearDate;
	}

	public void setEndOfYearDate(long endOfYearDate)
	{
		this.endOfYearDate = endOfYearDate;
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
