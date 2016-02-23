package com.game.net.m14;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

public class M1460 extends BasicMessage {

	public long lv;
	public long cExp;
	public long needExp;
	public long cAss;
	public long maxAss;
	public int breaks;
	public int up;
	public long assAdd;
	public long cue;

	public void decode(byte[] _arg1) {
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);

		this.lv = dis.readUnsignedInt();
		this.cExp = dis.readUnsignedInt();
		this.needExp = dis.readUnsignedInt();
		this.cAss = dis.readUnsignedInt();
		this.maxAss = dis.readUnsignedInt();
		this.breaks = dis.readShort();
		this.up = dis.readByte();
		this.assAdd = dis.readUnsignedInt();
		this.cue = dis.readUnsignedInt();
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M1460{" +
				"lv=" + lv +
				", cExp=" + cExp +
				", needExp=" + needExp +
				", cAss=" + cAss +
				", maxAss=" + maxAss +
				", breaks=" + breaks +
				", up=" + up +
				", assAdd=" + assAdd +
				", cue=" + cue +
				'}';
	}
}
