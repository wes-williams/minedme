package com.minedme.social.app;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minedme.social.app.data.DataStore;
import com.minedme.social.app.data.SessionData;
import com.minedme.social.app.data.entity.User;
import com.minedme.social.app.validators.UserValidator;
import com.minedme.social.util.RecaptchaUtil;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet
{
	private final static Logger logger = Logger.getLogger(RegisterServlet.class.getName());
	private final static String REGISTER_PAGE = "/WEB-INF/jsp/register.jsp";

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{
		 req.getRequestDispatcher(REGISTER_PAGE).forward(req, resp);
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{
		try
		{
			if(!RecaptchaUtil.verifyRecaptcha(req))
			{
				handleError("captcha", req,resp);
				return;
			}

			User user = new User();
			if(!UserValidator.validateAndPopulate(user, req))
			{
				handleError("user",req,resp);
				return;
			}
			
			DataStore dataStore = new DataStore();
			try
			{
				dataStore.connect();
				dataStore.saveUser(user);
			}
			finally
			{
				dataStore.disconnect();
			}
			
			SessionData sessionData = new SessionData(req,resp);
			if(sessionData.activate(user))
			{
				resp.sendRedirect(req.getContextPath() + NavigationConstants.WELCOME);
			}
			else
			{
				handleError("activation",req,resp);
			}
			return;
		}
		catch (Throwable t)
		{
			logger.info("Registration Processing Failure: " + t.getMessage());
			handleError("processing",req,resp);
			return;
		}
	}
	
	private void handleError(String error, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		req.setAttribute("errors", error);
		req.getRequestDispatcher(REGISTER_PAGE).forward(req, resp);
	}
}
