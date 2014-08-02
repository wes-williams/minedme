package com.minedme.social.app.data.oauth;

public class UserData 
{
	private String id;
	private String token;
	private String secret;
	
	public String getId() 
	{
		return id;
	}
	
	public void setId(String id) 
	{
		this.id = id;
	}
	
	public String getToken() 
	{
		return token;
	}
	
	public void setToken(String token) 
	{
		this.token = token;
	}
	
	public String getSecret() 
	{
		return secret;
	}
	
	public void setSecret(String secret) 
	{
		this.secret = secret;
	}
}
