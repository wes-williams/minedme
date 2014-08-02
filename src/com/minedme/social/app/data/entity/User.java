package com.minedme.social.app.data.entity;

import java.util.Date;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import com.google.appengine.api.datastore.Key;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class User
{
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	public Long getId()
	{
		return key==null?null:key.getId();
	}
	
	@Persistent
	private String accountName;
	@Persistent
	private String username;
	@Persistent
	private String privateData;
	@Persistent
	private boolean privacyEnabled;
	@Persistent
	private long createdDate;
	@Persistent
	private long lastModifiedDate;

	public String getAccountName()
	{
		return accountName;
	}

	public void setAccountName(String accountName)
	{
		this.accountName = accountName;
	}

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPrivateData()
	{
		return privateData;
	}

	public void setPrivateData(String privateData)
	{
		this.privateData = privateData;
	}

	public boolean isPrivacyEnabled()
	{
		return privacyEnabled;
	}

	public void setPrivacyEnabled(boolean privacyEnabled)
	{
		this.privacyEnabled = privacyEnabled;
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
