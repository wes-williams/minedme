package com.minedme.social.app;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minedme.social.app.data.SessionData;

@SuppressWarnings("serial")
public class HomeServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(HomeServlet.class.getName());
	private final static String HOME_PAGE ="/WEB-INF/jsp/home.jsp";

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{		
		if(!SessionData.isValid(req,resp))
		{
			resp.sendRedirect(req.getContextPath() + NavigationConstants.LOGIN);
			return;
		}
		
		req.getRequestDispatcher(HOME_PAGE).forward(req, resp);
	}
}