import java.io.IOException;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaException;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.RealizeCompleteEvent;
import javax.media.format.AudioFormat;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;

public class RTPMediaNode implements ControllerListener {
	public static final Format[] FORMATS = new Format[] { new AudioFormat(AudioFormat.MPEG_RTP) };
	public static final ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(ContentDescriptor.RAW_RTP);
	
	private MediaLocator mediaLocator = null;
	private DataSink dataSink = null;
	private Processor mediaProcessor = null;
	private Player p;
	
	public void setDataSource(DataSource sendStreamSource) throws IOException, MediaException {
		ProcessorModel model = new ProcessorModel(sendStreamSource, FORMATS, CONTENT_DESCRIPTOR);
		mediaProcessor = Manager.createRealizedProcessor(model);//(sendStreamSource);
		if (Main.Debug) System.out.println("... media processed ...");
		dataSink = Manager.createDataSink(mediaProcessor.getDataOutput(), mediaLocator);
		if (Main.Debug) System.out.println("... target linked ...");
	}
	
	public void setDataSource(Processor p) throws IOException, MediaException {
		mediaProcessor = p;
		dataSink = Manager.createDataSink(mediaProcessor.getDataOutput(), mediaLocator);
		if (Main.Debug) System.out.println("... target linked and media processed.");
	}
	
	public void setMediaLocator(MediaLocator ml)
	{
		mediaLocator = ml;
		if (Main.Debug) System.out.println("... successful.");
	}
	
	public void startStreaming() throws IOException {
		mediaProcessor.start();
		
		dataSink.open();
		Object a = ((Object) dataSink.getControl("PacketSizeControl"));
		dataSink.start();
		
		if (Main.Debug) System.out.println("... sending.");
	}
	
	public void initializePlayer()
	{
		try {
			if (Main.Debug) System.out.println("Initializing player and attaching to port ...");
			p = Manager.createPlayer(mediaLocator);
			p.addControllerListener(this);
			if (Main.Debug) System.out.println("... player initialized, waiting for data.");
			p.realize();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void controllerUpdate(ControllerEvent event) {
		// TODO Auto-generated method stub
		if (event instanceof RealizeCompleteEvent)
		{
			System.out.println("Data recieved, playing stream.");
			p.start();
		}
	}
}
