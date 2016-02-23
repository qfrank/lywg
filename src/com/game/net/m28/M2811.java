package com.game.net.m28;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 * 被交易方确认交易或取消交易
 *
 */
public class M2811 extends BasicMessage {

	public long result;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.result = dis.readByte();
	}

}
