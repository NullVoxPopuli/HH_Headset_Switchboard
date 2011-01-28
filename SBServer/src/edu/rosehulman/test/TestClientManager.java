package edu.rosehulman.test;


import java.util.ArrayList;

import org.junit.*;
import edu.rosehulman.Client;
import edu.rosehulman.ClientManager;
import edu.rosehulman.exceptions.ClientNotFound;

/**
 * TODO Put here a description of what this class does.
 *
 * @author lprestonsegoiii.
 *         Created Jan 27, 2011.
 */
public class TestClientManager
{
	private ClientManager	clientManager = new ClientManager(new ArrayList<Client>());;
	
	String[] ipAddresses = new String[]{"localhost", "127.0.0.1", "127.0.0.2"};
	String[] compNames = new String[]{"comp1", "comp2", "comp3"};
	String[] aliases = new String[]{"alias1", "alias2", "alias3"};
	int numberOfClients = this.ipAddresses.length;

	/**
	 * Before each test
	 *
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception
	{		
	}

	/**
	 * After each test
	 *
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception
	{
	}
	
	public void testAddingOfClients()
	{
		for(int i = 0; i < this.numberOfClients; i++)
		{
			ClientManager.addNewClient(this.ipAddresses[i], this.compNames[i], this.aliases[i]);
		}
		
	}
	
	public void testRetrievingClient() throws ClientNotFound{
		for(int i = 0; i < this.numberOfClients; i++)
		{
			assert(ClientManager.getClient(this.ipAddresses[i]).equals(ClientManager.getClient(this.compNames[i])) &&
					ClientManager.getClient(this.compNames[i]).equals(ClientManager.getClient(this.aliases[i])));
		}
	}


}
