package com.minedme.social.app.data;

import com.minedme.social.app.data.entity.UserActivityScorecard;
import com.minedme.social.app.data.entity.UserNetwork;

public interface NetworkDataHandler
{
	public void sendNotification(UserNetwork userNetwork, UserActivityScorecard activityScorecard, int outsideNetworkCount, int outsideNetworkPercent) throws Exception;
}
