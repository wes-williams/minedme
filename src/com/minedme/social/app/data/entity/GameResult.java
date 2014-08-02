package com.minedme.social.app.data.entity;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class GameResult
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
	private Long activityDate;
	@Persistent
	private Long playerId;
	@Persistent
	private String answer;
	@Persistent
	private boolean correct;
	@Persistent
	private byte yesterdayResult;
	@Persistent
	private byte todayResult;
	@Persistent
	private long playedDate;

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

	public Long getActivityDate()
	{
		return activityDate;
	}

	public void setActivityDate(Long activityDate)
	{
		this.activityDate = activityDate;
	}

	public Long getPlayerId()
	{
		return playerId;
	}

	public void setPlayerId(Long playerId)
	{
		this.playerId = playerId;
	}

	public String getAnswer()
	{
		return answer;
	}

	public void setAnswer(String answer)
	{
		this.answer = answer;
	}

	public boolean isCorrect()
	{
		return correct;
	}

	public void setCorrect(boolean correct)
	{
		this.correct = correct;
	}

	public byte getYesterdayResult()
	{
		return yesterdayResult;
	}

	public void setYesterdayResult(byte yesterdayResult)
	{
		this.yesterdayResult = yesterdayResult;
	}

	public byte getTodayResult()
	{
		return todayResult;
	}

	public void setTodayResult(byte todayResult)
	{
		this.todayResult = todayResult;
	}

	public long getPlayedDate()
	{
		return playedDate;
	}

	public void setPlayedDate(long playedDate)
	{
		this.playedDate = playedDate;
	}

}
