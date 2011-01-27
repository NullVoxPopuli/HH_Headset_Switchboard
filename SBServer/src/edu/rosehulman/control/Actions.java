package edu.rosehulman.control;

import edu.rosehulman.Client;
import edu.rosehulman.ClientManager;
import edu.rosehulman.exceptions.ClientNotFound;
import edu.rosehulman.exceptions.MemberIsNotHeardByClient;

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
	 *            audience members too.
	 * @param audience
	 *            - the array of IPs that that are to be added to the client
	 */
	public static void addToClient(int controlClientID, String target, String[] audience)
	{
		addToOrRemoveFromClient(controlClientID, target, audience, true);

	}

	/**
	 * This is a helper method of addToClient and removeFromClient. Since both
	 * methods do almost the same thing (as far as validation goes, it makes
	 * sense to keep all the logic in one method.
	 * 
	 * @param controlClientID
	 *            - the control client that issued the command.
	 * @param target
	 *            - the client that we are modifying
	 * @param audience
	 *            - the list of clients that are to be added or removed from the
	 *            target client.
	 * @param addingTo
	 *            - true if we are adding to the target client, false if
	 *            removing from the target client
	 */
	private static void addToOrRemoveFromClient(int controlClientID, String target, String[] audience, boolean addingTo)
	{
		Client targetClient;
		String clientsThatWereSuccessful = "";
		String addOrRemove = addingTo ? "added" : "removed";
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
				Messages.sendMessage(
						controlClientID,
						"A member could could not be " + addOrRemove + " to client.");
				if (!clientsThatWereSuccessful.isEmpty())
					Messages.sendMessage(
							controlClientID,
							"However, the following members were " + addOrRemove + ": " + clientsThatWereSuccessful);
				e.printStackTrace();

				return;
			}

			if (addingTo)
			{
				targetClient.addToAudience(clientThatCanHearTheTargetClient);
				clientsThatWereSuccessful.concat(" " + member);

			}
			else
			{
				try
				{
					targetClient.removeFromAudience(clientThatCanHearTheTargetClient);
					clientsThatWereSuccessful.concat(" " + member);

				}
				catch (MemberIsNotHeardByClient exception)
				{
					Messages.sendMessage(controlClientID, "Member, " + member + " is not heard by + " + target);
					exception.printStackTrace();
				}
			}
			
			if (!clientsThatWereSuccessful.isEmpty())
				Messages.sendMessage(controlClientID, "The following members were " + addOrRemove + ":\n" +
						clientsThatWereSuccessful);
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

	/**
	 * 
	 * This method assumes that none of the parameters that are passed are null
	 * 
	 * @param controlClientID
	 *            - the ID of the control client issuing this command
	 * @param target
	 *            - the client's IP, computer name, or alias that we are adding
	 *            audience members too.
	 * @param membersToRemove
	 *            - the array of IPs that that are to be added to the client
	 */
	public static void removeFromClient(int controlClientID, String target, String[] membersToRemove)
	{
		addToOrRemoveFromClient(controlClientID, target, membersToRemove, false);

	}
}
