package com.dilip;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/ImageServlet")
public class ImageServlet extends HttpServlet 
{
	private static final long serialVersionUID = 1L;
	
	/*
	 * This method merges two functionalities so it should be re-factored, ideally.  The first segment
	 * is responsible for rendering an image to the output stream.  The second segment is responsible
	 * for sending a list of folder and image links back to the JSP page.
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// Get the parameters from the request
		String path = request.getParameter("path");
		String type = request.getParameter("type");
		System.out.println("Path=" + path + ", Type=" + type);
		// Obtain a handle on the file/folder represented by this path
		File f = new File(path);
		// If it is a file, we can proceed to render it since we can assume
		// that it is an image file at this point
		if (f.isFile())
		{
			response.setContentType("image/jpeg");
			// Make sure that only one image is stored and manipulated in
			// memory at any given time in order to avoid memory heap errors
			synchronized(this)
			{
				// Read the image data into memory
				BufferedImage img = ImageIO.read(f);
				// If this image cannot be read, then replace it with a default image
				if (img == null)
				{
					String pathToWeb = getServletContext().getRealPath("/");
					img = ImageIO.read(new File(pathToWeb + "images\\error.png"));
				}
				// If we require a thumbnail, then resize the image
		        if (type.equals("thumbnail"))
		        {
		        	int w = img.getWidth();
			        int h = img.getHeight();
			        if (h > w)
			        {
			        	img = resize(img, 100, (int)(h*100.0)/w);
			        }
			        else
			        {
			        	img = resize(img, (int)(w*100.0)/h, 100);
			        }
		        }
		        OutputStream out = response.getOutputStream();
		        // Render the image to the browser window
				ImageIO.write(img, "jpg", out);
				img.flush();
				out.close();
			}
		}
		// If it is a folder, get its list of files and sub-folders
		else
		{
			File[] paths = f.listFiles();
			// This can be null if the folder represented by this file does not exist
			if (paths != null)
			{
				ArrayList<File> imagePathsList = new ArrayList<File>();
				ArrayList<File> folderPathsList = new ArrayList<File>();
				// Split the list into folders and images and filter out unwanted files
				for (int i = 0; i < paths.length; i++)
				{
					if (paths[i].isHidden())
					{
						continue;
					}
					else if (paths[i].isFile())
					{
						String name = paths[i].getName().toLowerCase();
						// Only include files with common image file extensions
						if (!(name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".gif")))
						{
							continue;
						}
						imagePathsList.add(paths[i]);
					}
					else if (paths[i].isDirectory())
					{
						folderPathsList.add(paths[i]);
					}
				}
				File[] imagePathsArray = null;
				// Can happen if there are no images in this directory
				if (!imagePathsList.isEmpty())
				{
					imagePathsArray = (File[]) imagePathsList.toArray(new File[imagePathsList.size()]);
				}
				File[] folderPathsArray = null;
				// Can happen if there are no sub-folders in this directory
				if (!folderPathsList.isEmpty())
				{
					folderPathsArray = (File[]) folderPathsList.toArray(new File[folderPathsList.size()]);
				}
				// Store the results in the request
				request.setAttribute("imagePaths", imagePathsArray);
				request.setAttribute("folderPaths", folderPathsArray);
				request.setAttribute("current", f);
			}
			// Forward the request back to the JSP page
			RequestDispatcher dispatcher = getServletContext().getRequestDispatcher("/index.jsp");
			dispatcher.forward(request, response);
		}

	}
	
	public BufferedImage resize(BufferedImage img, int newW, int newH) 
	{
	    BufferedImage scaledImg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB);
	    Graphics g = scaledImg.createGraphics();
	    g.drawImage(img, 0, 0, newW, newH, null);
	    g.dispose();
	    return scaledImg;
	}  

}
