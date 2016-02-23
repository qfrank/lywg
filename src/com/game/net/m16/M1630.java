package com.game.net.m16;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

public class M1630 extends BasicMessage{

	public long playerId;
	public int skillId;
	public int sx;
	public int sy;
	public int targetId;
	public int tx;
	public int ty;
	public int delay_time;
	public int _skillId;
	public int _x;
	public int _y;
	public long _targetId;
	public int _tx;
	public int _ty;
	public int _delay_time;

	public void decode(byte[] _arg1){
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);
		this.playerId = dis.readInt() & 0xffffffff;
		this.skillId = dis.readShort();
		this.sx = dis.readShort();
		this.sy = dis.readShort();
		this.targetId = dis.readInt() & 0xffffffff;
		this.tx = dis.readShort();
		this.ty = dis.readShort();
	}

	public byte[] encode(byte[] data){
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeShort(this._skillId);//0x12 means hide self skill
		dos.writeShort(this._x);
		dos.writeShort(this._y);
		dos.writeInt((int)this._targetId);
		if ((((((((((((((((((((((this._skillId == 2)) || ((this._skillId == 8)))) || ((this._skillId == 9)))) || ((this._skillId == 10)))) || ((this._skillId == 17)))) || ((this._skillId == 18)))) || ((this._skillId == 21)))) || ((this._skillId == 24)))) || ((this._skillId == 30)))) || ((this._skillId == 31)))) || ((this._skillId == 89)))){
			this._ty = 0;
		};
		dos.writeShort(this._tx);
		dos.writeShort(this._ty);
		dos.writeShort(this._delay_time);
		return super.encode(dos.toByteArray());
	}
}
