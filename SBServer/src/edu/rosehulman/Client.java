package edu.rosehulman;

import javax.media.Player;
import javax.media.protocol.DataSource;

import edu.rosehulman.exceptions.MemberIsNotHeardByClient;

/**
 * Clients represent the people who will be talking to the server, and who will
 * be receiving streams from other clients.
 * 
 * @author Ben Kehm, L. Preston Sego III, Kenny Skaggs, Bennie Waters Created
 *         Jan 9, 2011.
 */
public class Client
{

	private Channel	audience;
	private String	computerName;
	private String	ipAddress;
	private String	alias;
	private Object	audioStream;

	/**
	 * The Audience is the list of clients that a client can talk to.
	 * 
	 * @return the list of clients that this client can talk to.
	 */
	public Channel getAudience()
	{
		return this.audience;
	}

	/**
	 * 
	 * The Audience is the list of clients that a client can talk to.
	 * 
	 * @param audience
	 *            - a list of clients that this client will be able to talk to.
	 */
	public void setAudience(Channel audience)
	{
		this.audience = audience;
	}

	/**
	 * Sets the field called 'audioStream' to the given value.
	 * 
	 * @param audioStream
	 *            The audioStream to set.
	 */
	public void setAudioStream(Object audioStream)
	{
		this.audioStream = audioStream;
	}

	/**
	 * Returns the value of the field called 'audioStream'.
	 * 
	 * @return Returns the audioStream.
	 */
	public Object getAudioStream()
	{
		return this.audioStream;
	}

	/**
	 * The name of this client. This is set by the administrator at runtime. If
	 * there is a save config, name will be set by this program upon start up.
	 * 
	 * @return the name.
	 */
	public String getAlias()
	{
		return this.alias;
	}

	/**
	 * Sets the name of this client. Any string is acceptable. This is just a
	 * way to be more human friendly rather than displaying a bunch of numbers
	 * for a client.
	 * 
	 * @param name
	 *            - A string identifying the client.
	 */
	public void setAlias(String name)
	{
		this.alias = name;
	}

	/**
	 * Returns the value of the field called 'computerName'.
	 * 
	 * @return Returns the computerName.
	 */
	public String getComputerName()
	{
		return this.computerName;
	}

	/**
	 * Sets the field called 'computerName' to the given value.
	 * 
	 * @param computerName
	 *            The computerName to set.
	 */
	public void setComputerName(String computerName)
	{
		this.computerName = computerName;
	}

	/**
	 * Returns the value of the field called 'ipAddress'.
	 * 
	 * @return Returns the ipAddress.
	 */
	public String getIpAddress()
	{
		return this.ipAddress;
	}

	/**
	 * Sets the field called 'ipAddress' to the given value.
	 * 
	 * @param ipAddress
	 *            The ipAddress to set.
	 */
	public void setIpAddress(String ipAddress)
	{
		this.ipAddress = ipAddress;
	}
	
	/**
	 * 
	 * Constructor for a Client. No audience members.
	 *
	 * @param ipAddress
	 * @param computerName
	 * @param alias
	 */
	public Client(String ipAddress, String computerName, String alias)
	{
		this.setIpAddress(ipAddress);
		this.setComputerName(computerName);
		this.setAlias(alias);
	}


	/**
	 * 
	 * Constructor for a client. Directly sets the stream for which this client
	 * is associated with.
	 * 
	 * @param audioStream
	 *            - an audio stream object.
	 */
	public Client(Object audioStream)
	{
		this.setAudioStream(audioStream);
	}


	/**
	 * TODO Put here a description of what this constructor does.
	 *
	 * @param audIp
	 * @param audCname
	 */
	public Client(String audIp, String audCname)
	{
		this.ipAddress = audIp;
		this.computerName = audCname;
	}

	/**
	 * TODO Put here a description of what this constructor does.
	 *
	 * @param chan
	 * @param name
	 * @param ip
	 */
	public Client(Channel chan, String name, String ip)
	{
		this.audience = chan;
		this.computerName = name;
		this.ipAddress = ip;
	}

	/**
	 * 
	 * Adds a client to the list of clients that this client can talk to.
	 * 
	 * @param client
	 *            - a Client
	 */
	public void addToAudience(Client client)
	{
		this.audience.add(client);
	}

	/**
	 * Removes a client from the list of people that can here THIS client.
	 * 
	 * @param audienceMember
	 *            - a pre-existing client that is to be removed from the list of
	 *            people that can hear THIS client
	 * @throws MemberIsNotHeardByClient
	 *             - if the audienceMember is not in the list of members for
	 *             this client.
	 */
	public void removeFromAudience(Client audienceMember) throws MemberIsNotHeardByClient
	{
		if (this.audience.contains(audienceMember))
			this.audience.remove(audienceMember);
		else
			throw new MemberIsNotHeardByClient();

	}

}
