package com.game.net.m28;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

public class M2813 extends BasicMessage {

	public int deniedId;
	public int denyingdId;
	public String denyingName;
	
	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.denyingdId = dis.readInt();
		this.denyingName = dis.readUTF();
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt(this.deniedId);
		return (super.encode(dos.toByteArray()));
	}

}
