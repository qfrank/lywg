package com.game.net.m13;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 * create role
 *
 */
public class M1302 extends BasicMessage{

	public long result;

	public long id;

	public int _job = 1;//1表战士

	public int _sex = 1;//1表男

	public String _roleName;

	public void decode(byte[] _arg1){
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);
		this.result = dis.readByte();
		if(this.result == 0)
			this.id = dis.readInt();
	}

	public byte[] encode(byte[] data){
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeByte(this._job);
		dos.writeByte(this._sex);
		dos.writeUTF(this._roleName);
		return (super.encode(dos.toByteArray()));
	}
	
}
