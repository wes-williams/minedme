package com.minedme.social.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class EncoderUtil
{

	public static String urlEncode(String data)
	{
		try
		{
			return URLEncoder.encode(data, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException("Failed URL Encoding",e);
		}
	}
}
