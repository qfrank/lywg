package com.game.net.m15;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * when item hided on the ground 
 *
 */
public class M1511 extends BasicMessage {

	public long id;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.id = dis.readUnsignedInt();
	}

}
