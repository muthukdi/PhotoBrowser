<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
 
<%@ page import="java.io.File" %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Dilip's Web Page</title>
</head>
<body>
<h1>Welcome to Dilip's Photo Browser!</h1>
<%
	// Retrieve stored attributes and parameters from this request
	File[] imagePathsArray = (File[]) request.getAttribute("imagePaths");		// file handles for images in the current directory
	File[] folderPathsArray = (File[]) request.getAttribute("folderPaths");		// file handles for folders in the current directory
	File current = (File) request.getAttribute("current");						// the current directory
	String lastImageIndex = request.getParameter("lastImageIndex");				// used to determine the last image to be displayed
	
	// This will happen on a first visit
	if (current == null)
	{
%>
		<h3>Unknown Directory</h3>
<%
	}
	// This can either happen on a first visit, or if it's not a valid
	// directory, or else if there are no folders nor images present
	if (imagePathsArray == null && folderPathsArray == null)
	{
%>
		<a href="ImageServlet?path=C:\\">C:\</a>
<%
	}
	else
	{
%>
		<h3><%=current.getAbsolutePath()%></h3>
<%
		// There are no folders in this directory
		if (folderPathsArray != null)
		{
			// List the folders first in bold
			for (int i = 0; i < folderPathsArray.length; i++)
			{
%>
				<!-- If the linked directory contains images, lastImageIndex is used to implement pagination -->
				<a href="ImageServlet?path=<%=folderPathsArray[i].getAbsolutePath()%>&lastImageIndex=10"><b><%=folderPathsArray[i].getName()%></b></a>
				<br />
<%
			}
		}
		// There are no image files in this directory
		if (imagePathsArray != null)
		{
			int last = Integer.parseInt(lastImageIndex);
			// The last index should not exceed the index of the last image
			last = Math.min(imagePathsArray.length, last);
			// Only display ten images at a time to avoid "OutOfMemoryError"
			int first = last < 10 ? 0 : last - 10;
			// Render the images
			for (int i = first; i < last; i++)
			{
%>
				<!-- We link to the original image but we render the thumbnails asynchronously -->
				<a href="ImageServlet?path=<%=imagePathsArray[i].getAbsolutePath()%>&type=original">
					<img src="ImageServlet?path=<%=imagePathsArray[i].getAbsolutePath()%>&type=thumbnail" />
				</a>
				<br />
<%
			}
			// If there are more images to be displayed
			if (last != imagePathsArray.length)
			{
				// The last index of the next set should
				// be ten more than the current one
				last += 10;
%>
				<br />
				<br />
				<a href="ImageServlet?path=<%=current.getAbsolutePath()%>&lastImageIndex=<%=last%>"><b>Next >></b></a>
<%
			}
		}
	}
%>

</body>
</html>