package edu.rosehulman.control;

import edu.rosehulman.control.ControlListener.ControlServerClientThread;

/**
 * Messages that can be send to clients
 *
 * @author lprestonsegoiii.
 *         Created Jan 25, 2011.
 */
public class Messages
{
	
	/**
	 * 
	 * Sends the list of clients formatted as:
	 *  id	ip	comp_name	given_name
	 *
	 * @param ID - of the client that we are sending to.
	 */
	public static void sendListOfClients(int ID)
	{
		String listOfUsers = "";
		
		ControlListener.getControlClients()[ID].sendToAssociatedControlClient(listOfUsers);
	}
	
	public static void sendMessage(int ID, String msg)
	{
		ControlListener.getControlClients()[ID].sendToAssociatedControlClient(msg);
	}

	/**
	 * sends help to the requesting control client
	 *
	 * @param ID - the id of the user requesting help
	 */
	public static void sendHelp(int ID)
	{
		ControlServerClientThread[] controlClients = ControlListener.getControlClients();
		controlClients[ID].sendToAssociatedControlClient("" +
				"--------------------------------------------------------\n" +
				"--------------- Switchboard Control Help ---------------\n" +
				"--------------------------------------------------------\n" +
				"\n");
		controlClients[ID].sendToAssociatedControlClient("" +
				"Commands:\n" +
				"\t atc \t\t Add to client\n" +
				"\t\t\t\t e.g.: /atc destinationIP clientIP1 clientIP2 ...\n" +
				"\t rfc \t\t Remove from client\n" +
				"\t\t\t\t e.g.: /rfc destinationIP clientIP1 clientIP2 ...\n" +
				"");
		
	}
	
	public static void sendToAll(String msg)
	{
		for (int i = 0; i < ControlListener.getControlClientCount(); i++)
			ControlListener.getControlClients()[i].sendToAssociatedControlClient(msg);
	}
}
