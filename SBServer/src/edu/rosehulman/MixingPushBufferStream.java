package edu.rosehulman;

import java.io.IOException;
import java.util.Arrays;

import javax.media.Buffer;
import javax.media.Format;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.PushBufferStream;

public class MixingPushBufferStream implements PushBufferStream {
	
	private PushBufferStream[] mStreams = null;
	
	public static MergingBufferTransferHandler transferWrapper;

	public MixingPushBufferStream()
	{
		
	}
	
	public MixingPushBufferStream(PushBufferStream[] streams) {
		mStreams = streams;
	}

	@Override
	public Format getFormat() {
		// TODO Auto-generated method stub
		return mStreams[0].getFormat();
	}

	@Override
	public void read(Buffer inBuffer) throws IOException {
		Buffer buffer2 = new Buffer();
		mStreams[0].read(inBuffer);
		mStreams[1].read(buffer2);
		byte[] bufBuf1 = (byte[]) inBuffer.getData();
		byte[] bufBuf2 = (byte[]) buffer2.getData();
		for (int index = 100; index < Math.min(bufBuf1.length, bufBuf2.length); index++)
		{
			bufBuf2[index] += bufBuf1[index];
		}
		inBuffer.setData(bufBuf2);
//		System.out.println("what now");
	}

	@Override
	public void setTransferHandler(BufferTransferHandler transferHandler) {
		MergingBufferTransferHandler wrapper = MergingBufferTransferHandler.GetInstance();
		wrapper.AddHandler(transferHandler);
		for (PushBufferStream stream : mStreams)
		{
			if (true)
			{
				stream.setTransferHandler(wrapper);
				wrapper.triedToSet = false;
			}
		}
	}

	@Override
	public boolean endOfStream() {
		// TODO Auto-generated method stub
		return mStreams[0].endOfStream();
	}

	@Override
	public ContentDescriptor getContentDescriptor() {
		// TODO Auto-generated method stub
		return mStreams[0].getContentDescriptor();
	}

	@Override
	public long getContentLength() {
		// TODO Auto-generated method stub
		return mStreams[0].getContentLength();
	}

	@Override
	public Object getControl(String arg0) {
		// TODO Auto-generated method stub
		return mStreams[0].getControl(arg0);
	}

	@Override
	public Object[] getControls() {
		// TODO Auto-generated method stub
		return mStreams[0].getControls();
	}

}
