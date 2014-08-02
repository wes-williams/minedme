package com.minedme.social.app.data.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class UserNetworkActivity
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
	private Long networkId;
	@Persistent
	private long activityDate;
	@Persistent
	private byte count;
	@Persistent
	private String networkUrl;

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public Long getNetworkId()
	{
		return networkId;
	}

	public void setNetworkId(Long networkId)
	{
		this.networkId = networkId;
	}

	public long getActivityDate()
	{
		return activityDate;
	}

	public void setActivityDate(long activityDate)
	{
		this.activityDate = activityDate;
	}

	public byte getCount()
	{
		return count;
	}

	public void setCount(byte count)
	{
		this.count = count;
	}

	public String getNetworkUrl()
	{
		return networkUrl;
	}

	public void setNetworkUrl(String networkUrl)
	{
		this.networkUrl = networkUrl;
	}

}
