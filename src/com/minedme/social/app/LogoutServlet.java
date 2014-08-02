package com.minedme.social.app;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minedme.social.app.data.SessionData;


@SuppressWarnings("serial")
public class LogoutServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(LogoutServlet.class.getName());

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{
		SessionData sessionData = new SessionData(req,resp);
		
		sessionData.invalidate();
		
		resp.sendRedirect(req.getContextPath() + NavigationConstants.INDEX);
	}
}