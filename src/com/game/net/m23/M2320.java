package com.game.net.m23;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


public class M2320 extends BasicMessage {

	public long viptype;
	public int lefttime;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.viptype = dis.readByte();
		this.lefttime = dis.readInt();
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M2320{" +
				"viptype=" + viptype +
				", lefttime=" + lefttime +
				'}';
	}
}
