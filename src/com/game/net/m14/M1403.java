package com.game.net.m14;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;
import wg.pojo.EquipInfo;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * onGetEquipData
 * 
 *
 */
public class M1403 extends BasicMessage {

	public JsonObject data;

	public int pId;
	public List<EquipInfo> equipArr;
	public long type;
	public int _id;
	public int flg;

	public void decode(byte[] _arg1) {
		InputByteArray dis = new InputByteArray(_arg1);

		int _local3;
		EquipInfo _local4;
		this.type = dis.readByte();
		if (this.type == 1) {
			String json = dis.readUTF();
			JsonParser jp = new JsonParser();
			JsonReader jr = new JsonReader(new StringReader(json.toLowerCase()));
			jr.setLenient(true);
			JsonElement je = jp.parse(jr);
			data = je.getAsJsonObject();
		} else {
			this.pId = dis.readInt();
			_local3 = dis.readShort();
			this.equipArr = new ArrayList();
			while (_local3-- > 0) {
				_local4 = new EquipInfo();
				_local4.id = dis.readInt();
				_local4.type = dis.readInt();
				_local4.part = dis.readByte();
				_local4.slevel = dis.readShort();
				_local4.snum = dis.readByte();
				_local4.luck = dis.readShort();
				_local4.bpatt = dis.readShort();
				_local4.bpdef = dis.readShort();
				_local4.bmatt = dis.readShort();
				_local4.bmdef = dis.readShort();
				_local4.btatt = dis.readShort();
				_local4.bind = dis.readByte();
				this.equipArr.add(_local4);
			}
		}

	}

	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		dos.writeInt(this._id);
		dos.writeByte(this.flg);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M1403 [data=" + data + ", pId=" + pId + ", equipArr=" + equipArr + ", type=" + type + ", _id=" + _id + ", flg=" + flg + "]";
	}

}
