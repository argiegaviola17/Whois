/*
 *  Package:    GeekTools Whois Java Client 1.0.2
 *  File:       Whois.java (Java source file)
 *  Author:     Erik C. Thauvin <erik@skytouch.com>
 *  Comments:   Part of the GeekTools Whois Java Client package.
 *
 *  See the README.TXT file for more information.
 *
 *  Copyright (C) 2000-2001 SkyTouch Communications. All Rights Reserved.
 *  This program is distributed under the terms of the GNU General
 *  Public License as published by the Free Software Foundation.
 *  See the COPYING.TXT file for more information.
 *
 *  $Id$
 */
import java.io.*;

import java.net.Socket;
import java.util.Properties;

/**
 * Class Whois
 *
 * @author Erik C. Thauvin (erik@skytouch.com)
 * @version 1.0.2
 */
public class Whois
{
	/**
	 * Method main
	 *
	 * The Truth is Out There!
	 *
	 * @param args Command line arguments
	 */
	public static void main(String[] args)
	{
		// Display usage if there are no command line arguments
		if (args.length < 1)
		{
			System.out.println("Usage: java Whois query[@<whois.server>]");

			return;
		}

		// Default server is whois.geektools.com
		String server = "whois.geektools.com";

		// Default server port is 43
		int port = 43;

		// Load the properties file.
		try
		{
			final FileInputStream in = new FileInputStream("Whois.properties");
			final Properties app = new Properties();

			app.load(in);

			// Get the server property
			server = (app.getProperty("server", server));

			// Get the port property
			try
			{
				port = Integer.parseInt(app.getProperty("port"));
			}
			catch (NumberFormatException e)
			{
				// Do nothing!
			}

			in.close();
		}
		catch (FileNotFoundException e)
		{
			// Do nothing!
		}
		catch (IOException e)
		{
			System.err.println("Whois: an error occurred while loading the properties file: " + e);
		}

		// Build the whois query using command line arguments
		final StringBuffer buff = new StringBuffer(args[0]);

		for (int i = 1; i < args.length; i++)
		{
			buff.append(" " + args[i]);
		}

		// Convert string buffer to string
		String query = buff.toString();

		// The whois server can be specified after "@"
		// e.g.: query@whois.networksolutions.com
		final int at = query.lastIndexOf("@");
		final int len = query.length();

		if ((at != -1))
		{
			// Remove the @ if last character in query
			// e.g.: john@doe.com@
			if (at == (len - 1))
			{
				query = query.substring(0, len - 1);
			}
			else
			{
				// The whois server is specified after "@"
				server = query.substring(at + 1, len);
				// The whois query is specified before "@"
				query = query.substring(0, at);
			}
		}

		try
		{
			// Establish connection to whois server & port
			final Socket connection = new Socket(server, port);
			final PrintStream out =
					new PrintStream(connection.getOutputStream());
			final BufferedReader in = new BufferedReader(
					new InputStreamReader(connection.getInputStream()));
			String line = "";

			// Send the whois query
			out.println(query);
			// Display the whois server's address & port
			System.out.println("[" + server + ":" + port + "]");

			// Read/display the query's result
			while ((line = in.readLine()) != null)
			{
				System.out.println(line);
			}
		}
		catch (java.net.UnknownHostException e)
		{
			// Unknown whois server
			System.err.println("Whois: unknown host: " + server);

			return;
		}
		catch (IOException e)
		{
			// Could not connect to whois server
			System.err.println("Whois: " + e);

			return;
		}
	}
}
