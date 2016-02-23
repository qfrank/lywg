package com.game.net.m13;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 * login target id role 
 *
 */
public class M1304 extends BasicMessage{
	
	public long result;
	public long _id;
	public String _password;// 6 security code(number type)
	
	public M1304() {
	}
	
	public M1304(long _id, String _password) {
		this._id = _id;
		this._password = _password;
	}

	public void decode(byte[] _arg1){
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);
		this.result = dis.readByte();
	}

	public byte[] encode(byte[] data){
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt((int)this._id);
		dos.writeUTF(this._password);
		return (super.encode(dos.toByteArray()));
	}
	
}
