package edu.rosehulman;

import javax.media.Player;

public class Client {

	private Channel audience;
	private String name;
	private String ip;
	private String port;
	private Player rtpPlayer;

	public Channel getAudience() {
		return audience;
	}

	public void setAudience(Channel audience) {
		this.audience = audience;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	public Client(String name, String ip, String port) {
		this(new Channel(), name, ip, port);
	}
	
	public Client(Channel audience, String name, String ip, String port) {
		this.audience = audience;
		this.name = name;
		this.ip = ip;
		this.port = port;
	}

	public Client(Player RTPPlayer) {
		this.rtpPlayer = RTPPlayer;
	}

	public void removeClientFromChannel(Client c) {
		if(audience.contains(c))
			audience.remove(c);
	}

	public void addToAudience(Client client) {
		this.audience.add(client);		
	}

}
