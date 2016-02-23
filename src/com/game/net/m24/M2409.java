package com.game.net.m24;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


public class M2409 extends BasicMessage{
	
	public int uiId;
	public int type;
	public int _uiId;
	public int _funId;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.uiId = dis.readByte();
		if (dis.available()>0){
			this.type = dis.readByte();
		};
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeByte(this._uiId);//02
		dos.writeShort(this._funId);//01
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M2409 [uiId=" + uiId + ", type=" + type + ", _uiId=" + _uiId + ", _funId=" + _funId + "]";
	}
	
	
}
