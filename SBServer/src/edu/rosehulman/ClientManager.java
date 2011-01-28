package edu.rosehulman;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Vector;

import javax.media.Control;
import javax.media.Player;
import javax.media.protocol.DataSource;
import javax.media.rtp.RTPControl;
import javax.media.rtp.rtcp.SourceDescription;

import com.sun.media.rtp.RTPRemoteSourceInfo;
import com.sun.media.rtp.RecvSSRCInfo;

import edu.rosehulman.exceptions.ClientNotFound;
import edu.rosehulman.exceptions.MemberIsNotHeardByClient;

public class ClientManager {

	private static ArrayList<Client> clients = new ArrayList<Client>();

	public static ArrayList<Client> getClients() {
		return clients;
	}

	public ClientManager(ArrayList<Client> clients) {
		ClientManager.clients = clients;
	}


	public void setupChannels(boolean[][] c2cmatrix) {
		for (Client c : clients) {
			c.getAudience().clear();
		}
		for (int i = 0; i < c2cmatrix.length; i++) {
			for (int j = 0; j < c2cmatrix[i].length; j++) {
				if (c2cmatrix[i][j]) {
					clients.get(i).addToAudience(clients.get(j));
				}
			}
		}
	}

	public void addClient(Client c) {
		clients.add(c);
	}

	public void removeClient(Client c) throws MemberIsNotHeardByClient {
		clients.remove(c);
		for (Client d : clients) {
			d.removeFromAudience(c);
		}
	}

	public static void addNewClientWithStream(Player RTPPlayer) {
		clients.add(new Client(RTPPlayer));
	}

	/**
	 * Add a client with a streamto the list of clients
	 *
	 * @param dsource
	 * @param remoteParticipants
	 */
	public static void addNewClientWithStream(DataSource dsource)
	{
		if(Switchboard.DEBUG) System.out.println("Adding Client to the ClientManager...");

		clients.add(new Client(dsource));
		
	}

	/**
	 * Returns the client that has a matching IP, computer name, or alias
	 *
	 * @param name - could be computer name, IP address, or alias
	 * @return - client if found
	 * @throws ClientNotFound - thrown if no client is found
	 */
	public static Client getClient(String name) throws ClientNotFound
	{
		for (Client curClient : clients)
		{
			if (curClient.getAlias().equals(name) ||
					curClient.getComputerName().equals(name) ||
					curClient.getIpAddress().equals(name))
			{
				return curClient;
			}
		}
		
		throw new ClientNotFound();
	}

	/**
	 * creates a client from scratch and adds it to the client manager
	 *
	 * @param ipAddress
	 * @param computerName
	 * @param alias
	 */
	public static void addNewClient(String ipAddress, String computerName, String alias)
	{
		Client newClient = new Client(ipAddress, computerName, alias);
	}
}
