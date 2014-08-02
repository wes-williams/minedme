package com.minedme.social.util;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class MemcacheUtil
{
	private static final String CACHE_VERSION = "v1";
	public static final String CONNECT_NAMESPACE = "mm.connect";
	public static final String GAME_NAMESPACE = "mm.game";
	public static final String MINE_SNAPSHOT_NAMESPACE= "mm.mine.shot";
	
	public static MemcacheService memcacheService;

	
	public static boolean remove(String namespace, String key)
	{
		if(namespace == null || namespace.length()==0 || key == null || key.length()==0)
		{
			throw new IllegalArgumentException("namespace and key are required");
		}
		
		if(memcacheService == null)
		{
			memcacheService = MemcacheServiceFactory.getMemcacheService();
		}
		
		return memcacheService.delete(createKey(namespace,key));
	}
	
	public static String get(String namespace, String key)
	{
		if(namespace == null || namespace.length()==0 || key == null || key.length()==0)
		{
			throw new IllegalArgumentException("namespace and key are required");
		}
		
		if(memcacheService == null)
		{
			memcacheService = MemcacheServiceFactory.getMemcacheService();
		}
		
		return (String) memcacheService.get(createKey(namespace,key));
	}
	
	public static void put(String namespace, String key, String value)
	{
		if(namespace == null || namespace.length()==0 || key == null || key.length()==0)
		{
			throw new IllegalArgumentException("namespace and key are required");
		}
		
		if(memcacheService == null)
		{
			memcacheService = MemcacheServiceFactory.getMemcacheService();
		}
		
		memcacheService.put(createKey(namespace,key), value);
	}
	
	public static void put(String namespace, String key, String value, int secondsDelay)
	{
		if(namespace == null || namespace.length()==0 || key == null || key.length()==0)
		{
			throw new IllegalArgumentException("namespace and key are required");
		}
		
		if(memcacheService == null)
		{
			memcacheService = MemcacheServiceFactory.getMemcacheService();
		}
		
		memcacheService.put(createKey(namespace,key), value, Expiration.byDeltaSeconds(secondsDelay));
	}
	
	public static long countDown(String namespace, String key, long initialValue)
	{
		if(namespace == null || namespace.length()==0 || key == null || key.length()==0)
		{
			throw new IllegalArgumentException("namespace and key are required");
		}
		
		if(initialValue <= 0)
		{
			throw new IllegalArgumentException("positive values are required");
		}
		
		if(memcacheService == null)
		{
			memcacheService = MemcacheServiceFactory.getMemcacheService();
		}
		
		return memcacheService.increment(createKey(namespace,key),-1L, initialValue);
	}
	
	private static String createKey(String namespace, String key)
	{
		return CACHE_VERSION + "#" + namespace + "#" + key;
	}
		
	
}
