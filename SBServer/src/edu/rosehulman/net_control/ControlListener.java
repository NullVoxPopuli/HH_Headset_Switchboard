package edu.rosehulman.net_control;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


import edu.rosehulman.Switchboard;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Jan 12, 2011.
 */
public class ControlListener implements Runnable
{

	private static boolean	hasNotRecievedQuitCommand = true;
	
	private static Socket	serverSocket;
	private static ServerSocket	controlServer;
	private static int	connectionStatus;
	
	private static BufferedReader in;
	private static PrintWriter	out;
	
	private static final int BEGIN_CONNECT = 0;
	private static final int CONNECTED = 1;
	private static final int DISCONNECTING = 2;
	private static final int DISCONNECTED = 3;
	private static final int IN_SESSION = 4;
	
	private static final String TERMINATE_SESSION = new Character((char)0).toString();
	
	private static StringBuffer toSend = new StringBuffer("");


	@Override
	public void run()
	{
		String currentMessage;
		 // handle packets and such.
	     while (hasNotRecievedQuitCommand ) {
	    	 
	    	 try {
		    	 // only update every 1/4 second.
				Thread.sleep(250);
			} catch (InterruptedException exception) {
				System.err.println("The control server thread was interrupted.");
			}
			
			switch (connectionStatus){
				case BEGIN_CONNECT :
					try{					
						 // add two for control communication
						controlServer = new ServerSocket(Switchboard.serverPort + 2);
						serverSocket = controlServer.accept();
						
						in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
						out = new PrintWriter(serverSocket.getOutputStream(), true);
						connectionStatus = CONNECTED;
					} catch (IOException e){
						System.err.println("Setting up the control server failed...");
						closeServer();
					}
					
					break;
				case CONNECTED:
					try {
						
						// send responses
						if (toSend.length() != 0){
							out.print(toSend);
							out.flush();
							connectionStatus = IN_SESSION;
						}
						
						// receive commands
						if (in.ready()){
							currentMessage = in.readLine();
							if(Switchboard.DEBUG)
								System.out.println(currentMessage);
							
							if ((currentMessage != null) && (currentMessage.length() != 0)){
								if(currentMessage.equals(TERMINATE_SESSION)){
									connectionStatus = DISCONNECTING;
								}else{
									handleCommand(currentMessage);
								}
							}
						}
						
					} catch (IOException e) {
						closeServer();
					}
					break;
				case DISCONNECTING:
					// send anything that hasn't yet been sent
					out.print(TERMINATE_SESSION);
					out.flush();
					// TODO: let the client know they are disconnecting from the server
					System.out.println("Disconnecting...");
					closeServer();
					break;
				default:
					break;

			}

	     }
	}

	/**
	 * Tidily shuts down the server
	 *
	 */
	private void closeServer()
	{
		connectionStatus = DISCONNECTED;
	     try {
	         if (serverSocket != null) {
	        	 serverSocket.close();
	        	 serverSocket = null;
	         }
	      }catch (IOException e) { serverSocket = null; }

	      try {
	         if (controlServer != null) {
	        	 controlServer.close();
	        	 controlServer = null;
	         }
	      }catch (IOException e) { controlServer = null; }

	      try {
	         if (in != null) {
	            in.close();
	            in = null;
	         }
	      }catch (IOException e) { in = null; }

	      if (out != null) {
	         out.close();
	         out = null;
	      }
	}
	
	/**
	 * Handles commands sent to the server
	 * TODO Put here a description of what this method does.
	 *
	 * @param message
	 */
	private void handleCommand(String message){
		
	}
}
