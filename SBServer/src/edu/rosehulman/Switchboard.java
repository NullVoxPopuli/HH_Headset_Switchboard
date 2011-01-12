package edu.rosehulman;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

import edu.rosehulman.net_control.ControlListener;


public class Switchboard {
	public static final boolean DEBUG = true;
	
	private static ClientManager manager;
	private static RTPConnector rtpConnector = new RTPConnector();
	
	// these are things that can change real time

	public static String serverIP = "0.0.0.0"; // backup incase the OS can't figure out its IP
	public static int serverPort = 13378;
	
	public static void main(String[] args){
		
		try {
			serverIP = InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("We will need a way to handle this error easily for the user");
		}
		// Since Client Manager is a singleton, we shoud make it static, so we don't have 
		// multiple copies.
		//ArrayList<Client> clients = getClients();
		//manager = new ClientManager(clients);
		
		// Start Network Interface
		Thread netFace = new Thread(new ControlListener(), "Switchboard Control Server");
		netFace.start();
		
		// init our RTP Connector
		rtpConnector.init();
		
		
		// begin rtp stream stuff
		rtpConnector.start();
	}

	private static ArrayList<Client> getClients() {
		// TODO Auto-generated method stub
		return null;
	}
}
