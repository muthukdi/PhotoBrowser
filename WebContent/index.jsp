<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%!
	String loginError;
	String signupError;
%>
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Login/Sign-up</title>
	</head>
	<body>
		<h1>Login</h1>
		<%
			loginError = (String)request.getAttribute("login_error");
			if (loginError != null && !loginError.equals("null") && !loginError.equals(""))
			{
		%>
			<p style="color:red"><%=loginError %></p>
			<br />
		<%} %>
		<form action = "LoginServlet" method = "GET">
			Email: <input type="text" name="email">
			<br />
			Password: <input type="text" name="password">
			<br />
			<br />
			<input type="submit" value="Login">
		</form>
		<br />
		<br />
		<br />
		<h1>Create New User</h1>
		<%
			signupError = (String)request.getAttribute("signup_error");
			if (signupError != null && !signupError.equals("null") && !signupError.equals(""))
			{
		%>
			<p style="color:red"><%=signupError %></p>
			<br />
		<%} %>
		<form action = "SignupServlet" method = "GET">
			First Name: <input type="text" name="firstname">
			<br />
			Last Name: <input type="text" name="lastname">
			<br />
			Email: <input type="text" name="email">
			<br />
			Password: <input type="text" name="password">
			<br />
			<br />
			<input type="submit" value="Sign-Up">
		</form>
	</body>
</html>