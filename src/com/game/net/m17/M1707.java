package com.game.net.m17;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 * buy item from shop
 *
 */
public class M1707 extends BasicMessage {
	
	public long result;
	public int _goodId;
	public int _num = 1;

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
		dos.writeInt(this._goodId);//when value is 0x46,it means buy random stone
		dos.writeShort(this._num);
		return (super.encode(dos.toByteArray()));
	}

	@Override
	public String toString() {
		return "M1707{" +
				"result=" + result +
				", _goodId=" + _goodId +
				", _num=" + _num +
				'}';
	}
}
