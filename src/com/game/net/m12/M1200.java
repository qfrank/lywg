package com.game.net.m12;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


public class M1200 extends BasicMessage {
	public long result;
	public int result_1 = -1;
	public String _accId;
	public String _key;
	public String src;
	public String sub_src;
	public int flags;
	public int time;
	
	public void decode(byte[] _arg1) {
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);
		this.result = dis.readByte();
		if (dis.available() > 0) {
			this.result_1 = dis.readByte();
		}
	}

	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeShort((((((((7 + 8) + this._accId.length()) + this._key
				.length()) + this.src.length()) + this.sub_src.length()) + 4) + 4));
		dos.writeInt(message);
		dos.writeByte(0);
		dos.writeUTF(this._accId);
		dos.writeUTF(this._key);
		dos.writeUTF(this.src);
		dos.writeUTF(this.sub_src);
		dos.writeInt(this.flags);
		dos.writeInt(this.time);
		return (dos.toByteArray());
	}
}
