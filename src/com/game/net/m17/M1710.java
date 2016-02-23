package com.game.net.m17;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

public class M1710 extends BasicMessage {

	public int result;
	public int _id;
	public int _index;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.result = dis.readByte();
	}

	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt(this._id);
		dos.writeByte(this._index);
		return (super.encode(dos.toByteArray()));
	}

}
