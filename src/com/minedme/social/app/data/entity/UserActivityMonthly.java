package com.minedme.social.app.data.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

public class UserActivityMonthly
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
	private byte monthNumber;
	@Persistent
	private long startOfMonthDate;
	@Persistent
	private long endOfMonthDate;
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

	public byte getMonthNumber()
	{
		return monthNumber;
	}

	public void setMonthNumber(byte monthNumber)
	{
		this.monthNumber = monthNumber;
	}

	public long getStartOfMonthDate()
	{
		return startOfMonthDate;
	}

	public void setStartOfMonthDate(long startOfMonthDate)
	{
		this.startOfMonthDate = startOfMonthDate;
	}

	public long getEndOfMonthDate()
	{
		return endOfMonthDate;
	}

	public void setEndOfMonthDate(long endOfMonthDate)
	{
		this.endOfMonthDate = endOfMonthDate;
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
