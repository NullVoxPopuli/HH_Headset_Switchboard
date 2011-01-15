package edu.rosehulman;


import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Vector;

import javax.media.Format;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.rtp.Participant;
import javax.media.rtp.RTPManager;
import javax.media.rtp.ReceiveStream;
import javax.media.rtp.ReceiveStreamListener;
import javax.media.rtp.SessionListener;
import javax.media.rtp.event.NewParticipantEvent;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import javax.media.rtp.event.SessionEvent;

import com.sun.media.rtp.RecvSSRCInfo;

public class RTPConnectionManager implements ReceiveStreamListener, SessionListener, AssociationListener {
	public static final Format[] FORMATS = new Format[] { new AudioFormat(
			AudioFormat.MPEG_RTP, 48000, 16, 2)};
	public static final ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(
			ContentDescriptor.RAW_RTP);

	private RTPManager mgr;
	private Hashtable<Long, DataSource> ssrcToStream = new Hashtable<Long, DataSource>();

	/**
	 * Creates the Manager that will be used to control the session
	 * @param address
	 * @param port
	 * @param ttl
	 * @param listener
	 * @return
	 */
	public RTPManager createManager(String address, int port, int ttl) {
		
		this.mgr = RTPManager.newInstance();
		this.mgr.addFormat(FORMATS[0], 18);
		
		this.mgr.addReceiveStreamListener(this);
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

	@Override
	public void update(ReceiveStreamEvent event) {	      
         if (event instanceof NewReceiveStreamEvent)
         {
             DataSource dsource = null;
             ReceiveStream stream = null;
             
             try
             {
                 // get a handle over the new incoming data stream
                 stream =((NewReceiveStreamEvent)event).getReceiveStream();
                 dsource = stream.getDataSource();
                 
                 //Associate SSRC to data stream
                 long ssrc = stream.getSSRC();
                 if (Switchboard.DEBUG) System.out.println("Recieving new incoming data stream, associating with SSRC " + ssrc);
                 ssrcToStream.put(ssrc, dsource);
                 
                 //Old code for adding to client manager, needs updating
    	         if (dsource != null) ClientManager.addNewClientWithStream(dsource);
                 
             } catch (Exception e) {
                 System.err.println("NewReceiveStreamEvent exception " 
                                    + e.getMessage());
                 e.printStackTrace();
                 return;
             }
         }
         else{
        	 System.out.println("Don't know what to do with this: " + event.getParticipant());
         }
	}

	@Override
	public void update(SessionEvent event) {
		if (event instanceof NewParticipantEvent)
		{
			Participant participant = ((NewParticipantEvent) event).getParticipant();
			Vector<RecvSSRCInfo> streams = participant.getStreams();
			if (!streams.isEmpty())
			{
				RecvSSRCInfo ssrcInfo = (RecvSSRCInfo) streams.get(0);
				//byte[] ssrcbytes = intToByteArray(ssrcInfo.getSSRC());
				System.out.println("Data sending user recognized: " + participant.getCNAME() + " with SSRC " + ssrcInfo.getSSRC());
			}
		}
	}
	
	public static final byte[] intToByteArray(int value) {
		return new byte[]{
		(byte)(value >>> 24), (byte)(value >> 16 & 0xff), (byte)(value >> 8 & 0xff), (byte)(value & 0xff) };
		}

	@Override
	public void newAssociation(String ip, long ssrc) {
		System.out.println("New source detected. IP: " + ip + " | SSRC: " + ssrc);
	}
}
