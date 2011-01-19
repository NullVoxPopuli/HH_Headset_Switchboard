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
import javax.media.Processor;
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
			AudioFormat.MPEG_RTP, 48000, 16, 2)};
	public static final ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(
			ContentDescriptor.RAW_RTP);

	private RTPManager mgr;
	private Hashtable<Long, String> ssrcToIp = new Hashtable<Long, String>();
	private ArrayList<DataSource> sources = new ArrayList<DataSource>();
	private DataSink sink = null;

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

//	@Override
//	public void update(ReceiveStreamEvent event) {	      
////         if (event instanceof NewReceiveStreamEvent)
////         {
////             DataSource dsource = null;
////             ReceiveStream stream = null;
////             
////             try
////             {
////                 // get a handle over the new incoming data stream
////                 stream =((NewReceiveStreamEvent)event).getReceiveStream();
////                 dsource = stream.getDataSource();
////                 
////                 //Associate SSRC to data stream
////                 long ssrc = stream.getSSRC();
////                 if (Switchboard.DEBUG) System.out.println("Recieving new incoming data stream, associating with SSRC " + ssrc);
////                 this.ssrcToStream.put(ssrc, dsource);
////                 
////                 
////             } catch (Exception e) {
////                 System.err.println("NewReceiveStreamEvent exception " 
////                                    + e.getMessage());
////                 e.printStackTrace();
////                 return;
////             }
////         }
////         else{
////        	 System.out.println("Don't know what to do with this: " + event.getParticipant());
////         }
//	}

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
            MediaLocator ml = null;
            
			Participant participant = ((NewParticipantEvent) event).getParticipant();
			Vector<RecvSSRCInfo> streams = participant.getStreams();
			if (!streams.isEmpty())
			{
				dsource = streams.get(0).getDataSource();
				RecvSSRCInfo ssrcInfo = (RecvSSRCInfo) streams.get(0);
				cname = participant.getCNAME();
				ssrc = ssrcInfo.getSSRC();
				//byte[] ssrcbytes = intToByteArray(ssrcInfo.getSSRC());
				if (Switchboard.DEBUG) System.out.println("Data sending user recognized: " + cname + " with SSRC " + ssrc);
				String ip = this.ssrcToIp.get(ssrc); 
   	         	if (dsource != null) ClientManager.addNewClientWithStream(dsource, ip, cname);
   	         	
   	         	try {
   	         		ml = new MediaLocator("rtp://"+ip+":"+13380+"/audio");
					this.sources.add(dsource);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					System.err.println("Could not create send stream");
					e.printStackTrace();
				}
				DataSource[] sources = new DataSource[this.sources.size()];
				this.sources.toArray(sources);
				
				try {
					//DataSource finalSource = Manager.createMergingDataSource(sources);
					this.sink = Manager.createDataSink(dsource, ml);
					this.sink.open();
					this.sink.start();
					if (Switchboard.DEBUG) System.out.println("Sending audio to client: " + cname);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();}
				
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
