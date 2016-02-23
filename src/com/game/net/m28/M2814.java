package com.game.net.m28;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 *
 *
 */
public class M2814 extends BasicMessage {

	public long requestId;
	public String requestName;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.requestId = dis.readUnsignedInt();
		this.requestName = dis.readUTF();
	}

}
