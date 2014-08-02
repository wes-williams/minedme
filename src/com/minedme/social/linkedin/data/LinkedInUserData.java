package com.minedme.social.linkedin.data;

import com.minedme.social.app.data.oauth.UserData;

public class LinkedInUserData extends UserData 
{
	private String url;

	public String getUrl()
	{
		return url;
	}

	public void setUrl(String url)
	{
		this.url = url;
	}

}
