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

	public void removeClient(Client c) {
		clients.remove(c);
		for (Client d : clients) {
			d.removeClientFromChannel(c);
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
		String macAddress = "";


		// DataSource.rtpcontrol.stream.address (Inet4Address)

		clients.add(new Client(dsource, macAddress));
		
	}

	/**
	 * Verifies that the requested mac address isn't in use.
	 *
	 * @param macAddress formatted 0a1b2c3d4e5f
	 * @return true if the MAC has not been used before. false otherwise.
	 */
	private static boolean isMacInUse(String macAddress)
	{
		for (Client c : clients)
		{
			if(c.getMacAddress().equals(macAddress)) return false;
		}
		return true;
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
}
