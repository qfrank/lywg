package com.game.net.m12;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;
import org.frkd.util.Global;


public class M1201 extends BasicMessage {
	
	public long result;
	public String logic;
	public int code = 0;
	public String _id;

	public void decode(byte[] _arg1) {
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);
		this.result = dis.readByte();
		this.logic = dis.readUTF();
		
		if(dis.available()>=4){
			this.code = dis.readInt();
		};
		Global.setData("1201_code", this.code);
	}

	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeShort(((7 + 2) + this._id.length()));
		dos.writeInt(message);
		dos.writeByte(0);
		dos.writeUTF(this._id);
		return (dos.toByteArray());
	}
}
