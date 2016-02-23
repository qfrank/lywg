package com.game.net.m13;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 * get random role name
 *
 */
public class M1305 extends BasicMessage{

	public String randomName;

	public int sex = 1;

	public void decode(byte[] _arg1){
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);
		this.randomName = dis.readUTF();
	}

	public byte[] encode(byte[] data){
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt((int)this.sex);
		return (super.encode(dos.toByteArray()));
	}
	
}
