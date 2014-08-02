package com.minedme.social.app.data.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class NetworkActivity
{
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	public Long getId()
	{
		return key==null?null:key.getId();
	}
	
	@Persistent
	private String networkId;
	@Persistent
	private Long activityDate;
	@Persistent
	private int count;
	@Persistent
	private int mode;
	@Persistent
	private int median;
	@Persistent
	private int mean;
	@Persistent
	private int min;
	@Persistent
	private int max;

	public String getNetworkId()
	{
		return networkId;
	}

	public void setNetwork(String networkId)
	{
		this.networkId = networkId;
	}

	public Long getActivityDate()
	{
		return activityDate;
	}

	public void setActivityDate(Long activityDate)
	{
		this.activityDate = activityDate;
	}

	public int getCount()
	{
		return count;
	}

	public void setCount(int count)
	{
		this.count = count;
	}

	public int getMode()
	{
		return mode;
	}

	public void setMode(int mode)
	{
		this.mode = mode;
	}

	public int getMedian()
	{
		return median;
	}

	public void setMedian(int median)
	{
		this.median = median;
	}

	public int getMean()
	{
		return mean;
	}

	public void setMean(int mean)
	{
		this.mean = mean;
	}

	public int getMin()
	{
		return min;
	}

	public void setMin(int min)
	{
		this.min = min;
	}

	public int getMax()
	{
		return max;
	}

	public void setMax(int max)
	{
		this.max = max;
	}
}
