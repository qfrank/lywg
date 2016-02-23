package com.game.net.m15;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * new player appeared
 */
public class M1501 extends BasicMessage {

	public long result;

	public int type;

	public Map info;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		int _local2 = dis.readByte();

		this.info = new HashMap();
		this.info.put("id", dis.readUnsignedInt());
		this.info.put("x", dis.readShort());
		this.info.put("y", dis.readShort());
		this.info.put("angle", dis.readByte());

		_local2 = dis.readByte();
		if (_local2 == 0) {
			this.info.put("state", dis.readByte());
			this.info.put("buff", dis.readShort());
			this.info.put("hp", dis.readUnsignedInt());
			this.info.put("maxhp", dis.readUnsignedInt());
			this.info.put("level", dis.readByte());
			this.info.put("sex", dis.readByte());
			this.info.put("job", dis.readByte());
			this.info.put("name", dis.readUTF());
			this.info.put("clothtid", dis.readShort());
			this.info.put("swordid", dis.readShort());
			this.info.put("group", dis.readByte());
			this.info.put("faction", dis.readUTF());
			this.info.put("title", dis.readUnsignedInt());
			this.info.put("guildid", dis.readUnsignedInt());
			this.info.put("color", dis.readByte());
			if (dis.available() > 0) {
				int _local3 = dis.readShort();
				List _local4 = new ArrayList();
				while (_local3-- > 0) {
					Map _local5 = new HashMap();
					_local5.put("id", dis.readByte());
					_local5.put("title", dis.readUTF());
					_local4.add(_local5);
				}
				this.info.put("titleary", _local4);
			}
			this.info.put("mp1", dis.readUnsignedInt());
			this.info.put("maxmp1", dis.readUnsignedInt());
		} else {
			if (_local2 == 1) {
				this.info.put("state", dis.readByte());
				this.info.put("buff", dis.readShort());
				this.info.put("hp", dis.readUnsignedInt());
				this.info.put("maxhp", dis.readUnsignedInt());
				this.info.put("name", dis.readUTF());
				this.info.put("clothtid", dis.readShort());
				if (dis.available() > 0) {
					this.info.put("level", dis.readShort());
				}
				if (dis.available() > 0) {
					this.info.put("warcamp", dis.readByte());
				}
			} else {
				this.info.put("clothtid", dis.readShort());
				this.info.put("name", dis.readUTF());
			}
		}
	}

	@Override
	public String toString() {
		return "M1501{" +
				"result=" + result +
				", type=" + type +
				", info=" + info +
				'}';
	}
}
