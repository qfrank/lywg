package com.game.net.m17;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 *
 * 
 *
 */
public class M1713 extends BasicMessage {

	public int result;
	public int _pos;
	public long _id;

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
		dos.writeByte(this._pos);//1
		dos.writeInt((int)this._id);//item id
		return (super.encode(dos.toByteArray()));
	}

}
