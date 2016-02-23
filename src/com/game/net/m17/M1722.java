package com.game.net.m17;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 * sell target id item
 * 
 *
 */
public class M1722 extends BasicMessage {

	public int result;   
	public int _id;      
	public long _num= 1;
	
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
		dos.writeInt(this._id);
        dos.writeShort((short)this._num);
		return (super.encode(dos.toByteArray()));
	}

}
