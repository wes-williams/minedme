package com.minedme.social.app.data.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ActivityDate
{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	public Long getId()
	{
		return key == null ? null : key.getId();
	}

	@Persistent
	private long utcTime;
	@Persistent
	private byte dayNumber;
	@Persistent
	private byte dayOfWeekNumber;
	@Persistent
	private byte weekOfYearNumber;
	@Persistent
	private byte monthNumber;
	@Persistent
	private String monthName;
	@Persistent
	private short yearNumber;

	public long getUtcTime()
	{
		return utcTime;
	}

	public void setUtcTime(long utcTime)
	{
		this.utcTime = utcTime;
	}

	public byte getDayNumber()
	{
		return dayNumber;
	}

	public void setDayNumber(byte dayNumber)
	{
		this.dayNumber = dayNumber;
	}

	public byte getDayOfWeekNumber()
	{
		return dayOfWeekNumber;
	}

	public void setDayOfWeekNumber(byte dayOfWeekNumber)
	{
		this.dayOfWeekNumber = dayOfWeekNumber;
	}

	public byte getWeekOfYearNumber()
	{
		return weekOfYearNumber;
	}

	public void setWeekOfYearNumber(byte weekOfYearNumber)
	{
		this.weekOfYearNumber = weekOfYearNumber;
	}

	public byte getMonthNumber()
	{
		return monthNumber;
	}

	public void setMonthNumber(byte monthNumber)
	{
		this.monthNumber = monthNumber;
	}

	public String getMonthName()
	{
		return monthName;
	}

	public void setMonthName(String monthName)
	{
		this.monthName = monthName;
	}

	public short getYearNumber()
	{
		return yearNumber;
	}

	public void setYearNumber(short yearNumber)
	{
		this.yearNumber = yearNumber;
	}

}
