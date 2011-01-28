package edu.rosehulman;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.HashSet;

import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushSourceStream;
import javax.media.protocol.SourceTransferHandler;
import javax.media.rtp.OutputDataStream;
import javax.media.rtp.RTPConnector;


/**
 * An implementation of RTPConnector based on UDP sockets.
 */
public class RTPSocketAdapter implements RTPConnector {

    DatagramSocket dataSock;
    DatagramSocket ctrlSock;

    InetAddress addr;
    int port;
    AssociationListener listener;
    HashSet<Long> previousSSRCSet = new HashSet<Long>();

    SockInputStream dataInStrm = null, ctrlInStrm = null;
    SockOutputStream dataOutStrm = null, ctrlOutStrm = null;


    public RTPSocketAdapter(InetAddress addr, int port) throws IOException
    {
    	this(addr, port, 1);
    }

    public RTPSocketAdapter(InetAddress addr, int port, int ttl) throws IOException
    {
    	try
    	{
    		if (addr.isMulticastAddress())
    		{
    			this.dataSock = new MulticastSocket(port);
    			this.ctrlSock = new MulticastSocket(port+1);
    			((MulticastSocket)this.dataSock).joinGroup(addr);
    			((MulticastSocket)this.dataSock).setTimeToLive(ttl);
    			((MulticastSocket)this.ctrlSock).joinGroup(addr);
    			((MulticastSocket)this.ctrlSock).setTimeToLive(ttl);
    		}
    		else
    		{
    			this.dataSock = new DatagramSocket(port, InetAddress.getLocalHost());
    			this.ctrlSock = new DatagramSocket(port+1, InetAddress.getLocalHost());
    		}
    	}
    	catch (SocketException e)
    	{
    		throw new IOException(e.getMessage());
    	}

    	this.addr = addr;
    	this.port = port;
    }

    public void AddAssociationListener(AssociationListener listener)
    {
    	this.listener = listener;
    }
    
    /**
     * Returns an input stream to receive the RTP data. 
     */
    public PushSourceStream getDataInputStream() throws IOException {
	if (this.dataInStrm == null) {
	    this.dataInStrm = new SockInputStream(this.dataSock, this.addr, this.port);
	    this.dataInStrm.start();
	}
	return this.dataInStrm;
    }

    /**
     * Returns an output stream to send the RTP data.
     */
    public OutputDataStream getDataOutputStream() throws IOException {
	if (this.dataOutStrm == null)
	    this.dataOutStrm = new SockOutputStream(this.dataSock, this.addr, this.port);
	return this.dataOutStrm;
    }

    /**
     * Returns an input stream to receive the RTCP data.
     */
    public PushSourceStream getControlInputStream() throws IOException {
	if (this.ctrlInStrm == null) {
	    this.ctrlInStrm = new SockInputStream(this.ctrlSock, this.addr, this.port+1);
	    this.ctrlInStrm.start();
	}
	return this.ctrlInStrm;
    }

    /**
     * Returns an output stream to send the RTCP data.
     */
    public OutputDataStream getControlOutputStream() throws IOException {
	if (this.ctrlOutStrm == null)
	    this.ctrlOutStrm = new SockOutputStream(this.ctrlSock, this.addr, this.port+1);
	return this.ctrlOutStrm;
    }

    /**
     * Close all the RTP, RTCP streams.
     */
    public void close() {
	if (this.dataInStrm != null)
	    this.dataInStrm.kill();
	if (this.ctrlInStrm != null)
	    this.ctrlInStrm.kill();
	this.dataSock.close();
	this.ctrlSock.close();
    }

    /**
     * Set the receive buffer size of the RTP data channel.
     * This is only a hint to the implementation.  The actual implementation
     * may not be able to do anything to this.
     */
    public void setReceiveBufferSize( int size) throws IOException {
	this.dataSock.setReceiveBufferSize(size);
    }

    /**
     * Get the receive buffer size set on the RTP data channel.
     * Return -1 if the receive buffer size is not applicable for 
     * the implementation.
     */
    public int getReceiveBufferSize() {
	try {
	    return this.dataSock.getReceiveBufferSize();
	} catch (Exception e) {
	    return -1;
	}
    }

    /**
     * Set the send buffer size of the RTP data channel.
     * This is only a hint to the implementation.  The actual implementation
     * may not be able to do anything to this.
     */
    public void setSendBufferSize( int size) throws IOException {
	this.dataSock.setSendBufferSize(size);
    }

    /**
     * Get the send buffer size set on the RTP data channel.
     * Return -1 if the send buffer size is not applicable for 
     * the implementation.
     */
    public int getSendBufferSize() {
	try {
	    return this.dataSock.getSendBufferSize();
	} catch (Exception e) {
	    return -1;
	}
    }

    /**
     * Return the RTCP bandwidth fraction.  This value is used to
     * initialize the RTPManager.  Check RTPManager for more detauls.
     * Return -1 to use the default values.
     */
    public double getRTCPBandwidthFraction() {
	return -1;
    }

    /**
     * Return the RTCP sender bandwidth fraction.  This value is used to
     * initialize the RTPManager.  Check RTPManager for more detauls.
     * Return -1 to use the default values.
     */
    public double getRTCPSenderBandwidthFraction() {
	return -1;
    }


    /**
     * An inner class to implement an OutputDataStream based on UDP sockets.
     */
    class SockOutputStream implements OutputDataStream {

	DatagramSocket sock;
	InetAddress addr;
	int port;

	public SockOutputStream(DatagramSocket sock, InetAddress addr, int port) {
	    this.sock = sock;
	    this.addr = addr;
	    this.port = port;
	}

	public int write(byte data[], int offset, int len) {
	    try {
		this.sock.send(new DatagramPacket(data, offset, len, this.addr, this.port));
	    } catch (Exception e) {
		return -1;
	    }
	    return len;
	}
    }


    /**
     * An inner class to implement an PushSourceStream based on UDP sockets.
     */
    class SockInputStream extends Thread implements PushSourceStream {

	DatagramSocket sock;
	InetAddress addr;
	int port;
	boolean done = false;
	boolean dataRead = false;

	SourceTransferHandler sth = null;

	public SockInputStream(DatagramSocket sock, InetAddress addr, int port) {
	    this.sock = sock;
	    this.addr = addr;
	    this.port = port;
	}

	public int read(byte buffer[], int offset, int length) {
	    DatagramPacket p = new DatagramPacket(buffer, offset, length, this.addr, this.port);
	    
	    try
	    {
	    	this.sock.receive(p);
	    }
	    catch (IOException e)
	    {
	    	return -1;
	    }
	    synchronized (this)
	    {
	    	this.dataRead = true;
	    	notify();
	    }
	    
	    if (Switchboard.DEBUG_NETWORK)
	    	System.out.println("<<New packet>> IP: " + p.getAddress().getHostAddress() + " gives SSRC: " + ReadSSRC(buffer));
	    long ssrc = ReadSSRC(buffer);
	    if (!RTPSocketAdapter.this.previousSSRCSet.contains(ssrc))
	    {
	    	RTPSocketAdapter.this.previousSSRCSet.add(ssrc);
	    	RTPSocketAdapter.this.listener.newAssociation(p.getAddress().getHostAddress(), ssrc);
	    	if(Switchboard.DEBUG_NETWORK)
	    		System.out.println("SSRC list size: " + RTPSocketAdapter.this.previousSSRCSet.size());
	    }
	    //System.out.println("to port: ");
	    printBufferCraziness(buffer);
	    return p.getLength();
	}
	
	private void printBufferCraziness(byte buffer[])
	{
		//System.out.println(buffer[8] + " " + buffer[9] + " " + buffer[10] + " " + buffer[11]);
//		int line = 0;
//		System.out.println(buffer[0+ line] + " " + buffer[1 + line] + " " + buffer[2 + line] + " " + buffer[3 + line] + " " + buffer[4 + line] + " " + buffer[5 + line] + " " + buffer[6 + line] + " " + buffer[7 + line] + " " + 
//				buffer[8 + line] + " " + buffer[9 + line] + " " + buffer[10 + line] + " " + buffer[11 + line] + " " + buffer[12 + line] + " " + buffer[13 + line] + " " + buffer[14 + line] + " " + buffer[15 + line] + " " + 
//				buffer[16 + line] + " " + buffer[17 + line] + " " + buffer[18 + line] + " " + buffer[19 + line] + " " + buffer[20 + line] + " " + buffer[21 + line] + " " + buffer[22 + line] + " " + buffer[23 + line] + " " + 
//				buffer[24 + line] + " " + buffer[25 + line] + " " + buffer[26 + line] + " " + buffer[27 + line] + " " + buffer[28 + line] + " " + buffer[29 + line] + " " + buffer[30 + line] + " " + buffer[31 + line]);
//		line = 1;
//		System.out.println(buffer[0+ line] + " " + buffer[1 + line] + " " + buffer[2 + line] + " " + buffer[3 + line] + " " + buffer[4 + line] + " " + buffer[5 + line] + " " + buffer[6 + line] + " " + buffer[7 + line] + " " + 
//				buffer[8 + line] + " " + buffer[9 + line] + " " + buffer[10 + line] + " " + buffer[11 + line] + " " + buffer[12 + line] + " " + buffer[13 + line] + " " + buffer[14 + line] + " " + buffer[15 + line] + " " + 
//				buffer[16 + line] + " " + buffer[17 + line] + " " + buffer[18 + line] + " " + buffer[19 + line] + " " + buffer[20 + line] + " " + buffer[21 + line] + " " + buffer[22 + line] + " " + buffer[23 + line] + " " + 
//				buffer[24 + line] + " " + buffer[25 + line] + " " + buffer[26 + line] + " " + buffer[27 + line] + " " + buffer[28 + line] + " " + buffer[29 + line] + " " + buffer[30 + line] + " " + buffer[31 + line]);
//		line = 2;
//		System.out.println(buffer[0+ line] + " " + buffer[1 + line] + " " + buffer[2 + line] + " " + buffer[3 + line] + " " + buffer[4 + line] + " " + buffer[5 + line] + " " + buffer[6 + line] + " " + buffer[7 + line] + " " + 
//				buffer[8 + line] + " " + buffer[9 + line] + " " + buffer[10 + line] + " " + buffer[11 + line] + " " + buffer[12 + line] + " " + buffer[13 + line] + " " + buffer[14 + line] + " " + buffer[15 + line] + " " + 
//				buffer[16 + line] + " " + buffer[17 + line] + " " + buffer[18 + line] + " " + buffer[19 + line] + " " + buffer[20 + line] + " " + buffer[21 + line] + " " + buffer[22 + line] + " " + buffer[23 + line] + " " + 
//				buffer[24 + line] + " " + buffer[25 + line] + " " + buffer[26 + line] + " " + buffer[27 + line] + " " + buffer[28 + line] + " " + buffer[29 + line] + " " + buffer[30 + line] + " " + buffer[31 + line]);
//		line = 3;
//		System.out.println(buffer[0+ line] + " " + buffer[1 + line] + " " + buffer[2 + line] + " " + buffer[3 + line] + " " + buffer[4 + line] + " " + buffer[5 + line] + " " + buffer[6 + line] + " " + buffer[7 + line] + " " + 
//				buffer[8 + line] + " " + buffer[9 + line] + " " + buffer[10 + line] + " " + buffer[11 + line] + " " + buffer[12 + line] + " " + buffer[13 + line] + " " + buffer[14 + line] + " " + buffer[15 + line] + " " + 
//				buffer[16 + line] + " " + buffer[17 + line] + " " + buffer[18 + line] + " " + buffer[19 + line] + " " + buffer[20 + line] + " " + buffer[21 + line] + " " + buffer[22 + line] + " " + buffer[23 + line] + " " + 
//				buffer[24 + line] + " " + buffer[25 + line] + " " + buffer[26 + line] + " " + buffer[27 + line] + " " + buffer[28 + line] + " " + buffer[29 + line] + " " + buffer[30 + line] + " " + buffer[31 + line]);
	}
	
	private int ReadSSRC(byte buffer[])
	{
		byte[] ssrcBuffer = new byte[4];
		for (int index = 8; index < 12; index++)
		{
			ssrcBuffer[index-8] = buffer[index];
		}
		
		ByteBuffer byteBuffer = ByteBuffer.wrap(ssrcBuffer);
		return byteBuffer.getInt();
	}

	public synchronized void start() {
	    super.start();
	    if (this.sth != null) {
		this.dataRead = true;
		notify();
	    }
	}

	public synchronized void kill() {
	    this.done = true;
	    notify();
	}

	public int getMinimumTransferSize() {
	    return 2 * 1024;	// twice the MTU size, just to be safe.
	}

	public synchronized void setTransferHandler(SourceTransferHandler sth) {
	    this.sth = sth;
	    this.dataRead = true;
	    notify();
	}

	// Not applicable.
	public ContentDescriptor getContentDescriptor() {
	    return null;
	}

	// Not applicable.
	public long getContentLength() {
	    return LENGTH_UNKNOWN;
	}

	// Not applicable.
	public boolean endOfStream() {
	    return false;
	}

	// Not applicable.
	public Object[] getControls() {
	    return new Object[0];
	}

	// Not applicable.
	public Object getControl(String type) {
	    return null;
	}

	/**
	 * Loop and notify the transfer handler of new data.
	 */
	public void run() {
	    while (!this.done) {

		synchronized (this) {
		    while (!this.dataRead && !this.done) {
			try {
			    wait();
			} catch (InterruptedException e) { }
		    }
		    this.dataRead = false;
		}

		if (this.sth != null && !this.done) {
		    this.sth.transferData(this);
		}
	    }
	}
    }
}