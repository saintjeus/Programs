package edu.nmsu.cs.webserver;

/**
 * Web worker: an object of this class executes in its own new thread to receive and respond to a
 * single HTTP request. After the constructor the object executes on its "run" method, and leaves
 * when it is done.
 *
 * One WebWorker object is only responsible for one client connection. This code uses Java threads
 * to parallelize the handling of clients: each WebWorker runs in its own thread. This means that
 * you can essentially just think about what is happening on one client at a time, ignoring the fact
 * that the entirety of the webserver execution might be handling other clients, too.
 *
 * This WebWorker class (i.e., an object of this class) is where all the client interaction is done.
 * The "run()" method is the beginning -- think of it as the "main()" for a client interaction. It
 * does three things in a row, invoking three methods in this class: it reads the incoming HTTP
 * request; it writes out an HTTP header to begin its response, and then it writes out some HTML
 * content for the response content. HTTP requests and responses are just lines of text (in a very
 * particular format).
 * 
 * @author Jon Cook, Ph.D.
 *
 **/

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.text.SimpleDateFormat;

public class WebWorker implements Runnable
{

	private Socket socket;
	private String requestString ="";
	boolean GETvalid = false;
	boolean GETdefault = false;
	File file;
	/**
	 * Constructor: must have a valid open socket
	 **/
	public WebWorker(Socket s)
	{
		socket = s;
	}
	/**
	 * Worker thread starting point. Each worker handles just one HTTP request and then returns, which
	 * destroys the thread. This method assumes that whoever created the worker created it with a
	 * valid open socket object.
	 **/
	public void run()
	{
		System.err.println("Handling connection...");
		try
		{
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			readHTTPRequest(is);
			if (!GETdefault)
				GETvalid = checkRequest();
			writeHTTPHeader(os, "text/html");
			writeContent(os);
			os.flush();
			socket.close();
		}
		catch (Exception e)
		{
			System.err.println("Output error: " + e);
		}
		System.err.println("Done handling connection.");
		return;
	}

	/**
	 * Read the HTTP request header.
	 **/
	private void readHTTPRequest(InputStream is)
	{
		String line;
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		int counter = 0;
		while (true)
		{
			try
			{
				while (!r.ready())
					Thread.sleep(1);
				line = r.readLine();
				System.err.println("Request line: (" + line + ")");
				if(line.contains("GET")){
					String[]request = line.split(" ");
					requestString = request[1];
					System.out.println("requestString:"+requestString);
				}
				if (requestString.equals("/")||requestString.equals("/favicon.ico")){
					GETdefault = true; //load default page
				}
				if (line.length() == 0) {
					break;
				}
			}
			catch (Exception e)
			{
				System.err.println("Request error: " + e);
				break;
			}
		}
	}



	/**
	 * Write the HTTP header lines to the client network connection.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 * @param contentType
	 *          is the string MIME content type (e.g. "text/html")
	 **/
	private void writeHTTPHeader(OutputStream os, String contentType) throws Exception
	{
		Date d = new Date();
		DateFormat df = DateFormat.getDateTimeInstance();
		df.setTimeZone(TimeZone.getTimeZone("GMT"));
		/*check if GET request is valid. if no request, header and default message is fine.
		* if request length is greater than 1 and file path is not valid, send message 404 not found*/

		if (GETvalid || GETdefault){
			os.write("HTTP/1.1 200 OK\n".getBytes());
		}
		else {
			os.write("404 NOT FOUND\n".getBytes());
		}

		System.err.println("GETvalid: "+GETvalid);
		System.err.println("GETdefault: "+GETdefault);
		os.write("Date: ".getBytes());
		os.write((df.format(d)).getBytes());
		os.write("\n".getBytes());
		os.write("Server: Jon's very own server\n".getBytes());
		// os.write("Last-Modified: Wed, 08 Jan 2003 23:11:55 GMT\n".getBytes());
		// os.write("Content-Length: 438\n".getBytes());
		os.write("Connection: close\n".getBytes());
		os.write("Content-Type: ".getBytes());
		os.write(contentType.getBytes());
		os.write("\n\n".getBytes()); // HTTP header ends with 2 newlines
	}

	/**
	 * Write the data content to the client network connection. This MUST be done after the HTTP
	 * header has been written out.
	 * 
	 * @param os
	 *          is the OutputStream object to write to
	 **/
	private void writeContent(OutputStream os) throws Exception
	{

			if(GETvalid&&!GETdefault){
			try {
				System.err.println("requestString: " + requestString);
				System.err.println("file path it's looking for: "+ file.toString());
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line = "";
				while((line=reader.readLine()) != null){

					if(line.contains("<cs371date")){
						SimpleDateFormat format = new SimpleDateFormat("MM-dd-yyyy");
						String dateString = format.format(new Date());
						line.replaceAll("<cs371date", dateString);
					}
					if(line.contains("<cs371server")){
						line.replaceAll("<cs371server", "JESUSBARBA");
					}

					os.write(line.getBytes());
				}
			}

			catch(Exception e){
				System.err.println("ERROR STARTS HERE");
				e.printStackTrace();
				os.write("<html><head>404 NOT FOUND</head></html>".getBytes());
			}


		}

		else {
			String currentDir = System.getProperty("user.dir");
			System.out.println("GETvalid: "+GETvalid);
			System.out.println("currentDirectory: "+currentDir);
			os.write("<html><head></head><body>\n".getBytes());
			os.write("<h3>Default page for CS371 Java HTTP server</h3>\n".getBytes());
			//os.write(new String("<h1>localfile: "+ file.getPath() + "</h1>").getBytes());
			os.write("</body></html>\n".getBytes());
		}


	}
	private boolean checkRequest(){
		boolean check = false;
		file = new File(requestString);
		if (file.exists())
			GETvalid = true;
		return check;
	}
} // end class
