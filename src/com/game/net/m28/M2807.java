package com.game.net.m28;

import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 *
 */
public class M2807 extends BasicMessage {

	public int tradeIndex;
	public int packageIndex;

	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeByte(this.tradeIndex);
		dos.writeByte(this.packageIndex);
		return (super.encode(dos.toByteArray()));
	}

}
