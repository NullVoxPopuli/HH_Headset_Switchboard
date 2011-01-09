package edu.rosehulman;

import javax.media.Player;
import javax.media.protocol.DataSource;

/**
 * Clients represent the people who will be talking to the server, and 
 * who will be receiving streams from other clients.
 *
 * @author Ben Kehm, L. Preston Sego III, Kenny Skaggs, Bennie Waters
 *         Created Jan 9, 2011.
 */
public class Client {

	private Channel audience;
	private String name;
	private String macAddress;
	private Object audioStream;

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
	 * Sets the field called 'audioStream' to the given value.
	 * @param audioStream The audioStream to set.
	 */
	public void setAudioStream(Object audioStream)
	{
		this.audioStream = audioStream;
	}

	/**
	 * Returns the value of the field called 'audioStream'.
	 * @return Returns the audioStream.
	 */
	public Object getAudioStream()
	{
		return this.audioStream;
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

	/**
	 * 
	 * Constructor for a Client.
	 * Creates blank Channel
	 *
	 * @param name - the name identifying this client
	 * @param mac - the MAC address of this client. Formatted 0a1b2c3d4e5f.
	 */
	public Client(String name, String mac) {
		this(new Channel(), name, mac);
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
	public Client(Channel audience, String name, String mac) {
		this.audience = audience;
		this.name = name;
		this.macAddress = mac;
	}

	/**
	 * 
	 * Constructor for a client.
	 * Directly sets the stream for which this client is associated with.
	 *
	 * @param audioStream - an audio stream object.
	 */
	public Client(Object audioStream) {
		this.setAudioStream(audioStream);
	}

	/**
	 * Creates a client using a datasource and a MAC address to identify with.
	 *
	 * @param audioStream - Stream that the audio comes fram
	 * @param macAddress - MAC address that identifies the device that this client associates with
	 */
	public Client(Object audioStream, String macAddress)
	{
		this.audioStream = audioStream;
		this.macAddress = macAddress;
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
