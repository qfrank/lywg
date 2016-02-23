package com.game.net.m14;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.frkd.io.InputByteArray;
import org.frkd.io.OutputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * user info
 *
 */
public class M1400 extends BasicMessage{
	
	public Map data;
	
	@Override
	public void decode(byte[] data) {
		InputByteArray dis = new InputByteArray(data);
		int _local2 = dis.readByte();
		this.data = new HashMap();
		if (_local2 == 1){
			JsonParser jp = new JsonParser();
			String json = dis.readUTF();
			JsonReader jr = new JsonReader(new StringReader(json.toLowerCase()));
			jr.setLenient(true);
			JsonElement je = jp.parse(jr);
			JsonObject jsonObject = je.getAsJsonObject();
			this.data.put("sceneid", jsonObject.get("sceneid").getAsInt());
			this.data.put("x", jsonObject.get("x").getAsShort());
			this.data.put("y", jsonObject.get("y").getAsShort());
            if(jsonObject.has("clothtid")){
			    this.data.put("clothtid", jsonObject.get("clothtid").getAsShort());//衣服
            }
            if(jsonObject.has("swordid")){
			    this.data.put("swordid", jsonObject.get("swordid").getAsShort());//武器
            }
			this.data.put("name", jsonObject.get("name").getAsString());
			this.data.put("faction", jsonObject.get("faction")!=null?jsonObject.get("faction").getAsString():"");
			this.data.put("group", jsonObject.get("group").getAsByte());
			this.data.put("title", jsonObject.get("title").getAsLong());
			this.data.put("job", jsonObject.get("job").getAsByte());
			this.data.put("sex", jsonObject.get("sex").getAsByte());
			this.data.put("level", jsonObject.get("level").getAsByte());
			this.data.put("exp", jsonObject.get("exp").getAsLong());
			this.data.put("needexp", jsonObject.get("needexp").getAsLong());
			this.data.put("pk", jsonObject.get("pk").getAsLong());
			this.data.put("minac", jsonObject.get("minac").getAsLong());
			this.data.put("maxac", jsonObject.get("maxac").getAsLong());
			this.data.put("minmac", jsonObject.get("minmac").getAsLong());
			this.data.put("maxmac", jsonObject.get("maxmac").getAsLong());
			this.data.put("mindc", jsonObject.get("mindc").getAsLong());
			this.data.put("maxdc", jsonObject.get("maxdc").getAsLong());
			this.data.put("minmc", jsonObject.get("minmc").getAsLong());
			this.data.put("maxmc", jsonObject.get("maxmc").getAsLong());
			this.data.put("minsc", jsonObject.get("minsc").getAsLong());
			this.data.put("maxsc", jsonObject.get("maxsc").getAsLong());
			this.data.put("hp", jsonObject.get("hp").getAsLong());
			this.data.put("maxhp", jsonObject.get("maxhp").getAsLong());
			this.data.put("mp", jsonObject.get("mp").getAsLong());
			this.data.put("maxmp", jsonObject.get("maxmp").getAsLong());
			this.data.put("duck", jsonObject.get("duck").getAsShort());
			this.data.put("accurate", jsonObject.get("accurate").getAsShort());
			this.data.put("lucky", jsonObject.get("lucky").getAsByte());
			this.data.put("soul", jsonObject.get("soul")!=null?jsonObject.get("soul").getAsByte():null);
			this.data.put("gold", jsonObject.get("gold").getAsLong());
			this.data.put("bgold", jsonObject.get("bgold").getAsLong());
			this.data.put("ingot", jsonObject.get("ingot").getAsLong());
			this.data.put("bingot", jsonObject.get("bingot").getAsLong());
		} else {
			this.data.put("sceneid", dis.readInt());
			this.data.put("x", dis.readShort());
			this.data.put("y", dis.readShort());
			this.data.put("clothtid", dis.readShort());
			this.data.put("swordid", dis.readShort());
			this.data.put("name", dis.readUTF());
			this.data.put("faction", dis.readUTF());
			this.data.put("group", dis.readByte());
			this.data.put("title", dis.readUnsignedInt());
			this.data.put("job", dis.readByte());
			this.data.put("sex", dis.readByte());
			this.data.put("level", dis.readByte());
			this.data.put("exp", dis.readUnsignedInt());
			this.data.put("needexp", dis.readUnsignedInt());
			this.data.put("pk", dis.readUnsignedInt());
			this.data.put("minac", dis.readUnsignedInt());
			this.data.put("maxac", dis.readUnsignedInt());
			this.data.put("minmac", dis.readUnsignedInt());
			this.data.put("maxmac", dis.readUnsignedInt());
			this.data.put("mindc", dis.readUnsignedInt());
			this.data.put("maxdc", dis.readUnsignedInt());
			this.data.put("minmc", dis.readUnsignedInt());
			this.data.put("maxmc", dis.readUnsignedInt());
			this.data.put("minsc", dis.readUnsignedInt());
			this.data.put("maxsc", dis.readUnsignedInt());
			this.data.put("hp", dis.readUnsignedInt());
			this.data.put("maxhp", dis.readUnsignedInt());
			this.data.put("mp", dis.readUnsignedInt());
			this.data.put("maxmp", dis.readUnsignedInt());
			this.data.put("duck", dis.readShort());
			this.data.put("accurate", dis.readShort());
			this.data.put("lucky", dis.readByte());
			this.data.put("soul", dis.readByte());
			this.data.put("gold", dis.readUnsignedInt());
			this.data.put("bgold", dis.readUnsignedInt());
			this.data.put("ingot", dis.readUnsignedInt());
			this.data.put("bingot", dis.readUnsignedInt());
		};
	}
	
	@Override
	public byte[] encode(byte[] data) {
		OutputByteArray dos = new OutputByteArray();
		dos.writeByte(0);
		return super.encode(dos.toByteArray());
	}

	@Override
	public String toString() {
		return "M1400{" +
				"data=" + data +
				'}';
	}
}
