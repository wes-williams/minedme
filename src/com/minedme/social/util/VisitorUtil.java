package com.minedme.social.util;

import org.apache.commons.codec.binary.Base64;

public class VisitorUtil
{
	public static String createPublicIdentifier(Long activityId)
	{
		if(activityId <=0)
		{
			return null;
		}
		
		String salt = new StringBuilder(String.valueOf(System.currentTimeMillis())).reverse().substring(0,4);
		String uniqueUserActivityKey = salt + activityId;
		return Base64.encodeBase64URLSafeString(uniqueUserActivityKey.getBytes());
	}
	
	public static Long retrieveActivityForPublicIdentifier(String publicId)
	{
		try
		{
			return Long.parseLong(new String(Base64.decodeBase64(publicId.getBytes())).substring(4));
		}
		catch(Exception ex)
		{
		}
		
		return null;
	}
	
}
