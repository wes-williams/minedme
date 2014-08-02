package com.minedme.social.app;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.codehaus.jackson.map.ObjectMapper;

import com.minedme.social.app.data.DataStore;
import com.minedme.social.app.data.SessionData;
import com.minedme.social.app.data.entity.User;
import com.minedme.social.foursquare.data.FoursquareDataStore;
import com.minedme.social.util.EncryptionUtil;

@SuppressWarnings("serial")
public class LoginServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(LoginServlet.class.getName());
	private final static String LOGIN_PAGE = "/WEB-INF/jsp/login.jsp";
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{
		SessionData sessionData = new SessionData(req,resp);
		
		if(sessionData.isValid())
		{
			sessionData.invalidate();
		}
		
		req.getRequestDispatcher(LOGIN_PAGE).forward(req, resp);
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{
		try
		{
			SessionData sessionData = new SessionData(req,resp);
			
			if(sessionData.isValid())
			{
				sessionData.invalidate();
			}
			
			String username = req.getParameter("username");
			String password = req.getParameter("password");
			
			if(username == null || password == null ||
					username.length()==0 || password.length()==0 ||
					!username.equals(username.trim()) || !password.equals(password.trim()))
			{
				handleError("login",req,resp);
				return;
			}
			
			try
			{
				password = EncryptionUtil.sha1(password);
			}
			catch(Exception e)
			{
				handleError("login",req,resp);
				return;
			}
			
			User user = null;
			
			DataStore dataStore = new DataStore();
			try
			{
				dataStore.connect();
				user = dataStore.findUserByUsername(username);
			}
			finally
			{
				dataStore.disconnect();
			}
			
			if(user == null)
			{
				handleError("login",req,resp);
				return;
			}
			
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String,String> privateData = mapper.readValue(EncryptionUtil.decrypt(user.getPrivateData()),HashMap.class);
			if(!password.equals(privateData.get("password")))
			{
				handleError("login",req,resp);
				return;
			}
			
			if(!sessionData.activate(user))
			{
				handleError("session",req,resp);
				return;			
			}
			
			resp.sendRedirect(req.getContextPath() + NavigationConstants.HOME);		
			return;
		}
		catch(Throwable t)
		{
			logger.info("Login Processing Failure: " + t.getMessage());
			handleError("processing",req,resp);
			return;
		}
	}
	
	private void handleError(String error, HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		req.setAttribute("errors", error);
		req.getRequestDispatcher(LOGIN_PAGE).forward(req, resp);
	}
}