package com.game.net.m17;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;
import wg.pojo.PackageItem;

import java.util.ArrayList;
import java.util.List;


/**
 * request to get package info
 * 
 *
 */
public class M1732 extends BasicMessage {

	public int _pos;
	
	public byte pos;
	
	public int total;
	
	public List<PackageItem> items;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		
		this.items = new ArrayList();
		pos = dis.readByte();
		total = dis.readUnsignedByte();
		long _local2 = dis.readUnsignedShort();
		while(_local2-->0){
			PackageItem _local4 = new PackageItem();
			_local4.id = dis.readInt();
			_local4.type = dis.readInt();
			_local4.index = dis.readUnsignedByte();
			_local4.num = dis.readByte();
			_local4.slevel = dis.readShort();
			_local4.snum = dis.readByte();
			_local4.luck = dis.readShort();
			_local4.bpatt = dis.readShort();
			_local4.bpdef = dis.readShort();
			_local4.bmatt = dis.readShort();
			_local4.bmdef = dis.readShort();
			_local4.btatt = dis.readShort();
			_local4.bind = dis.readByte();
			items.add(_local4);
		}
	}

	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeByte(this._pos);
		return (super.encode(dos.toByteArray()));
	}

	@Override
	public String toString() {
		return "M1732{" +
				"_pos=" + _pos +
				", pos=" + pos +
				", total=" + total +
				", items=" + items +
				'}';
	}
}
