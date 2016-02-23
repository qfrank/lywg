package com.game.net.m28;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 *
 */
public class M2800 extends BasicMessage {

	public int tradeTargetId;
	public int tradeRetrunId;
	public String tradeReturnName;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.tradeRetrunId = dis.readInt()&0xffffffff;
        this.tradeReturnName = dis.readUTF();
	}

	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt(this.tradeTargetId);
		return (super.encode(dos.toByteArray()));
	}

}
