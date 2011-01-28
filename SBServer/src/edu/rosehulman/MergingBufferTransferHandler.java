package edu.rosehulman;

import java.io.IOException;
import java.util.ArrayList;

import javax.media.Buffer;
import javax.media.protocol.BufferTransferHandler;
import javax.media.protocol.PushBufferStream;

public class MergingBufferTransferHandler implements BufferTransferHandler {

	private static MergingBufferTransferHandler instance = new MergingBufferTransferHandler();
	private ArrayList<BufferTransferHandler> authenticHandlerArray;
	public boolean triedToSet = true;
	ArrayList<Byte> megaBufBuf = new ArrayList<Byte>();
	
	private MergingBufferTransferHandler()
	{
		authenticHandlerArray = new ArrayList<BufferTransferHandler>();
	}
	
	public static MergingBufferTransferHandler GetInstance()
	{
		return instance;
	}
	
	public void AddHandler(BufferTransferHandler transferHandler)
	{
		authenticHandlerArray.add(transferHandler);
	}
	
	@Override
	public void transferData(PushBufferStream bufferStream) {
//		for (BufferTransferHandler authenticHandler : authenticHandlerArray)
//		{
//			authenticHandler.transferData(bufferStream);
//		}
		authenticHandlerArray.get(0).transferData(MergingDataSource.ultimateStream);
	}

}
