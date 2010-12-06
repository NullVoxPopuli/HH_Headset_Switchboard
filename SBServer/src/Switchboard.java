import java.util.ArrayList;


public class Switchboard {
	
	private static ClientManager manager;
	
	public static void main(String[] args){
		ArrayList<Client> clients = getClients();
		manager = new ClientManager(clients);
	}

	private static ArrayList<Client> getClients() {
		// TODO Auto-generated method stub
		return null;
	}
}
