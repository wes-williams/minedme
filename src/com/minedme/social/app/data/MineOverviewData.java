package com.minedme.social.app.data;

public class MineOverviewData
{
	private String publicId;
	private int prospectorCount;
	private int nuggetCount;
	private int appraisalValue;	
	private String formattedDate;
	private String dayData;
	private String dayTotalsData;
	private String weekData;
	private String weekTotalsData;
	private String monthData;
	private String monthTotalsData;
	private String yearData;
	private String yearTotalsData;
	private NetworkDetail[] networkDetails;
	
	public String getPublicId()
	{
		return publicId;
	}

	public void setPublicId(String publicId)
	{
		this.publicId = publicId;
	}

	public int getProspectorCount()
	{
		return prospectorCount;
	}

	public void setProspectorCount(int prospectorCount)
	{
		this.prospectorCount = prospectorCount;
	}

	public int getNuggetCount()
	{
		return nuggetCount;
	}

	public void setNuggetCount(int nuggetCount)
	{
		this.nuggetCount = nuggetCount;
	}

	public int getAppraisalValue()
	{
		return appraisalValue;
	}

	public void setAppraisalValue(int appraisalValue)
	{
		this.appraisalValue = appraisalValue;
	}

	public String getFormattedDate()
	{
		return formattedDate;
	}

	public void setFormattedDate(String formattedDate)
	{
		this.formattedDate = formattedDate;
	}

	public String getDayData()
	{
		return dayData;
	}

	public void setDayData(String dayData)
	{
		this.dayData = dayData;
	}

	public String getDayTotalsData()
	{
		return dayTotalsData;
	}

	public void setDayTotalsData(String dayTotalsData)
	{
		this.dayTotalsData = dayTotalsData;
	}

	public String getWeekData()
	{
		return weekData;
	}

	public void setWeekData(String weekData)
	{
		this.weekData = weekData;
	}

	public String getWeekTotalsData()
	{
		return weekTotalsData;
	}

	public void setWeekTotalsData(String weekTotalsData)
	{
		this.weekTotalsData = weekTotalsData;
	}

	public String getMonthData()
	{
		return monthData;
	}

	public void setMonthData(String monthData)
	{
		this.monthData = monthData;
	}

	public String getMonthTotalsData()
	{
		return monthTotalsData;
	}

	public void setMonthTotalsData(String monthTotalsData)
	{
		this.monthTotalsData = monthTotalsData;
	}

	public String getYearData()
	{
		return yearData;
	}

	public void setYearData(String yearData)
	{
		this.yearData = yearData;
	}

	public String getYearTotalsData()
	{
		return yearTotalsData;
	}

	public void setYearTotalsData(String yearTotalsData)
	{
		this.yearTotalsData = yearTotalsData;
	}
	
	public NetworkDetail[] getNetworkDetails()
	{
		return networkDetails;
	}

	public void setNetworkDetails(NetworkDetail[] networkDetails)
	{
		this.networkDetails = networkDetails;
	}

	public static class NetworkDetail
	{
		private long networkId;
		private String networkName;
		private String networkUrl;
		
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
	}
}
