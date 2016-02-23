package com.game.net.m15;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


public class M1503 extends BasicMessage{

	public int state;
	public long result;
	public long playerId;
	public int x;
	public int y;
	public int _x;
	public int _y;
	public int _state;//walk:1 , run:2
	
	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.playerId = dis.readUnsignedInt();
		this.x = dis.readShort();
		this.y = dis.readShort();
		this.state = dis.readByte();
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeShort(this._x);
		dos.writeShort(this._y);
		dos.writeByte(this._state);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M1503{" +
				"state=" + state +
				", result=" + result +
				", playerId=" + playerId +
				", x=" + x +
				", y=" + y +
				", _x=" + _x +
				", _y=" + _y +
				", _state=" + _state +
				'}';
	}
}
