package com.game.net.m17;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;
import wg.pojo.PackageItem;

/**
 * when item info in package is updating,we will get this packet,e.g. after
 * using random stone
 * 
 *
 */
public class M1711 extends BasicMessage {

	public long pos;
	public int index;
	public long result;

	public PackageItem item;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.pos = dis.readByte();
		this.index = dis.readByte() & 0xff;
		this.result = dis.readByte();
		if (this.result == 1) {//means some items added to package
			item = new PackageItem();
			item.index = this.index;
			item.id = dis.readInt();
			item.type = dis.readInt();
			item.num = dis.readShort();
			item.slevel = dis.readShort();
			item.luck = dis.readShort();
			item.bpatt = dis.readShort();
			item.bpdef = dis.readShort();
			item.bmatt = dis.readShort();
			item.bmdef = dis.readShort();
			item.btatt = dis.readShort();
			item.bind = dis.readByte();
			item.snum = dis.readByte();
		}
	}

	@Override
	public String toString() {
		return "M1711{" +
				"pos=" + pos +
				", index=" + index +
				", result=" + result +
				", item=" + item +
				'}';
	}
}
