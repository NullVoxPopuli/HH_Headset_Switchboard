package edu.rosehulman.control;

import edu.rosehulman.Client;
import edu.rosehulman.ClientManager;
import edu.rosehulman.exceptions.ClientNotFound;

/**
 * TODO Put here a description of what this class does.
 * 
 * @author lprestonsegoiii. Created Jan 27, 2011.
 */
public class Actions
{
	/**
	 * 
	 * This method assumes that none of the parameters that are passed are null
	 * 
	 * @param controlClientID
	 *            - the ID of the control client issuing this command
	 * @param target
	 *            - the client's IP, computer name, or alias that we are adding
	 *            adience members too.
	 * @param audience
	 *            - the array of IPs that that are to be added to the client
	 */
	public static void addToClient(int controlClientID, String target, String[] audience)
	{
		Client targetClient;
		String addedClients = "";
		try
		{
			targetClient = ClientManager.getClient(target);
		}
		catch (ClientNotFound e)
		{
			Messages.sendMessage(
					controlClientID, "Target Client could not be found");
			e.printStackTrace();
			return;
		}

		for (String member : audience)
		{
			Client clientThatCanHearTheTargetClient;
			try
			{
				clientThatCanHearTheTargetClient = ClientManager.getClient(member);
			}
			catch (ClientNotFound e)
			{
				Messages.sendMessage(controlClientID, "A member could could not be added to client.");
				if (!addedClients.isEmpty())
					Messages.sendMessage(controlClientID, "However, the following members were added: " + addedClients);
				e.printStackTrace();
				
				return;
			}
			
			targetClient.addToAudience(clientThatCanHearTheTargetClient);
			addedClients.concat(" " + member);
		}

	}

	/**
	 * 
	 * checks to see if the IP address is formatted correctly
	 * 
	 * @param potentionIPAddress
	 *            - candidate address
	 * @return - true if in correct format
	 */
	public static boolean validateIPFormat(String potentionIPAddress)
	{
		String[] parts = potentionIPAddress.split(".");
		for (String s : parts)
		{
			int i = Integer.parseInt(s);
			if (i < 0 || i > 255)
			{
				return false;
			}
		}
		return true;
	}
}
