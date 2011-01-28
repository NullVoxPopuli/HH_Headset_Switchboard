package edu.rosehulman;

import java.io.IOException;
import java.util.Vector;

import javax.media.Time;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.protocol.SourceStream;


public class MergingDataSource extends PushBufferDataSource {
  
    DataSource[] sources;
    SourceStream[] streams = null;
    Object [] controls = null;
    public static MixingPushBufferStream ultimateStream;
    
  /**
   * Constructor 
   */
  public MergingDataSource(DataSource[] sources) {

    this.sources = sources;
  }

  /**
   * Returns the content-type of the merged streams. If all streams are RAW, 
   * the returned content-type is RAW. Otherwise, the return content-type is 
   * MIXED.
   */
  public String getContentType() {
    
      if (sources.length == 1)
	  return sources[0].getContentType();

    boolean isRaw = true;

    for (int index = 0; index < sources.length; index++) {
      if (!sources[index].getContentType().equals(ContentDescriptor.RAW)) {
	isRaw = false;
	break;
      }
    }
     
    if (isRaw)
      return ContentDescriptor.RAW;
    else if (sources.length == 1)
      return sources[0].getContentType();
    else
      return ContentDescriptor.MIXED;
  }
  
  public void connect() throws IOException {

    for (int i = 0; i < sources.length; i++)
      sources[i].connect();
  }
  
  public void disconnect() {

    for (int i = 0; i < sources.length; i++)
      sources[i].disconnect();
  }
  
  public void start() throws IOException {
    
    for (int i = 0; i < sources.length; i++)
      sources[i].start();
  }
  
  public void stop() throws IOException {
    
    for (int i = 0; i < sources.length; i++)
      sources[i].stop();
  }  

  /**
   * Obtain the collection of objects that
   * control the object that implements this interface.
   * <p>
   *
   * If no controls are supported, a zero length
   * array is returned.
   *
   * @return the collection of object controls
   */
  public Object[] getControls() {
      if (controls == null) {
	  Vector vcontrols = new Vector(1);
	  for (int i = 0; i < sources.length; i++) {
	      Object [] cs = (Object[]) sources[i].getControls();
	      if (cs.length > 0) {
		  for (int j = 0; j < cs.length; j++) {
		      vcontrols.addElement(cs[j]);
		  }
	      }
	  }
	  controls = new Object[vcontrols.size()];
	  for (int c = 0; c < vcontrols.size(); c++)
	      controls[c] = vcontrols.elementAt(c);
      }
	  
      return controls;
  }
  
  /**
   * Obtain the object that implements the specified
   * <code>Class</code> or <code>Interface</code>
   * The full class or interface name must be used.
   * <p>
   * 
   * If the control is not supported then <code>null</code>
   * is returned.
   *
   * @return the object that implements the control,
   * or <code>null</code>.
   */
  public Object getControl(String controlType) {
      try {
          Class  cls = Class.forName(controlType);
          Object cs[] = getControls();
          for (int i = 0; i < cs.length; i++) {
	      if (cls.isInstance(cs[i]))
		  return cs[i];
          }
          return null;
	  
      } catch (Exception e) {   // no such controlType or such control
	  return null;
      }      
  }

  /**
   * Get the duration of the media represented
   * by this object.
   * The value returned is the media's duration
   * when played at the default rate.
   * If the duration can't be determined  (for example, the media object is presenting live
   * video)  <CODE>getDuration</CODE> returns <CODE>DURATION_UNKNOWN</CODE>.
   *
   * @return A <CODE>Time</CODE> object representing the duration or DURATION_UNKNOWN.
   */
  public Time getDuration() {
    
    Time longest = new Time(0);

    for (int i = 0; i < sources.length; i++) {
      Time sourceDuration = sources[i].getDuration();
      if (sourceDuration.getSeconds() > longest.getSeconds())
	longest = sourceDuration;
    }

    return longest;
  }

@Override
public PushBufferStream[] getStreams()
{
	// Make a new PushBufferStream that will iteratively grab data from each connected stream and create a new, single, buffer
	// that will contain the mixed data (some data may be left out)
	
	//Gather all the streams
	PushBufferStream[] streams = new PushBufferStream[sources.length];
	int index = 0;
	for (DataSource source : sources)
	{
		PushBufferStream[] sourceStreams = ((PushBufferDataSource) source).getStreams();
		streams[index] = sourceStreams[0];
		index++;
	}
	
	//Make and return the mixing push buffer stream
	MixingPushBufferStream[] returnMixingPushBufferStreamArray = new MixingPushBufferStream[1];
	ultimateStream = new MixingPushBufferStream(streams);
	returnMixingPushBufferStreamArray[0] = ultimateStream;
	
	return returnMixingPushBufferStreamArray;
}
  
}