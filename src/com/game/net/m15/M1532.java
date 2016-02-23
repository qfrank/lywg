package com.game.net.m15;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class M1532 extends BasicMessage {

	public List<Map> sceneArray;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		sceneArray = new ArrayList();
		Map _local3;
		int _local4;
		Map _local5;
		int _local2 = dis.readShort();
		while (_local2-- > 0) {
			_local3 = new HashMap();
			_local3.put("mapid", dis.readUnsignedInt());
			_local3.put("sourceid", dis.readUnsignedInt());
			_local3.put("map_name", dis.readUTF());
			_local4 = dis.readShort();
			if (_local4 > 0) {
				List portArray = new ArrayList();
				while (_local4-- > 0) {
					_local5 = new HashMap();
					_local5.put("id", dis.readUnsignedInt());
					_local5.put("x", dis.readShort());
					_local5.put("y", dis.readShort());
					_local5.put("next", dis.readUnsignedInt());
					_local5.put("jmapid", dis.readUnsignedInt());
					_local5.put("jx", dis.readShort());
					_local5.put("jy", dis.readShort());
					_local5.put("name", dis.readUTF());
					_local5.put("type", dis.readUTF());
					portArray.add(_local5);
				}
				_local3.put("portArray", portArray);
			}
			this.sceneArray.add(_local3);
		}
	}

}
