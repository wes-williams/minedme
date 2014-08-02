package com.minedme.social.twitter.data;

import com.minedme.social.app.data.oauth.UserData;

public class TwitterUserData extends UserData 
{
	private String screenName;

	public String getScreenName()
	{
		return screenName;
	}

	public void setScreenName(String screenName)
	{
		this.screenName = screenName;
	}

}
