package com.minedme.social.app;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.minedme.social.app.data.SessionData;

@SuppressWarnings("serial")
public class IndexServlet extends HttpServlet 
{
	private final static Logger logger = Logger.getLogger(IndexServlet.class.getName());
	private final static String INDEX_PAGE ="/WEB-INF/jsp/index.jsp";

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException
	{		
		if(SessionData.isValid(req,resp))
		{
			req.getRequestDispatcher(NavigationConstants.HOME).forward(req, resp);
			return;
		}
		
		req.getRequestDispatcher(INDEX_PAGE).forward(req, resp);
	}
}