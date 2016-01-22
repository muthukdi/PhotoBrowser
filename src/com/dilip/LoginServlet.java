package com.dilip;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet
{
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		String db_path = "jdbc:mysql://localhost/photobrowser";
		String db_user = "root";
		String db_password = "december";
		String sqlCommand = "";
		Connection cn = null;
		Statement st = null;
		ResultSet rs = null;
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String firstname = null;
		String lastname = null;
		response.setContentType("text/html");
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			cn = DriverManager.getConnection(db_path, db_user, db_password);
			st = cn.createStatement();
			sqlCommand = "select * from member where email = '" + email + "' and password = '" + password + "' and status = 'ACTIVE'";
			rs = st.executeQuery(sqlCommand);
			if (rs.next())
			{
				firstname = rs.getString("firstname");
				lastname = rs.getString("lastname");
				Member member = new Member();
				member.setEmail(email);
				member.setPassword(password);
				member.setFirstname(firstname);
				member.setLastname(lastname);
				HttpSession session = request.getSession(true);
				session.setAttribute("member", member);
				request.getRequestDispatcher("/browser.jsp").forward(request, response);
			}
			else
			{
				request.setAttribute("login_error", "Invalid username and/or password!");
				request.getRequestDispatcher("/index.jsp").forward(request, response);
			}
		}
		catch (ClassNotFoundException cnfe)
		{
			System.out.println(cnfe.getMessage());
		}
		catch (SQLException sqle)
		{
			System.out.println(sqle.getMessage());
		}
		finally
		{
			try
			{
				if (cn != null) cn.close();
				if (st != null) st.close();
				if (rs != null) rs.close();
			}
			catch (SQLException e)
			{
				System.out.println(e.getMessage());
			}
		}
	}

}