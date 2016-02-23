package com.game.net.m15;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 *
 *
 */
public class M1505 extends BasicMessage {

	public long result;
	public int id;
	public int x;
	public int y;
	public int mapId;
	public int portX;
	public int portY;
	public int _target;
	public int _x;
	public int _y;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.result = dis.readByte();
		this.id = dis.readShort();
		this.x = dis.readShort();
		this.y = dis.readShort();
	}

	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeShort(this._target);
		dos.writeShort(this._x);
		dos.writeShort(this._y);
		dos.writeShort(this.mapId);
		dos.writeShort(this.portX);
		dos.writeShort(this.portY);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M1505 [result=" + result + ", id=" + id + ", x=" + x + ", y=" + y + ", mapId=" + mapId + ", portX=" + portX + ", portY=" + portY + ", _target="
				+ _target + ", _x=" + _x + ", _y=" + _y + "]";
	}
	
	

}
