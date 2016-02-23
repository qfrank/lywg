package com.game.net.m24;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;


/**
 * NPC对话，
 * 领取灵兽押送任务，_npcId:0x2F49，_type:0x1
 * 灵兽交付，_npcId:0x2B2C，_type:0x1
 * 刷新灵兽等级，_npcId:0x2F49，_type:0x2
 * 领取战神，_npcId:0x2F49，_type:0x2E
 * 去幽冥宫，_npcId：0x2EE1，_type：0x1A
 * 去邪灵天界副本,_npcId：0x2EED，_type：0x0425
 * 
 * 回收50级武器,_npcId:0x2EEC,_type:0x044D
 * 回收50级衣服,_npcId:0x2EEC,_type:0x0457
 * 回收60级武器,_npcId:0x2EEC,_type:0x0835
 * 
 * 去卧龙村，_npcId:0x2EE1,_type:0x0B
 *
 */
public class M2402 extends BasicMessage{
	
	public int result;
	public int _npcId;
	public int _type;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		this.result = dis.readByte();
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt(this._npcId);
		dos.writeInt(this._type);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M2402{" +
				"result=" + result +
				", _npcId=" + _npcId +
				", _type=" + _type +
				'}';
	}
}
