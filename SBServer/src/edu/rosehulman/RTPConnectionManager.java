package edu.rosehulman;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.NoDataSinkException;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.rtp.Participant;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SessionListener;
import javax.media.rtp.event.NewParticipantEvent;
import javax.media.rtp.event.SessionEvent;

import com.sun.media.rtp.RecvSSRCInfo;

public class RTPConnectionManager implements SessionListener, AssociationListener {
	public static final Format[] FORMATS = new Format[] { new AudioFormat(
			AudioFormat.DVI_RTP)};
	public static final ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(
			ContentDescriptor.RAW_RTP);

	private RTPManager mgr;
	private Hashtable<Long, String> ssrcToIp = new Hashtable<Long, String>();
	private Hashtable<String, Object> ipToDataSink = new Hashtable<String, Object>();

	/**
	 * Creates the RTPManager that will be used to receive incoming connections from remote clients
	 * @param address - The IP address to use when receiving incoming audio
	 * @param port - The port to use when receiving incoming audio
	 * @return
	 */
	public RTPManager createManager(String address, int port) {
		
		this.mgr = RTPManager.newInstance();
		this.mgr.addFormat(FORMATS[0], 18);
		
		this.mgr.addSessionListener(this);
		
		try {
			address = "137.112.104.179";
			RTPSocketAdapter socketAdapter = new RTPSocketAdapter(InetAddress.getByName(address), port);
			socketAdapter.AddAssociationListener(this);
			this.mgr.initialize(socketAdapter);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this.mgr;
	}

	/**
	 * Callback required by the SessionListener interface. Called when a new participant has been recognized by the session
	 * @param event - incoming SessionEvent that describes what happened and gives related data
	 */
	@Override
	public void update(SessionEvent event){
		if (event instanceof NewParticipantEvent)
		{
            DataSource dsource = null;
            String cname;
            Long ssrc;
            
			Participant participant = ((NewParticipantEvent) event).getParticipant();
			Vector<RecvSSRCInfo> streams = participant.getStreams();
			if (!streams.isEmpty())
			{
				dsource = streams.get(0).getDataSource();
				RecvSSRCInfo ssrcInfo = (RecvSSRCInfo) streams.get(0);
				cname = participant.getCNAME();
				ssrc = ssrcInfo.getSSRC();
				if (Switchboard.DEBUG) System.out.println("Data sending user recognized: " + cname + " with SSRC " + ssrc);
				String ip = this.ssrcToIp.get(ssrc);
   	         	if (dsource != null) ClientManager.addNewClient(ip, cname, cname, dsource);
   	         	try {
					ipToDataSink.put(ip, Manager.createDataSink(dsource, new MediaLocator("rtp://" + ip + ":" + 13380 + "/audio")));
				} catch (NoDataSinkException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			SendAllSourcesToAllClients();
		}
	}
	
	private void SendAllSourcesToAllClients()
	{
        ArrayList<Client> clients = ClientManager.getClients();
        if (clients.size() < 2) return;
        DataSource[] sources = new DataSource[clients.size()]; 
        
        //Collect all the sources
        int index = 0;
        for (Client client : clients)
        {
        	sources[index] = Manager.createCloneableDataSource((DataSource) client.getAudioStream());
        	index++;
        }
        
        
        //Send all the sources to everyone
        for (Client client : clients)
        {
        	MediaLocator ml = new MediaLocator("rtp://" + client.getIpAddress() + ":" + 13380 + "/audio");
        	if (Switchboard.DEBUG_NETWORK) System.out.println("Sending audio to " + client.getIpAddress() + ":13380");
        	DataSink sink;
			try {
//		        DataSource finalSource = Manager.createMergingDataSource(sources);
//		        ProcessorModel model = new ProcessorModel(finalSource, FORMATS, CONTENT_DESCRIPTOR);
//				Processor mediaProcessor = Manager.createRealizedProcessor(model);
//	        	mediaProcessor.start();
//				sink.setSource(mediaProcessor.getDataOutput());
				
				DataSource finalSource = new MergingDataSource(sources);
	        	sink = (DataSink) ipToDataSink.get(client.getIpAddress());
	        	sink.setSource(finalSource);
	        	sink.open();
	        	sink.start();
	        	ipToDataSink.remove(client.getIpAddress());
	        	ipToDataSink.put(client.getIpAddress(), sink);
	        	System.out.println("----------------------SERVER SENDING");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
	}

	/**
	 * Callback required by the AssociationListener interface. Called when a packet is seen with a previously unseen SSRC
	 * @param ip - The IP address the SSRC came from
	 * @param ssrc - The SSRC given by the packet
	 */
	@Override
	public void newAssociation(String ip, long ssrc) {
		if (Switchboard.DEBUG) System.out.println("New source detected. IP: " + ip + " | SSRC: " + ssrc);
		this.ssrcToIp.put(ssrc, ip);
	}
}
