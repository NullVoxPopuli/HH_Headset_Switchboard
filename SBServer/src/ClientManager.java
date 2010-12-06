import java.util.ArrayList;

public class ClientManager {

	private ArrayList<Client> clients;

	public ArrayList<Client> getClients() {
		return clients;
	}

	public ClientManager(ArrayList<Client> clients) {
		this.clients = clients;
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
}
