package com.game.net.m13;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 * delete role
 *
 */
public class M1303 extends BasicMessage{

	public long result;
	public int _id;
	public String _password;

	public void decode(byte[] _arg1){
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);
		this.result = dis.readByte();
	}

	public byte[] encode(byte[] data){
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt(_id);
		dos.writeUTF(this._password);
		return (super.encode(dos.toByteArray()));
	}
	
}
