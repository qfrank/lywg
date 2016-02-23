package com.game.net.m17;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 *
 * combine item
 *
 */
public class M1717 extends BasicMessage {

	public int result;
	public int _pos;
	public int _index;
	public int _new_index;

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

		dos.writeByte(this._pos);
		dos.writeShort(this._index);
		dos.writeShort(this._new_index);

		return (super.encode(dos.toByteArray()));
	}

}
