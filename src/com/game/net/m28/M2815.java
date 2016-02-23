package com.game.net.m28;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 *
 *
 */
public class M2815 extends BasicMessage {

	public int requestNum;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.requestNum = dis.readByte();
	}

	@Override
	public String toString() {
		return "M2815{" +
				"requestNum=" + requestNum +
				'}';
	}
}
