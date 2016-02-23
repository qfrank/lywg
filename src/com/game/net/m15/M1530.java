package com.game.net.m15;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;
import wg.pojo.NpcData;

import java.util.ArrayList;
import java.util.List;

/**
 * fetch all npc data
 *
 */
public class M1530 extends BasicMessage{

	public List<NpcData> npcDataList;
	
	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		int _local2 = dis.readShort();
		this.npcDataList = new ArrayList();
		while (_local2-->0) {
			NpcData npcData = new NpcData();
			npcData.id = dis.readInt();
			npcData.type = dis.readInt();
			npcData.name = dis.readUTF().trim();
			npcData.scene = dis.readShort();
			npcData.x = dis.readShort();
			npcData.y = dis.readShort();
			npcData.isShowMiniMapName = dis.readByte();
			npcData.op = dis.readInt();
			npcDataList.add(npcData);
		};
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		return super.encode(dos.toByteArray());
	}


}
