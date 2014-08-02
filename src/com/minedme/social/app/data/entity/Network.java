package com.minedme.social.app.data.entity;

import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Network 
{
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	public Long getId()
	{
		return key==null?null:key.getId();
	}
	
	@Persistent
	private String name;
	@Persistent
	private String privateData;
	@Persistent
	private long createdDate;
	@Persistent
	private long lastModifiedDate;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getPrivateData()
	{
		return privateData;
	}

	public void setPrivateData(String privateData)
	{
		this.privateData = privateData;
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
