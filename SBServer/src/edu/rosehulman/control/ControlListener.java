package edu.rosehulman.control;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.Character.Subset;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

import edu.rosehulman.Switchboard;
import edu.rosehulman.control.Actions;

/**
 * This handles Switchboard server control cammands, and gives feedback to the
 * client
 * 
 * Nearly all of the code here is from and modified slightly by wachsmut
  http://pirate.shu.edu/~wachsmut/Teaching/CSAS2214/Virtual/Lectures/chat-client-server.html
 * 
 * @author lprestonsegoiii. Created Jan 13, 2011.
 */
public class ControlListener implements Runnable
{
	private static ControlServerClientThread	controlClients[]		= new ControlServerClientThread[10];
	private ServerSocket		serverSocket	= null;
	private Thread				thread			= null;
	private static int					controlClientCount		= 0;
	public static final int COMMAND = 0;
		
	/**
	 * Returns the value of the field called 'controlClients'.
	 * @return Returns the controlClients.
	 */
	public static ControlServerClientThread[] getControlClients()
	{
		return controlClients;
	}
	
	/**
	 * since the ID internally is kept by pids (which makes it difficult to 
	 * send messages to control clients), we need to get teh index
	 * that the control client is stored.
	 *
	 * @param ID - PID of the control client
	 * @return
	 */
	public static int getControlClientWithID(int ID)
	{
		for (int i = 0; i < controlClientCount; i++)
			if (ID == controlClients[i].pid) return i;
		return -1;
	}

	/**
	 * Returns the value of the field called 'clientCount'.
	 * @return Returns the clientCount.
	 */
	public static int getControlClientCount()
	{
		return controlClientCount;
	}


	/**
	 * Constructor
	 * @param port - the Control Server port must be unique, and unused.
	 * 
	 */
	public ControlListener(int port)
	{
		try
		{
			System.out.println("Binding to port " + port + ", please wait  ...");
			this.serverSocket = new ServerSocket(port);
			System.out.println("Server started: " + this.serverSocket);
			startControlServer();
		}
		catch (IOException ioe)
		{
			System.out.println("Can not bind to port " + port + ": " + ioe.getMessage());
		}
	}

	@Override
	public void run()
	{
		while (this.thread != null)
		{
			try
			{
				System.out.println("Waiting for a client ...");
				addThreadForControlClientConnection(this.serverSocket.accept());
			}
			catch (IOException ioe)
			{
				System.out.println("Server accept error: " + ioe);
				stopControlServer();
			}
		}
	}

	public void startControlServer()
	{
		if (this.thread == null)
		{
			this.thread = new Thread(this);
			this.thread.start();
		}
	}

	public void stopControlServer()
	{
		if (this.thread != null)
		{
			this.thread.stop();
			this.thread = null;
		}
	}

	private int findControlClient(int ID)
	{
		for (int i = 0; i < this.controlClientCount; i++)
			if (this.controlClients[i].getID() == ID)
				return i;
		return -1;
	}

	public synchronized void processMessage(int ID, String input)
	{
		System.out.println("RECIEVED: " + input);
		if (input.charAt(0) == '/')
		{
			// we have a command
			String commandString = input.substring(1, input.length());
			String[] commandArgs = commandString.split(" ");
			String command = commandArgs[COMMAND];
			
			if (Switchboard.DEBUG) System.out.println(command);
			
			if (command.equals("exit") || command.equals("quit"))
			{
				System.out.println("Client " + ID + " leaving...");
				this.controlClients[findControlClient(ID)].sendToAssociatedControlClient(".bye");
				removeControlClient(ID);
			}
			else if(command.equals("atc")) // add to client
			{
				// first IP is the user being added to, teh rest are more
				// audience members
				if (commandArgs.length > 2)
				{
					String target = commandArgs[1];
					String[] audience = Arrays.copyOfRange(commandArgs, 2, commandArgs.length);
					Actions.addToClient(ID, target, audience);
				}
				else
				{
					Messages.sendMessage(ID, "Not enough arguments.");
				}

			}
			else if (command.equals("rfc"))
			{
				// first IP is teh user being removed from, the rest are
				// adience members that are being removed
				
			}
			else if (command.equals("nc"))
			{
				// first arg is the IP, the second is the name for that client
	
			}
			else if (command.equals("ls"))
			{
				// list all the users, and send them to the client that asked
				Messages.sendListOfClients(ID);
			}
			else if (command.equals("help"))
			{
				// send help to the requesting control client.
				Messages.sendHelp(ID);
			}
			else
			{
				System.err.println(input);
			}
			
		}
		else
		{
//
		}
		
	}

	public synchronized void removeControlClient(int ID)
	{
		int pos = findControlClient(ID);
		if (pos >= 0)
		{
			ControlServerClientThread toTerminate = this.controlClients[pos];
			System.out.println("Removing client thread " + ID + " at " + pos);
			if (pos < this.controlClientCount - 1)
				for (int i = pos + 1; i < this.controlClientCount; i++)
					this.controlClients[i - 1] = this.controlClients[i];
			this.controlClientCount--;
			try
			{
				toTerminate.closeStreams();
			}
			catch (IOException ioe)
			{
				System.out.println("Error closing thread: " + ioe);
			}
			toTerminate.stop();
		}
	}

	private void addThreadForControlClientConnection(Socket socket)
	{
		if (this.controlClientCount < this.controlClients.length)
		{
			System.out.println("Client accepted: " + socket);
			this.controlClients[this.controlClientCount] = new ControlServerClientThread(this, socket);
			try
			{
				this.controlClients[this.controlClientCount].openStreams();
				this.controlClients[this.controlClientCount].start();
				this.controlClientCount++;
			}
			catch (IOException ioe)
			{
				System.out.println("Error opening thread: " + ioe);
			}
		}
		else
			System.out.println("Client refused: maximum " + this.controlClients.length + " reached.");
	}

	public class ControlServerClientThread extends Thread
	{
		private ControlListener		server		= null;
		private Socket				socket		= null;
		private int					pid			= -1;
		private DataInputStream		streamIn	= null;
		private DataOutputStream	streamOut	= null;

		public ControlServerClientThread(ControlListener _server, Socket _socket)
		{
			super();
			this.server = _server;
			this.socket = _socket;
			this.pid = this.socket.getPort();
		}

		public void sendToAssociatedControlClient(String msg)
		{
			try
			{
				this.streamOut.writeUTF(msg);
				this.streamOut.flush();
			}
			catch (IOException ioe)
			{
				System.out.println(this.pid + " ERROR sending: " + ioe.getMessage());
				this.server.removeControlClient(this.pid);
				stop();
			}
		}

		public int getID()
		{
			return this.pid;
		}

		@Override
		public void run()
		{
			System.out.println("Server Thread " + this.pid + " running.");
			while (true)
			{
				try
				{
					this.server.processMessage(ControlListener.getControlClientWithID(this.pid), this.streamIn.readUTF());
				}
				catch (IOException ioe)
				{
					System.out.println(this.pid + " ERROR reading: " + ioe.getMessage());
					this.server.removeControlClient(this.pid);
					stop();
				}
			}
		}

		public void openStreams() throws IOException
		{
			this.streamIn = new DataInputStream(new BufferedInputStream(
					this.socket.getInputStream()));
			this.streamOut = new DataOutputStream(new BufferedOutputStream(
					this.socket.getOutputStream()));
		}

		public void closeStreams() throws IOException
		{
			if (this.socket != null)
				this.socket.close();
			if (this.streamIn != null)
				this.streamIn.close();
			if (this.streamOut != null)
				this.streamOut.close();
		}
	}
}
