package edu.rosehulman;
import java.util.ArrayList;

import javax.media.Player;

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
}
