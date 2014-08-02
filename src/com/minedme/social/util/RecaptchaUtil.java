package com.minedme.social.util;

import javax.servlet.http.HttpServletRequest;

import net.tanesha.recaptcha.ReCaptcha;
import net.tanesha.recaptcha.ReCaptchaFactory;
import net.tanesha.recaptcha.ReCaptchaImpl;
import net.tanesha.recaptcha.ReCaptchaResponse;

public class RecaptchaUtil
{
	private static final String PUBLIC_KEY = "XXXXXXXXXXXXXXXXX";
	private static final String PRIVATE_KEY = "XXXXXXXXXXXXXXXXX";
	public static String newRecaptcha()
	{
		ReCaptcha c = ReCaptchaFactory.newSecureReCaptcha(PUBLIC_KEY, PRIVATE_KEY, false);
        return c.createRecaptchaHtml(null, null);
	}
	
	public static boolean verifyRecaptcha(HttpServletRequest request)
	{
        String remoteAddr = request.getRemoteAddr();
        ReCaptchaImpl reCaptcha = new ReCaptchaImpl();
        reCaptcha.setPrivateKey(PRIVATE_KEY);

        String challenge = request.getParameter("recaptcha_challenge_field");
        String uresponse = request.getParameter("recaptcha_response_field");
        ReCaptchaResponse reCaptchaResponse = reCaptcha.checkAnswer(remoteAddr, challenge, uresponse);

        return reCaptchaResponse.isValid();
	}
}
