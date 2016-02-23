package com.game.net.m19;

import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * onUpdateUserInfoHandler
 * 
 *
 */
public class M1930 extends BasicMessage {
	
	/**
	 * type=1时，content可能为：绑定金币减少 50000
	 * type=10时，content可能为：刷新成功,当前灵兽等级(神兽小草)
	 */
	public long type;
	
	public String content;

	public void decode(byte[] _arg1) {
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);

		this.type = dis.readByte();
        this.content = dis.readUTF();

	}

	@Override
	public String toString() {
		return "M1930 [type=" + type + ", content=" + content + "]";
	}
	
	

}
