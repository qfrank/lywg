package com.game.net.m28;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 *
 */
public class M2801 extends BasicMessage {

	public int userId;
	public int tradeTargetId;
	public String tradeTargetName;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.tradeTargetId = dis.readInt();
		this.tradeTargetName = dis.readUTF();
	}

	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt(this.userId);
		return (super.encode(dos.toByteArray()));
	}

}
