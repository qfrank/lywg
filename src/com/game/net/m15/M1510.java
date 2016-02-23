package com.game.net.m15;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 * when item appeared on the ground
 *
 */
public class M1510 extends BasicMessage {

	public long id;
	public int type;
	public short x;
	public short y;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.id = dis.readUnsignedInt();
		this.type = dis.readInt();
		this.x = (short)dis.readShort();
		this.y = (short)dis.readShort();
	}

}
