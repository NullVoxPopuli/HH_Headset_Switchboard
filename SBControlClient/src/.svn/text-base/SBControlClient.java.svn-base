/*
 * Nearly all of the code here is from
 * http://pirate.shu.edu/~wachsmut/Teaching/CSAS2214
 * /Virtual/Lectures/chat-client-server.html
 */
import java.net.*;
import java.io.*;

public class SBControlClient implements Runnable
{
	private Socket				socket		= null;
	private Thread				thread		= null;
	private DataInputStream		console		= null;
	private DataOutputStream	streamOut	= null;
	private ChatClientThread	client		= null;

	public SBControlClient(String serverName, int serverPort)
	{
		System.out.println("Establishing connection. Please wait ...");
		try
		{
			this.socket = new Socket(serverName, serverPort);
			System.out.println("Connected: " + this.socket);
			start();
		}
		catch (UnknownHostException uhe)
		{
			System.out.println("Host unknown: " + uhe.getMessage());
		}
		catch (IOException ioe)
		{
			System.out.println("Unexpected exception: " + ioe.getMessage());
		}
	}

	@Override
	public void run()
	{
		while (this.thread != null)
		{
			try
			{
				String input = this.console.readLine();
			
				this.streamOut.writeUTF(input);
				this.streamOut.flush();
				
			}
			catch (IOException ioe)
			{
				System.out.println("Sending error: " + ioe.getMessage());
				stop();
			}
		}
	}

	public void processMessage(String msg)
	{
		if (msg.equals(".bye"))
		{
			System.out.println("Good bye. Press RETURN to exit ...");
			stop();
		}
		else
			System.out.println(msg);
	}

	public void start() throws IOException
	{
		this.console = new DataInputStream(System.in);
		this.streamOut = new DataOutputStream(this.socket.getOutputStream());
		if (this.thread == null)
		{
			this.client = new ChatClientThread(this, this.socket);
			this.thread = new Thread(this);
			this.thread.start();
		}
	}

	public void stop()
	{
		if (this.thread != null)
		{
			this.thread.stop();
			this.thread = null;
		}
		try
		{
			if (this.console != null)
				this.console.close();
			if (this.streamOut != null)
				this.streamOut.close();
			if (this.socket != null)
				this.socket.close();
		}
		catch (IOException ioe)
		{
			System.out.println("Error closing ...");
		}
		this.client.close();
		this.client.stop();
	}

	public static void main(String args[])
	{
		SBControlClient client = null;
		client = new SBControlClient("137.112.104.144", 13380);
	}

	public class ChatClientThread extends Thread
	{
		private Socket			socket		= null;
		private SBControlClient	client		= null;
		private DataInputStream	streamIn	= null;

		public ChatClientThread(SBControlClient _client, Socket _socket)
		{
			this.client = _client;
			this.socket = _socket;
			open();
			start();
		}

		public void open()
		{
			try
			{
				this.streamIn = new DataInputStream(
						this.socket.getInputStream());
			}
			catch (IOException ioe)
			{
				System.out.println("Error getting input stream: " + ioe);
				this.client.stop();
			}
		}

		public void close()
		{
			try
			{
				if (this.streamIn != null)
					this.streamIn.close();
			}
			catch (IOException ioe)
			{
				System.out.println("Error closing input stream: " + ioe);
			}
		}

		@Override
		public void run()
		{
			while (true)
			{
				try
				{
					this.client.processMessage(this.streamIn.readUTF());
				}
				catch (IOException ioe)
				{
					System.out.println("Listening error: " + ioe.getMessage());
					this.client.stop();
				}
			}
		}
	}
}
