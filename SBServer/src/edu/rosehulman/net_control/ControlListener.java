package edu.rosehulman.net_control;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.rosehulman.Switchboard;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author lprestonsegoiii. Created Jan 12, 2011.
 */
public class ControlListener implements Runnable
{

	private static boolean	hasNotRecievedQuitCommand	= true;

	private Socket			socket						= null;
	private ServerSocket	server						= null;
	private DataInputStream	streamIn					= null;

	public ControlListener()
	{
		System.out.println("Control Server waiting for connections on " + Switchboard.serverIP + ":" + Switchboard.controlPort);

	}

	public void open() throws IOException
	{
		this.streamIn = new DataInputStream(new BufferedInputStream(
				this.socket.getInputStream()));
	}

	public void close() throws IOException
	{
		if (this.socket != null)
			this.socket.close();
		if (this.streamIn != null)
			this.streamIn.close();
	}

	/**
	 * Handles commands sent to the server
	 * 
	 * @param message
	 */
	private void handleCommand(String message)
	{

	}

	@Override
	public void run()
	{

		try
		{
			this.server = new ServerSocket(Switchboard.controlPort);
			System.out.println("Server started: " + this.server);
			System.out.println("Waiting for a client ...");
			this.socket = this.server.accept();
			System.out.println("Client accepted: " + this.socket);
			open();
			boolean done = false;
			while (!done)
			{
				try
				{
					String line = this.streamIn.readUTF();
					System.out.println(line);
					done = line.equals(".bye");
				}
				catch (IOException ioe)
				{
//					done = true;
					// we probably don't want this to happen. cause the client
					// should be able to be closed at anytime.
				}
			}
			close();
		}
		catch (IOException ioe)
		{
			System.out.println(ioe);
		}
	}
}
