package com.game.net.m15;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


public class M1514 extends BasicMessage {

	public int state;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.state = dis.readByte();
	}

	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		return (super.encode(dos.toByteArray()));
	}

	@Override
	public String toString() {
		return "M1514{" +
				"state=" + state +
				'}';
	}
}
