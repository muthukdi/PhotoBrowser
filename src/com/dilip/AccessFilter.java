package com.dilip;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@WebFilter(filterName = "AccessFilter",
urlPatterns = {"/*"},
initParams = {@WebInitParam(name = "login_page", value = "/index.jsp")})
public class AccessFilter implements Filter
{
	private FilterConfig filterConfig;
	
	public void init(FilterConfig fConfig) throws ServletException
	{
		filterConfig = fConfig;
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		System.out.println("Request intercpted");
		boolean authorized = false;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String Uri = httpRequest.getRequestURI();
	    HttpSession session = httpRequest.getSession(false);
	    if (session != null)
	    {
	        Member member = (Member) session.getAttribute("member");
	        if (member != null)
	        {
	        	authorized = true;
	        }
	    }
	    if (authorized)
	    {
	        chain.doFilter(request, response);
	        return;
	    }
	    else if (Uri.contains("LoginServlet"))
	    {
	    	chain.doFilter(request, response);
	        return;
	    }
	    else if (Uri.contains("SignupServlet"))
	    {
	    	chain.doFilter(request, response);
	        return;
	    }
	    else if (Uri.contains("ActivationServlet"))
	    {
	    	chain.doFilter(request, response);
	        return;
	    }
	    else if (filterConfig != null)
	    {
	        String login_page = filterConfig.getInitParameter("login_page");
	        if (login_page != null && !"".equals(login_page))
	        {
	            filterConfig.getServletContext().getRequestDispatcher(login_page).forward(request, response);
	            return;
	        }
	    }
	    
	    throw new ServletException
		("Unauthorized access, unable to forward to login page");
	}

	public void destroy()
	{
		
	}

}