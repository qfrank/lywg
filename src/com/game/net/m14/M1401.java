package com.game.net.m14;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import org.frkd.io.InputByteArray;
import org.frkd.net.socket.protocol.BasicMessage;

import java.io.StringReader;

/**
 * onUpdateUserInfoHandler
 *
 */
public class M1401 extends BasicMessage {
	
	public JsonObject data;

	public void decode(byte[] _arg1) {
		InputByteArray dis = new InputByteArray(_arg1);
		dis.skipBytes(1);

		String json = dis.readUTF();
		JsonParser jp = new JsonParser();
		JsonReader jr = new JsonReader(new StringReader(json.toLowerCase()));
		jr.setLenient(true);
		JsonElement je = jp.parse(jr);
		data = je.getAsJsonObject();
	}

	@Override
	public String toString() {
		return "M1401{" +
				"data=" + data +
				'}';
	}
}
