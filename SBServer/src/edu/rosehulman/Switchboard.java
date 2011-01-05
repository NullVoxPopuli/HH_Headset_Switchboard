package edu.rosehulman;
import java.util.ArrayList;


public class Switchboard {
	public static final boolean DEBUG = true;
	
	private static ClientManager manager;
	private static RTPConnector rtpConnector = new RTPConnector();
	
	// these are things that can change real time
	public static String serverIP = "137.112.104.107";
	public static int serverPort = 13378;
	
	public static void main(String[] args){
		// Since Client Manager is a singleton, we shoud make it static, so we don't have 
		// multiple copies.
		//ArrayList<Client> clients = getClients();
		//manager = new ClientManager(clients);
		
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
