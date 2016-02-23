package com.game.net.m24;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

/**
 * 使用小飞鞋 最多50次，同屏目的坐标情况下可能不会收到M2411的回复，用M1504代之
 *
 */
public class M2411 extends BasicMessage{

	public int result;
	public int _sceneId;
	public int _x;
	public int _y;
	
	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		
		result = dis.readByte();
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeShort(_sceneId);
		dos.writeShort(_x);
		dos.writeShort(_y);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M2411{" +
				"result=" + result +
				", _sceneId=" + _sceneId +
				", _x=" + _x +
				", _y=" + _y +
				'}';
	}
}
