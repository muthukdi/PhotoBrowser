package com.dilip;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/SignupServlet")
public class SignupServlet extends HttpServlet
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
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String email = request.getParameter("email");
		String password = request.getParameter("password");
		String activationCode = new RandomString(20).nextString();
		response.setContentType("text/html");
		// Basic form validation
		if (firstname == null || firstname.equals("null") || firstname.equals("")
				|| lastname == null || lastname.equals("null") || lastname.equals("")
				|| email == null || email.equals("null") || email.equals("")
				|| password == null || password.equals("null") || password.equals(""))
		{
			request.setAttribute("signup_error", "Check your input fields!");
			request.getRequestDispatcher("/index.jsp").forward(request, response);
			return;
		}
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			cn = DriverManager.getConnection(db_path, db_user, db_password);
			st = cn.createStatement();
			// Check if this user already exists
			sqlCommand = "select * from member where email = '" + email + "'";
			rs = st.executeQuery(sqlCommand);
			// If the user exists
			if (rs.next())
			{
				// If the user is active, then redirect them
				if (rs.getString("status").equals("ACTIVE"))
				{
					request.setAttribute("signup_error", "This user already exists!");
					request.getRequestDispatcher("/index.jsp").forward(request, response);
				}
				// If the user is inactive, then they will be overwritten
				else
				{
					sqlCommand = "update member "
							+ "set firstname = '" + firstname + "', "
							+ "lastname = '" + lastname + "', "
							+ "password = '" + password + "', "
							+ "activation_code = '" + activationCode + "', "
							+ "status = 'INACTIVE' "
							+ "where email = '" + email + "'";
					st.executeUpdate(sqlCommand);
					sendActivationLink(firstname, lastname, email, activationCode);
					request.setAttribute("status_message", "User has been updated successfully. Please click on the activation link sent to your email inbox.");
					request.getRequestDispatcher("/status.jsp").forward(request, response);
				}
			}
			// If the user doesn't exist, then create a new one
			else
			{
				sqlCommand = "insert into member "
						+ "(email, password, firstname, lastname, activation_code, status) "
						+ "values "
						+ "('" + email + "', "
						+ "'" + password + "', "
						+ "'" + firstname + "', "
						+ "'" + lastname + "', "
						+ "'" + activationCode + "', "
						+ "'INACTIVE')";
				st.executeUpdate(sqlCommand);
				sendActivationLink(firstname, lastname, email, activationCode);
				request.setAttribute("status_message", "User has been added successfully. Please click on the activation link sent to your email inbox.");
				request.getRequestDispatcher("/status.jsp").forward(request, response);
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
	
	private void sendActivationLink(String firstname, String lastname, String email, String activationCode)
	{
		String from = "dmphotobrowser@gmail.com";
		String host = "smtp.gmail.com";
		String port = "587";
		Properties properties = System.getProperties();
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", "true");
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", port);
		Session session = Session.getInstance(properties,
		new javax.mail.Authenticator()
		{
			protected PasswordAuthentication getPasswordAuthentication()
			{
				return new PasswordAuthentication("dmphotobrowser", "cheepu82");
			}
		});
		try
		{
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(from));
				message.addRecipient(Message.RecipientType.BCC, new InternetAddress(email));
				message.setSubject("Member Activation Link");
				message.setText("Dear " + firstname + " " + lastname + ",\n\n" + "Please click on this activation link to validate your new account:\n\n" 
				+ "http://52.32.251.178:8080/PhotoBrowser/ActivationServlet?email=" + email + "&activation_code=" + activationCode);
				Transport.send(message);
		}
		catch (MessagingException mex)
		{
			StringWriter errors = new StringWriter();
			mex.printStackTrace(new PrintWriter(errors));
			System.out.println(errors.toString());
		}
	}

}