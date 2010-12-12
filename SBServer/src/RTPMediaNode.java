import java.io.IOException;
import java.net.InetAddress;

import javax.media.CannotRealizeException;
import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaException;
import javax.media.MediaLocator;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.RealizeCompleteEvent;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;
import javax.media.rtp.SessionManagerException;

public class RTPMediaNode {
	public static final Format[] FORMATS = new Format[] { new AudioFormat(AudioFormat.MPEG_RTP) };
	public static final ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(ContentDescriptor.RAW_RTP);
	
	private MediaLocator mediaLocator = null;
	private DataSink dataSink = null;
	private Processor mediaProcessor = null;
	
	public void setDataSource(DataSource sendStreamSource) throws IOException, MediaException {
		ProcessorModel model = new ProcessorModel(sendStreamSource, FORMATS, CONTENT_DESCRIPTOR);
		mediaProcessor = Manager.createRealizedProcessor(model);//(sendStreamSource);
		if (Switchboard.DEBUG) System.out.println("... media processed ...");
		dataSink = Manager.createDataSink(mediaProcessor.getDataOutput(), mediaLocator);
		if (Switchboard.DEBUG) System.out.println("... target linked ...");
	}
	
	public void setDataSource(Processor p) throws IOException, MediaException {
		mediaProcessor = p;
		dataSink = Manager.createDataSink(mediaProcessor.getDataOutput(), mediaLocator);
		if (Switchboard.DEBUG) System.out.println("... target linked and media processed.");
	}
	
	public void setMediaLocator(MediaLocator ml)
	{
		mediaLocator = ml;
		if (Switchboard.DEBUG) System.out.println("... successful.");
	}
	
	public void startStreaming() throws IOException {
		mediaProcessor.start();
		
		dataSink.open();
		dataSink.start();
		if (Switchboard.DEBUG) System.out.println("... sending.");
	}
	
	public void startPlayer()
	{
		try {
			
			Player p = Manager.createRealizedPlayer(mediaLocator);
			p.addControllerListener(new ControllerListener(){

				@Override
				public void controllerUpdate(ControllerEvent arg0) {
					if (arg0 instanceof RealizeCompleteEvent){
						System.out.println("We have received data, and we are playing... =)");
					}
					
				}
				
			});
			if (Switchboard.DEBUG) System.out.println("... attached to port ...");
			p.start();
			if (Switchboard.DEBUG) System.out.println("... playing.");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop() throws IOException{
		if(Switchboard.DEBUG) System.out.println("... stopping ...");
		mediaProcessor.stop();
		dataSink.stop();
		dataSink.close();
	}
}
