package com.minedme.social.facebook.data;

import com.minedme.social.app.data.oauth.UserData;

public class FacebookUserData extends UserData 
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
