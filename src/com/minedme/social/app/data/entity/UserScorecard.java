package com.minedme.social.app.data.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class UserScorecard
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
	private int networkScore;
	@Persistent
	private int networkCount;
	@Persistent
	private int visitorScore;
	@Persistent
	private int visitorCount;
	@Persistent
	private int gameScore;
	@Persistent
	private int gameCount;
	@Persistent
	private int totalScore;
	@Persistent
	private int totalCount;

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public int getNetworkScore()
	{
		return networkScore;
	}

	public void setNetworkScore(int networkScore)
	{
		this.networkScore = networkScore;
	}

	public int getNetworkCount()
	{
		return networkCount;
	}

	public void setNetworkCount(int networkCount)
	{
		this.networkCount = networkCount;
	}

	public int getVisitorScore()
	{
		return visitorScore;
	}

	public void setVisitorScore(int visitorScore)
	{
		this.visitorScore = visitorScore;
	}

	public int getVisitorCount()
	{
		return visitorCount;
	}

	public void setVisitorCount(int visitorCount)
	{
		this.visitorCount = visitorCount;
	}

	public int getGameScore()
	{
		return gameScore;
	}

	public void setGameScore(int gameScore)
	{
		this.gameScore = gameScore;
	}

	public int getGameCount()
	{
		return gameCount;
	}

	public void setGameCount(int gameCount)
	{
		this.gameCount = gameCount;
	}

	public int getTotalScore()
	{
		return totalScore;
	}

	public void setTotalScore(int totalScore)
	{
		this.totalScore = totalScore;
	}

	public int getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(int totalCount)
	{
		this.totalCount = totalCount;
	}

}
