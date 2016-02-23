package com.game.net.m16;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


public class M1652 extends BasicMessage{

	public long mode;
	
	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.mode = dis.readByte();
	}

	@Override
	public String toString() {
		return "M1652{" +
				"mode=" + mode +
				'}';
	}
}
