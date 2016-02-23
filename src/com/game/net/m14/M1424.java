package com.game.net.m14;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * getBuffNonskillData
 * 
 *
 */
public class M1424 extends BasicMessage {

	public long buffId;
	
    public long remain_hp_total;
    public int _type;

	public void decode(byte[] _arg1) {
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);

		this.buffId = dis.readUnsignedInt();
		this.remain_hp_total = dis.readUnsignedInt();
		
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt(this._type);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M1424 [buffId=" + buffId + ", remain_hp_total=" + remain_hp_total + ", _type=" + _type + "]";
	}

}
