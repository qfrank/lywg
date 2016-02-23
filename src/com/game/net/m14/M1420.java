package com.game.net.m14;

import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;
import wg.pojo.BuffInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * buffId:302
 * buffId:135
 * buffId:101
 * 
 *
 */
public class M1420 extends BasicMessage {

	public List<BuffInfo> buffInfoList;

	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		dis.skipBytes(1);
		int _local2 = dis.readShort();
		this.buffInfoList = new ArrayList();
		while (_local2-- > 0) {
			BuffInfo _local3 = new BuffInfo();
			_local3.buffId = dis.readUnsignedInt();
			_local3.times = dis.readUnsignedInt();
			_local3.rest_time = dis.readUnsignedInt();
			_local3.remain_times = dis.readUnsignedInt();
			_local3.flag = dis.readUnsignedInt();
			this.buffInfoList.add(_local3);

		}
	}

	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M1420{" +
				"buffInfoList=" + buffInfoList +
				'}';
	}
}
