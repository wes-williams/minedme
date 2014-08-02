package com.minedme.social.app;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minedme.social.app.data.SessionData;

@SuppressWarnings("serial")
public class WelcomeServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(WelcomeServlet.class.getName());
	private final static String WELCOME_PAGE ="/WEB-INF/jsp/welcome.jsp";

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{		
		if(!SessionData.isValid(req,resp))
		{
			resp.sendRedirect(NavigationConstants.LOGIN);
			return;
		}
		
		req.getRequestDispatcher(WELCOME_PAGE).forward(req, resp);
	}
}