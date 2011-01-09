package edu.rosehulman;

import javax.media.Player;

public class Client {

	private Channel audience;
	private String name;
	private String macAddress;
	private String port;
	private Player rtpPlayer;

	/**
	 * The Audience is the list of clients that a client can talk to.
	 *
	 * @return the list of clients that this client can talk to.
	 */
	public Channel getAudience() {
		return this.audience;
	}

	/**
	 * 
	 * The Audience is the list of clients that a client can talk to.
	 *
	 * @param audience - a list of clients that this client will be able to talk to.
	 */
	public void setAudience(Channel audience) {
		this.audience = audience;
	}

	/**
	 * The name of this client. This is set by the administrator at runtime.
	 * If there is a save config, name will be set by this program upon
	 * start up.
	 * 
	 * @return the name.
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name of this client.  Any string is acceptable.
	 * This is just a way to be more human friendly rather than
	 * displaying a bunch of numbers for a client.
	 * 
	 * @param name - A string identifying the client.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Normally MAC addresses are formatted 0a:1b:2c:3d:4e:5f.
	 * But since the colons are only to help humans read the address better, internally,
	 * the colons are not needed.
	 * 
	 * @return the client's MAC address formatted without spaces. Example: 0a1b2c3d4e5f
	 */
	public String getMacAddress() {
		return this.macAddress;
	}

	/**
	 * 
	 * Normally MAC addresses are formatted 0a:1b:2c:3d:4e:5f.
	 * But since the colons are only to help humans read the address better, internally,
	 * the colons are not needed.
	 *
	 * @param macAddress - MAC address should be formatted 0a1b2c3d4e5f
	 */
	public void setMacAddress(String macAddress) {
		this.macAddress = macAddress;
	}
	
	/**
	 * 
	 * Internally the MAC address is stored without colons (0a1b2c3d4e5f)
	 * This method returns the MAC address with the colons (0a:1b:2c:3d:4e:5f)
	 *
	 * @return a human readable version of the MAC address formatted: 0a:1b:2c:3d:4e:5f
	 */
	public String getHumanMacAddress(){
		String result = "";
		for (byte i = 0; i < this.macAddress.length(); i++)
		{
			if (i % 2 == 0) result.concat(":");
			result.concat(String.valueOf(this.macAddress.charAt(i)));
		}
		
		return result;
	}

	public String getPort() {
		return this.port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	/**
	 * 
	 * Constructor for a Client.
	 * Creates blank Channel
	 *
	 * @param name - the name identifying this client
	 * @param mac - the MAC address of this client. Formatted 0a1b2c3d4e5f.
	 * @param port
	 */
	public Client(String name, String mac, String port) {
		this(new Channel(), name, mac, port);
	}
	
	/**
	 * 
	 * Constructor for a client
	 *
	 * @param audience 
	 * @param name - the name identifying this client
	 * @param mac - the MAC address of this client. Formatted 0a1b2c3d4e5f
	 * @param port
	 */
	public Client(Channel audience, String name, String mac, String port) {
		this.audience = audience;
		this.name = name;
		this.macAddress = mac;
		this.port = port;
	}

	/**
	 * 
	 * Constructor for a client.
	 * Directly sets the RTPPlayer
	 *
	 * @param RTPPlayer - an RTPPlayer object.
	 */
	public Client(Player RTPPlayer) {
		this.rtpPlayer = RTPPlayer;
	}

	/**
	 * 
	 * Removes a client from the list of clients that this client can talk to.
	 *
	 * @param c - a Client
	 */
	public void removeClientFromChannel(Client c) {
		if(this.audience.contains(c))
			this.audience.remove(c);
	}

	/**
	 * 
	 * Adds a client to the list of clients that this client can talk to.
	 *
	 * @param client - a Client
	 */
	public void addToAudience(Client client) {
		this.audience.add(client);		
	}

}
