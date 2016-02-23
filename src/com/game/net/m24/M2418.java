package com.game.net.m24;

import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 * 登录指定ID人物,返回成功后,初始化游戏信息
 *
 */
public class M2418 extends BasicMessage{
	
	public int _flag;//是否登录器客户端,如果是为1,否则送0
	
	public M2418() {
	}
	
	public M2418(int _flag) {
		this._flag = _flag;
	}

	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeByte(this._flag);
		return super.encode(dos.toByteArray());
	}
}
