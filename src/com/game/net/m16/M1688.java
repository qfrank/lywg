package com.game.net.m16;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 * request to get robot params
 *
 */
public class M1688 extends BasicMessage{

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

		this._skill_alone = dis.readShort();
		this._skill_group = dis.readShort();
		this._open = dis.readInt()&0xffffffff;
		this._hp_signal = dis.readByte();
		this._hp_item = dis.readInt()&0xffffffff;
		this._hp_interval = dis.readShort();
		this._mp_signal = dis.readByte();
		this._mp_item = dis.readInt()&0xffffffff;
		this._mp_interval = dis.readShort();
		this._quit = dis.readByte();
		this._quit_item = dis.readInt()&0xffffffff;
		if (dis.available()>0){
			this._filterLevel = dis.readByte();
		}

	}

	@Override
	public String toString() {
		return "M1688{" +
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
