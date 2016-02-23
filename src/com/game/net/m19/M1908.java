package com.game.net.m19;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;
import org.frkd.util.log.LoggerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 *
 * 
 *
 */
public class M1908 extends BasicMessage {

	private static final Logger logger = LoggerUtil.getLogger(M1908.class);

	public Map whisper;
	public int _id;
	public String _content;

	public void decode(byte[] _arg1) {
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);
		this.whisper = new HashMap();
		this.whisper.put("id", dis.readInt());
		this.whisper.put("name", dis.readUTF());
		this.whisper.put("sex", dis.readByte());
		this.whisper.put("type", dis.readByte());
		this.whisper.put("content", dis.readUTF());
		this.whisper.put("vip", dis.readByte());
		if (dis.available() > 0) {
			this.whisper.put("level", dis.readUnsignedByte());
		}
		;
	}

	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt(this._id);
		dos.writeUTF(this._content);
		return super.encode(dos.toByteArray());
	}

}
