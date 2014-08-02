package com.minedme.social.util;

import java.io.StringWriter;
import java.util.logging.Logger;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;

public class EncryptionUtil
{
	private final static Logger logger = Logger.getLogger(EncryptionUtil.class.getName());

	private static final String HMAC_SHA1 = "HmacSHA1";
	private static final byte[] HMAC_KEY = "XXXXXXXXXXXXX".getBytes();

	private static final byte[] AES_KEY = "XXXXXXXXXXXXXXXX".getBytes();
	private static final byte[] AES_IV = "XXXXXXXXXXXXXXXX".getBytes();
	private static final String AES = "AES";
	private static final String AES_PADDING = "AES/CBC/PKCS5Padding";
	
	public static String sha1Random(String value, String secret) throws Exception
	{
		SecretKeySpec key = new SecretKeySpec((secret + "-" + new String(HMAC_KEY)).getBytes(), HMAC_SHA1);
		Mac mac = Mac.getInstance(HMAC_SHA1);
		mac.init(key);
		byte[] bytes = mac.doFinal(value.getBytes());
				
		return new String(Base64.encodeBase64(bytes)).replace("\r\n", "");
	}
	
	public static String sha1(String value) throws Exception
	{
		SecretKeySpec key = new SecretKeySpec(HMAC_KEY, HMAC_SHA1);
		Mac mac = Mac.getInstance(HMAC_SHA1);
		mac.init(key);
		byte[] bytes = mac.doFinal(value.getBytes());
				
		return new String(Base64.encodeBase64(bytes)).replace("\r\n", "");
	}

	public static String encrypt(String value) throws Exception
	{
		String encryptedValue = null;

		try
		{
			SecretKeySpec keyspec = new SecretKeySpec(AES_KEY.clone(), AES);
			IvParameterSpec ivspec = new IvParameterSpec(AES_IV.clone());

			Cipher cipher = Cipher.getInstance(AES_PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, keyspec, ivspec);

			byte[] encrypted = cipher.doFinal(value.getBytes());

			encryptedValue = Base64.encodeBase64String(encrypted);
		}
		catch (Throwable t)
		{
			logger.info("Encryption Failure: " + t.getMessage());
			throw new Exception("Encryption Failure");
		}

		return encryptedValue;
	}

	public static String decrypt(String value) throws Exception
	{
		String decryptedValue = null;

		try
		{
			SecretKeySpec keyspec = new SecretKeySpec(AES_KEY.clone(), AES);
			IvParameterSpec ivspec = new IvParameterSpec(AES_IV.clone());

			Cipher cipher = Cipher.getInstance(AES_PADDING);
			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);

			byte[] decrypted = cipher.doFinal(Base64.decodeBase64(value));

			decryptedValue = new String(decrypted);
		}
		catch (Throwable t)
		{
			logger.info("Decryption Failure: " + t.getMessage());
			throw new Exception("Decryption Failure");
		}

		return decryptedValue;
	}
	
	/*
	public static void main(String[] args) throws Exception
	{		
		java.util.HashMap<String,String> map = new java.util.HashMap<String,String>();
		map.put("password",EncryptionUtil.sha1(""));
		StringWriter writer = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		mapper.writeValue(writer, map);
		writer.flush();
		String encryptedJson = EncryptionUtil.encrypt(writer.toString());
		System.out.println(encryptedJson);
	
		System.out.println(EncryptionUtil.decrypt(""));
	}
	*/
}
