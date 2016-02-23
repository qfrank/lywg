package com.game.net.m13;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * fetch role list info
 *
 */
public class M1301 extends BasicMessage{
	
	public List<Map> avatars;
	
	public void decode(byte[] _arg1){
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);
		Map _local3;
		int _local2 = dis.readShort();
		this.avatars = new ArrayList();
		while (_local2-->0) {
			_local3 = new HashMap();
			_local3.put("id", dis.readUnsignedInt());
			_local3.put("status", dis.readShort());
			_local3.put("job", dis.readShort());
			_local3.put("sex", dis.readByte());
			_local3.put("nameresets", dis.readByte());
			_local3.put("level", dis.readShort());
			_local3.put("name", dis.readUTF());
			this.avatars.add(_local3);
		};
	}

	public byte[] encode(byte[] data){
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		return (super.encode(dos.toByteArray()));
	}
	
}
