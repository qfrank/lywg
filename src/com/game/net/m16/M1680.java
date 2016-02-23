package com.game.net.m16;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


public class M1680 extends BasicMessage{

	public long result;
	public int  _skill_alone;
	public int  _skill_group;
	public int  _open;
	public int  _hp_signal;
	public int  _hp_item;
	public int  _hp_interval;
	public int  _mp_signal;
	public int  _mp_item;
	public int  _mp_interval;
	public int  _quit;
	public int  _quit_item;
	public int  _filterLevel;
	
	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.result = dis.readByte();
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		if (this._hp_interval < 500){
			this._hp_interval = 500;
		};
		if (this._mp_interval < 500){
			this._mp_interval = 500;
		};
		dos.writeByte(0);
		dos.writeShort(this._skill_alone);
		dos.writeShort(this._skill_group);
		dos.writeInt(this._open);
		dos.writeByte(this._hp_signal);
		dos.writeInt(this._hp_item);
		dos.writeShort(this._hp_interval);
		dos.writeByte(this._mp_signal);
		dos.writeInt(this._mp_item);
		dos.writeShort(this._mp_interval);
		dos.writeByte(this._quit);
		dos.writeInt(this._quit_item);
		dos.writeByte(this._filterLevel);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M1680{" +
				"result=" + result +
				", _skill_alone=" + _skill_alone +
				", _skill_group=" + _skill_group +
				", _open=" + _open +
				", _hp_signal=" + _hp_signal +
				", _hp_item=" + _hp_item +
				", _hp_interval=" + _hp_interval +
				", _mp_signal=" + _mp_signal +
				", _mp_item=" + _mp_item +
				", _mp_interval=" + _mp_interval +
				", _quit=" + _quit +
				", _quit_item=" + _quit_item +
				", _filterLevel=" + _filterLevel +
				'}';
	}
}
