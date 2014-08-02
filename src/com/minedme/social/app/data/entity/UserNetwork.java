package com.minedme.social.app.data.entity;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class UserNetwork
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
	private Long networkId;
	@Persistent
	private String privateData;
	@Persistent
	private boolean readEnabled;
	@Persistent
	private boolean writeEnabled;
	@Persistent
	private boolean publicEnabled;
	@Persistent
	private int activityCount;
	@Persistent
	private long createdDate;
	@Persistent
	private long lastModifiedDate;
	
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

	public String getPrivateData()
	{
		return privateData;
	}

	public void setPrivateData(String privateData)
	{
		this.privateData = privateData;
	}

	public boolean isReadEnabled()
	{
		return readEnabled;
	}

	public void setReadEnabled(boolean readEnabled)
	{
		this.readEnabled = readEnabled;
	}

	public boolean isWriteEnabled()
	{
		return writeEnabled;
	}

	public void setWriteEnabled(boolean writeEnabled)
	{
		this.writeEnabled = writeEnabled;
	}

	public boolean isPublicEnabled()
	{
		return publicEnabled;
	}

	public void setPublicEnabled(boolean publicEnabled)
	{
		this.publicEnabled = publicEnabled;
	}

	public void setActivityCount(int activityCount)
	{
		this.activityCount = activityCount;
	}

	public int getActivityCount()
	{
		return activityCount;
	}
	
	public long getCreatedDate()
	{
		return createdDate;
	}

	public void setCreatedDate(long createdDate)
	{
		this.createdDate = createdDate;
	}

	public long getLastModifiedDate()
	{
		return lastModifiedDate;
	}

	public void setLastModifiedDate(long lastModifiedDate)
	{
		this.lastModifiedDate = lastModifiedDate;
	}
}
