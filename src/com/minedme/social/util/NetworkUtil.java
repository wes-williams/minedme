package com.minedme.social.util;

public class NetworkUtil
{
	public static String providePresentableName(String networkName)
	{
		if("FACEBOOK".equalsIgnoreCase(networkName))
		{
			return "Facebook";
		}
		else if("FOURSQUARE".equalsIgnoreCase(networkName))
		{
			return "Foursquare";
		}
		else if("LINKEDIN".equalsIgnoreCase(networkName))
		{
			return "LinkedIn";
		}
		else if("TWITTER".equalsIgnoreCase(networkName))
		{
			return "Twitter";
		}
		
		return networkName;
	}

	public static String identifyNetworkFromLink(String url)
	{
		
		if(url != null && url.length() > 0)
		{
			if(url.indexOf("://foursquare.com") != -1)
			{
				return "Foursquare";
			}
			else if(url.indexOf("://twitter.com") != -1)
			{
				return "Twitter";
			}
			else if(url.indexOf("://www.linkedin.com") != -1)
			{
				return "LinkedIn";
			}
			else if(url.indexOf("://www.facebook.com") != -1)
			{
				return "Facebook";
			}
		}
			
		return  "Unknown";
	}
}
