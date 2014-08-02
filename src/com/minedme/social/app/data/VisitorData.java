package com.minedme.social.app.data;

public class VisitorData
{
	private String publicId;
	private Long userId;
	private long activityDate;
	private String formattedActivityDate;
	private byte visitorScore;
	private short visitorCount;
	private byte gameScore;
	private short gameCount;
	private byte networkScore;
	private short networkCount;
	private short totalScore;
	private short totalCount;
	private NetworkUsage[] networkUsage;
	private String lastWeekChartData;
	private String lastWeekTotalsChartData;
	
	public String getPublicId()
	{
		return publicId;
	}

	public void setPublicId(String publicId)
	{
		this.publicId = publicId;
	}

	public Long getUserId()
	{
		return userId;
	}

	public void setUserId(Long userId)
	{
		this.userId = userId;
	}

	public long getActivityDate()
	{
		return activityDate;
	}

	public void setActivityDate(long activityDate)
	{
		this.activityDate = activityDate;
	}

	
	public String getFormattedActivityDate()
	{
		return formattedActivityDate;
	}

	public void setFormattedActivityDate(String formattedActivityDate)
	{
		this.formattedActivityDate = formattedActivityDate;
	}

	public byte getVisitorScore()
	{
		return visitorScore;
	}

	public void setVisitorScore(byte visitorScore)
	{
		this.visitorScore = visitorScore;
	}

	public short getVisitorCount()
	{
		return visitorCount;
	}

	public void setVisitorCount(short visitorCount)
	{
		this.visitorCount = visitorCount;
	}

	public byte getGameScore()
	{
		return gameScore;
	}

	public void setGameScore(byte gameScore)
	{
		this.gameScore = gameScore;
	}

	public short getGameCount()
	{
		return gameCount;
	}

	public void setGameCount(short gameCount)
	{
		this.gameCount = gameCount;
	}

	public byte getNetworkScore()
	{
		return networkScore;
	}

	public void setNetworkScore(byte networkScore)
	{
		this.networkScore = networkScore;
	}

	public short getNetworkCount()
	{
		return networkCount;
	}

	public void setNetworkCount(short networkCount)
	{
		this.networkCount = networkCount;
	}

	public short getTotalScore()
	{
		return totalScore;
	}

	public void setTotalScore(short totalScore)
	{
		this.totalScore = totalScore;
	}

	public short getTotalCount()
	{
		return totalCount;
	}

	public void setTotalCount(short totalCount)
	{
		this.totalCount = totalCount;
	}

	public NetworkUsage[] getNetworkUsage()
	{
		return networkUsage;
	}

	public void setNetworkUsage(NetworkUsage[] networkUsage)
	{
		this.networkUsage = networkUsage;
	}

	public String getLastWeekChartData()
	{
		return lastWeekChartData;
	}

	public void setLastWeekChartData(String lastWeekChartData)
	{
		this.lastWeekChartData = lastWeekChartData;
	}

	public String getLastWeekTotalsChartData()
	{
		return lastWeekTotalsChartData;
	}

	public void setLastWeekTotalsChartData(String lastWeekTotalsChartData)
	{
		this.lastWeekTotalsChartData = lastWeekTotalsChartData;
	}


	public static class NetworkUsage
	{
		private long networkId;
		private String networkName;
		private String networkUrl;
		private byte previousCount;
		private byte currentCount;

		public long getNetworkId()
		{
			return networkId;
		}

		public void setNetworkId(long networkId)
		{
			this.networkId = networkId;
		}
		

		public String getNetworkName()
		{
			return networkName;
		}

		public void setNetworkName(String networkName)
		{
			this.networkName = networkName;
		}

		public String getNetworkUrl()
		{
			return networkUrl;
		}

		public void setNetworkUrl(String networkUrl)
		{
			this.networkUrl = networkUrl;
		}

		public byte getPreviousCount()
		{
			return previousCount;
		}

		public void setPreviousCount(byte previousCount)
		{
			this.previousCount = previousCount;
		}

		public byte getCurrentCount()
		{
			return currentCount;
		}

		public void setCurrentCount(byte currentCount)
		{
			this.currentCount = currentCount;
		}
	}
}
